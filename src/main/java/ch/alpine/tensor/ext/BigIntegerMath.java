// code by jph
package ch.alpine.tensor.ext;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

/** implementation is standalone */
public enum BigIntegerMath {
  ;
  /** @param value
   * @return exact root of value
   * @throws IllegalArgumentException if value is not a square number */
  public static Optional<BigInteger> sqrt(BigInteger value) {
    BigInteger root = sqrtApproximation(value);
    if (root.multiply(root).equals(value))
      return Optional.of(root);
    return Optional.empty();
  }

  /** @param value
   * @return approximation to sqrt of value, exact root if input value is square number */
  // https://gist.github.com/JochemKuijpers/cd1ad9ec23d6d90959c549de5892d6cb
  private static BigInteger sqrtApproximation(BigInteger value) {
    BigInteger a = BigInteger.ONE;
    BigInteger b = value.shiftRight(5).add(BigInteger.valueOf(8));
    while (0 <= b.compareTo(a)) {
      BigInteger mid = a.add(b).shiftRight(1);
      if (0 < mid.multiply(mid).compareTo(value))
        b = mid.subtract(BigInteger.ONE);
      else
        a = mid.add(BigInteger.ONE);
    }
    return a.subtract(BigInteger.ONE);
  }

  /** implementation by Maarten Bodewes from
   * https://stackoverflow.com/questions/75378517/create-random-biginteger-using-randomgenerator-introduced-in-java-17
   * 
   * Mimics the {@link BigInteger#BigInteger(int, Random)} function using a
   * {@link RandomGenerator} instance.
   * 
   * @param numBits maximum bitLength of the new BigInteger.
   * @param rnd source of randomness to be used in computing the new
   * BigInteger.
   * @throws IllegalArgumentException {@code numBits} is negative.
   * @see #bitLength() */
  public static BigInteger random(int numBits, RandomGenerator randomGenerator) {
    if (numBits < 0)
      throw new IllegalArgumentException("numBits must be non-negative");
    if (numBits == 0)
      return BigInteger.ZERO;
    int length = (numBits + Byte.SIZE - 1) / Byte.SIZE;
    // mask bits that we need the value of to 1, the others - if any -- will be set to zero
    byte[] magnitude = new byte[length];
    randomGenerator.nextBytes(magnitude);
    byte bitMask = (byte) ((1 << ((numBits - 1) % Byte.SIZE + 1)) - 1);
    magnitude[0] &= bitMask; // magnitude is in big endian
    return new BigInteger(1, magnitude);
  }
}
