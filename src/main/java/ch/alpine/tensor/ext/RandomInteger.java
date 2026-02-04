// code by jph
package ch.alpine.tensor.ext;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

/** implementation is standalone
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomInteger.html">RandomInteger</a> */
public enum RandomInteger {
  ;
  /** @param max_inclusive
   * @param randomGenerator
   * @return random BigInteger from 0, 1, ..., max_inclusive */
  public static BigInteger of(BigInteger max_inclusive, RandomGenerator randomGenerator) {
    BigInteger bigInteger;
    do {
      bigInteger = random(max_inclusive.bitLength(), randomGenerator);
    } while (0 < bigInteger.compareTo(max_inclusive));
    return bigInteger;
  }

  /** @param max_inclusive
   * @return */
  public static BigInteger of(BigInteger max_inclusive) {
    return of(max_inclusive, ThreadLocalRandom.current());
  }

  /** implementation by Maarten Bodewes from
   * https://stackoverflow.com/questions/75378517/create-random-biginteger-using-randomgenerator-introduced-in-java-17
   * 
   * Mimics the {@link BigInteger#BigInteger(int, Random)} function using a
   * {@link RandomGenerator} instance.
   * 
   * @param numBits maximum bitLength of the new BigInteger.
   * @param randomGenerator source of randomness to be used in computing the new
   * BigInteger.
   * @throws IllegalArgumentException {@code numBits} is negative.
   * @see #bitLength() */
  /* package */ static BigInteger random(int numBits, RandomGenerator randomGenerator) {
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
