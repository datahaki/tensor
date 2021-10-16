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

  public static Tensor of(String string) {
    return of(string, Scalars::fromString);
  }

  public static Tensor of(String string, Function<String, Scalar> function) {
    try {
      Deque<List<Tensor>> deque = new ArrayDeque<>();
      int head = 0;
      int tail = 0;
      List<Tensor> last = null;
      boolean ignoreBlank = false;
      boolean noEmptyClosure = false;
      boolean noCommaClosure = false;
      for (char chr : string.toCharArray()) {
        if (chr == Tensor.OPENING_BRACKET) {
          if (deque.isEmpty() && (Objects.nonNull(last) || !string.substring(head, tail).isBlank()))
            // return StringScalar.of(string);
            throw new IllegalStateException();
          deque.push(new ArrayList<>());
          head = tail + 1;
          noEmptyClosure = false;
          noCommaClosure = true;
        } else //
        if (chr == COMMA) {
          String substring = string.substring(head, tail);
          if (ignoreBlank) {
            ignoreBlank = false;
            if (!substring.isBlank())
              // return StringScalar.of(string);
              throw new IllegalArgumentException();
          } else
            deque.peek().add(function.apply(substring.strip())); // TODO only strip a single white space
          head = tail + 1;
          noEmptyClosure = true;
          noCommaClosure = false;
        } else //
        if (chr == Tensor.CLOSING_BRACKET) {
          if (deque.isEmpty())
            return StringScalar.of(string);
          String substring = string.substring(head, tail);
          if (noEmptyClosure) {
            deque.peek().add(function.apply(substring.strip()));
            noEmptyClosure = false;
          } else //
          if (!substring.isBlank())
            if (noCommaClosure)
              deque.peek().add(function.apply(substring.strip()));
            else
              // return StringScalar.of(string);
              throw new IllegalStateException();
          last = deque.pop();
          if (!deque.isEmpty()) {
            deque.peek().add(Unprotect.using(last));
            ignoreBlank = true;
          }
          head = tail + 1;
        }
        ++tail;
      }
      if (Objects.isNull(last))
        return function.apply(string);
      if (deque.isEmpty() && //
          string.substring(head, tail).isBlank())
        return Unprotect.using(last);
      // throw exception
    } catch (Exception exception) {
      // ---
    }
    return StringScalar.of(string);
  }
}
