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
    if (0 <= value)
      return value;
    throw new IllegalArgumentException(Integer.toString(value));
  }

  /** @param value strictly positive
   * @return value
   * @throws Exception if given value is negative or zero */
  public static int requirePositive(int value) {
    if (0 < value)
      return value;
    throw new IllegalArgumentException(Integer.toString(value));
  }

  /** intended for use in reductive algorithms, for instance in the addition of
   * two vectors, where the length of the two vectors have to be equal.
   * 
   * inspired by junit's assertEquals
   * 
   * @param lhs
   * @param rhs
   * @return the identical value of lhs and rhs
   * @throws Exception if lhs and rhs are not equal */
  public static int requireEquals(int lhs, int rhs) {
    if (lhs == rhs)
      return lhs;
    throw new IllegalArgumentException(Integer.toString(lhs) + " != " + Integer.toString(rhs));
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
    if (0 < value)
      return 0 == (value & (value - 1));
    throw new IllegalArgumentException(Integer.toString(value));
  }
}
