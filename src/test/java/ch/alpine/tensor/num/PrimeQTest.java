// code by jph
package ch.alpine.tensor.num;

import java.lang.reflect.Modifier;
import java.math.BigInteger;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PrimeQTest extends TestCase {
  public void testPrimes() {
    for (Tensor _x : ResourceData.of("/number/primes.vector")) {
      RationalScalar x = (RationalScalar) _x;
      assertTrue(x.numerator().isProbablePrime(100));
      assertTrue(PrimeQ.of(x));
      assertEquals(PrimeQ.require(x), x);
    }
  }

  public void testPrimeFail() {
    AssertFail.of(() -> PrimeQ.require(BigInteger.TEN));
    AssertFail.of(() -> PrimeQ.require(Pi.HALF));
    AssertFail.of(() -> PrimeQ.require(RationalScalar.of(2, 3)));
    AssertFail.of(() -> PrimeQ.require(RationalScalar.of(200, 1)));
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(PrimeQ.class.getModifiers()));
  }
}
