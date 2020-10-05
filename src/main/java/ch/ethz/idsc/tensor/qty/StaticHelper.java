// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.regex.Pattern;

/* package */ enum StaticHelper {
  ;
  /** atomic unit may consist of roman letters in lower case a-z,
   * upper case A-Z, as well as the underscore character '_' */
  private static final Pattern PATTERN = Pattern.compile("[%A-Z_a-z]+");

  /** @param key atomic unit expression, for instance "kg"
   * @return given key
   * @throws Exception if given key is not an atomic unit expression */
  public static String requireAtomic(String key) {
    if (!PATTERN.matcher(key).matches())
      throw new IllegalArgumentException(key);
    return key;
  }
}
