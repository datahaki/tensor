// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.regex.Pattern;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.num.BinaryPower;
import ch.ethz.idsc.tensor.num.ScalarProduct;

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

  public static final BinaryPower<Scalar> BINARY_POWER = //
      new BinaryPower<>(new ScalarProduct(Quaternion.ONE));
}
