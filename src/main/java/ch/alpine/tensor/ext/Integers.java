// code by jph
package ch.alpine.tensor.ext;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Integers.html">Integers</a> */
public enum Integers {
  ;
  /** @param value non-negative
   * @return value
   * @throws Exception if given value is negative */
  public static int requirePositiveOrZero(int value) {
    if (value < 0)
      throw new IllegalArgumentException(Integer.toString(value));
    return value;
  }

  /** @param value strictly positive
   * @return value
   * @throws Exception if given value is negative or zero */
  public static int requirePositive(int value) {
    if (value <= 0)
      throw new IllegalArgumentException(Integer.toString(value));
    return value;
  }

  /** @param value
   * @return whether given value is an even number */
  public static boolean isEven(int value) {
    return (value & 1) == 0;
  }

  /** @param value strictly positive
   * @return true if value is a power of 2, e.g. 1, 2, 4, 8, 16, etc.
   * @throws Exception if given value is negative or zero */
  public static boolean isPowerOf2(int value) {
    if (value <= 0)
      throw new IllegalArgumentException(Integer.toString(value));
    return 0 == (value & (value - 1));
  }
}
