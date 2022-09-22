// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class BigIntegerMathTest {
  @Test
  void testZeroOne() {
    assertEquals(BigIntegerMath.sqrt(BigInteger.ZERO).get(), BigInteger.ZERO);
    assertEquals(BigIntegerMath.sqrt(BigInteger.ONE).get(), BigInteger.ONE);
  }

  @Test
  void testBigInteger() {
    Optional<BigInteger> sqrt = BigIntegerMath.sqrt(new BigInteger("21065681101554527729739161805139578084"));
    assertEquals(sqrt.get(), new BigInteger("4589736495873649578"));
  }

  @Test
  void testBigIntegerFail() {
    Optional<BigInteger> optional = BigIntegerMath.sqrt(new BigInteger("21065681101554527729739161805139578083"));
    assertFalse(optional.isPresent());
  }

  @Test
  void testNegativeFail() {
    Optional<BigInteger> optional = BigIntegerMath.sqrt(new BigInteger("-16"));
    assertFalse(optional.isPresent());
  }
}
