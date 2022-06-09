// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.io.ResourceData;

class PrimeQTest {
  @Test
  public void testPrimes() {
    for (Tensor _x : ResourceData.of("/io/primes.vector")) {
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
    assertThrows(IllegalArgumentException.class, () -> PrimeQ.require(BigInteger.TEN));
    assertThrows(TensorRuntimeException.class, () -> PrimeQ.require(Pi.HALF));
    assertThrows(TensorRuntimeException.class, () -> PrimeQ.require(RationalScalar.of(2, 3)));
    assertThrows(IllegalArgumentException.class, () -> PrimeQ.require(RationalScalar.of(200, 1)));
  }

  @Test
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(PrimeQ.class.getModifiers()));
  }
}
