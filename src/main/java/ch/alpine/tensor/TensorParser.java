// code by jph
package ch.alpine.tensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import ch.alpine.tensor.io.StringScalar;

/* package */ class TensorParser {
  private static final char COMMA = ',';
  // ---
  public static final TensorParser DEFAULT = new TensorParser(Scalars::fromString);
  /***************************************************/
  private final Function<String, Scalar> function;

  /** @param function that parses a string to a scalar
   * @throws Exception if given function is null */
  public TensorParser(Function<String, Scalar> function) {
    this.function = Objects.requireNonNull(function);
  }

  /** @param string for instance "{1, 2[m], {3+4*I, 5.67}}"
   * @return */
  public Tensor parse(String string) {
    int opening = string.indexOf(Tensor.OPENING_BRACKET); // first non-whitespace character should be "{"
    if (0 <= opening && string.substring(0, opening).isBlank()) {
      int closing = string.lastIndexOf(Tensor.CLOSING_BRACKET);
      if (opening < closing && string.substring(closing + 1).isBlank()) {
        List<Tensor> list = new ArrayList<>();
        int level = 1; // track nesting with "{" and "}"
        int beg = opening + 1;
        int index = opening + 1;
        for (; index <= closing; ++index) {
          final char chr = string.charAt(index);
          if (chr == Tensor.OPENING_BRACKET)
            ++level;
          boolean isComma = chr == COMMA;
          boolean is_last = chr == Tensor.CLOSING_BRACKET;
          if (level == 1 && (isComma || is_last)) {
            String entry = string.substring(beg, index).strip(); // trim is required
            if (!entry.isEmpty() || !is_last || 0 < list.size())
              list.add(parse(entry));
            beg = index + 1;
          }
          if (is_last) {
            --level;
            if (level < 1)
              break;
          }
        }
        return index == closing //
            ? Unprotect.using(list)
            : StringScalar.of(string);
      }
    }
    return function.apply(string);
  }
}
