// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;

class StringScalarTest {
  @Test
  void testStrings() {
    Tensor a = StringScalar.of("asd");
    Tensor b = StringScalar.of("x");
    Tensor d = Tensors.of(a, b, a, b);
    assertEquals(d.length(), 4);
    assertEquals(d.toString(), "{asd, x, asd, x}");
  }

  @Test
  void testHashCode() {
    assertEquals( //
        StringScalar.of("asd").hashCode(), //
        StringScalar.of("asd").hashCode());
  }

  @Test
  void testEquals() {
    assertEquals(StringScalar.of("3.14"), StringScalar.of("3.14"));
    assertFalse(StringScalar.of("3.14").equals(null));
    assertFalse(StringScalar.of("3.14").equals(StringScalar.of("3.141")));
    assertFalse(StringScalar.of("3.14").equals(DoubleScalar.of(3.14)));
  }

  @Test
  void testCurrentStandard() {
    String string = "{Hello, World}";
    assertTrue(string.equals(Tensors.fromString(string).toString()));
  }

  @Test
  void testFailOp() {
    assertThrows(Throw.class, () -> StringScalar.of("asd").reciprocal());
    assertThrows(Throw.class, () -> StringScalar.of("asd").negate());
    assertThrows(Throw.class, () -> StringScalar.of("asd").number());
    assertThrows(Throw.class, () -> StringScalar.of("asd").multiply(RealScalar.ONE));
    assertThrows(Throw.class, () -> StringScalar.of("asd").add(RealScalar.ONE));
  }

  @Test
  void testMultiplyFail() {
    assertThrows(Throw.class, () -> ComplexScalar.I.multiply(StringScalar.of("asd")));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> StringScalar.of(null));
  }

  @Test
  void testOneFail() {
    Scalar scalar = StringScalar.of("abc");
    assertThrows(Throw.class, () -> scalar.zero());
    assertThrows(Throw.class, () -> scalar.one());
  }

  @Test
  void testExact() {
    assertTrue(ExactScalarQ.of(StringScalar.of("abc")));
  }
}
