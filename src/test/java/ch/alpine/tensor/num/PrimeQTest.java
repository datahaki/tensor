// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.usr.AssertFail;

public class PrimeQTest {
  @Test
  public void testPrimes() {
    for (Tensor _x : ResourceData.of("/number/primes.vector")) {
      RationalScalar x = (RationalScalar) _x;
      assertTrue(x.numerator().isProbablePrime(100));
      assertTrue(PrimeQ.of(x));
      assertEquals(PrimeQ.require(x), x);
    }
  }

  @Test
  public void testMathematica() {
    BigInteger bigInteger = BigInteger.valueOf(3371149052237L);
    assertTrue(PrimeQ.of(bigInteger));
  }

  @Test
  public void testPrimeFail() {
    AssertFail.of(() -> PrimeQ.require(BigInteger.TEN));
    AssertFail.of(() -> PrimeQ.require(Pi.HALF));
    AssertFail.of(() -> PrimeQ.require(RationalScalar.of(2, 3)));
    AssertFail.of(() -> PrimeQ.require(RationalScalar.of(200, 1)));
  }

  @Test
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(PrimeQ.class.getModifiers()));
  }
}
