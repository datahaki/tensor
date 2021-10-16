// code by jph
package ch.alpine.tensor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import ch.alpine.tensor.io.StringScalar;

/* package */ enum TensorParser {
  ;
  private static final char COMMA = ',';

  /** If the appearance of the brackets { and } as well as the commas
   * do not represent a nested list structure, the given string is returned
   * as a {@link StringScalar}.
   * 
   * @param string for instance "{1, 2[m], {3+4*I, 5.67}}"
   * @param function for instance {@link Scalars#fromString(String)}
   * @return tensor interpretation of given string */
  public static Tensor of(String string, Function<String, Scalar> function) {
    try {
      Deque<List<Tensor>> deque = new ArrayDeque<>();
      int head = 0;
      int tail = 0;
      List<Tensor> last = null;
      boolean opening_disallow = false;
      boolean comma_stripBlank = false;
      boolean closing_forceInsert = false;
      boolean closing_allowInsertNonBlank = false;
      for (char chr : string.toCharArray()) {
        switch (chr) {
        case Tensor.OPENING_BRACKET: {
          if (opening_disallow)
            throw new IllegalStateException();
          if (deque.isEmpty() && !string.substring(head, tail).isBlank()) // "a{1, 2}"
            throw new IllegalStateException();
          deque.push(new ArrayList<>());
          closing_forceInsert = false;
          closing_allowInsertNonBlank = true;
          head = tail + 1;
          break;
        }
        case COMMA: {
          String substring = string.substring(head, tail);
          if (comma_stripBlank) {
            if (!substring.isBlank()) // "{{}a, 1}"
              throw new IllegalStateException();
          } else
            deque.peek().add(function.apply(substring.strip()));
          opening_disallow = false;
          comma_stripBlank = false;
          closing_forceInsert = true;
          closing_allowInsertNonBlank = false;
          head = tail + 1;
          break;
        }
        case Tensor.CLOSING_BRACKET: {
          String substring = string.substring(head, tail); // any expression before '}'
          if (closing_forceInsert) { // insert expression between ',' and '}'
            deque.peek().add(function.apply(substring.strip()));
            closing_forceInsert = false;
          } else {
            if (!substring.isBlank()) { // otherwise "{}"
              if (closing_allowInsertNonBlank) // "{1}"
                deque.peek().add(function.apply(substring.strip()));
              else // "{{0}1}"
                throw new IllegalStateException();
            }
          }
          last = deque.pop();
          opening_disallow = true;
          comma_stripBlank = !deque.isEmpty();
          if (comma_stripBlank)
            deque.peek().add(Unprotect.using(last));
          closing_allowInsertNonBlank = false;
          head = tail + 1;
          break;
        }
        default:
          break;
        }
        ++tail;
      }
      if (deque.isEmpty()) {
        if (Objects.isNull(last)) // "123"
          return function.apply(string);
        if (string.substring(head, tail).isBlank()) // "{1, 2} "
          return Unprotect.using(last);
      }
    } catch (Exception exception) {
      // ---
    }
    return StringScalar.of(string);
  }
}
