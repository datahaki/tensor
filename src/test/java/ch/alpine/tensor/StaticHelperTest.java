// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class StaticHelperTest {
  @Test
  void testZeroOne() {
    assertEquals(StaticHelper.sqrt(BigInteger.ZERO).orElseThrow(), BigInteger.ZERO);
    assertEquals(StaticHelper.sqrt(BigInteger.ONE).orElseThrow(), BigInteger.ONE);
  }

  @Test
  void testBigInteger() {
    Optional<BigInteger> sqrt = StaticHelper.sqrt(new BigInteger("21065681101554527729739161805139578084"));
    assertEquals(sqrt.orElseThrow(), new BigInteger("4589736495873649578"));
  }

  @Test
  void testBigIntegerFail1() {
    Optional<BigInteger> optional = StaticHelper.sqrt(new BigInteger("21065681101554527729739161805139578083"));
    assertFalse(optional.isPresent());
  }

  @Test
  void testBigIntegerFail2() {
    Optional<BigInteger> optional = StaticHelper.sqrt(new BigInteger("21065681101554527729739161805139578085"));
    assertFalse(optional.isPresent());
  }

  @Test
  void testNegativeFail() {
    assertThrows(Exception.class, () -> StaticHelper.sqrtApproximation(new BigInteger("-16")));
  }

  @Test
  void testStringLastIndex() {
    String string = "{ { } } ";
    assertEquals(string.lastIndexOf(Tensor.CLOSING_BRACKET, 2), -1);
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
