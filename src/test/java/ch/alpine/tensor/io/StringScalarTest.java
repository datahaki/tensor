// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StringScalarTest extends TestCase {
  public void testStrings() {
    Tensor a = StringScalar.of("asd");
    Tensor b = StringScalar.of("x");
    Tensor d = Tensors.of(a, b, a, b);
    assertEquals(d.length(), 4);
    assertEquals(d.toString(), "{asd, x, asd, x}");
  }

  public void testHashCode() {
    assertEquals( //
        StringScalar.of("asd").hashCode(), //
        StringScalar.of("asd").hashCode());
  }

  public void testEquals() {
    assertEquals(StringScalar.of("3.14"), StringScalar.of("3.14"));
    assertFalse(StringScalar.of("3.14").equals(null));
    assertFalse(StringScalar.of("3.14").equals(StringScalar.of("3.141")));
    assertFalse(StringScalar.of("3.14").equals(DoubleScalar.of(3.14)));
  }

  public void testCurrentStandard() {
    String string = "{Hello, World}";
    assertTrue(string.equals(Tensors.fromString(string).toString()));
  }

  public void testFailOp() {
    AssertFail.of(() -> StringScalar.of("asd").reciprocal());
    AssertFail.of(() -> StringScalar.of("asd").negate());
    AssertFail.of(() -> StringScalar.of("asd").number());
    AssertFail.of(() -> StringScalar.of("asd").multiply(RealScalar.ONE));
    AssertFail.of(() -> StringScalar.of("asd").add(RealScalar.ONE));
  }

  public void testMultiplyFail() {
    AssertFail.of(() -> ComplexScalar.I.multiply(StringScalar.of("asd")));
  }

  public void testNullFail() {
    AssertFail.of(() -> StringScalar.of(null));
  }

  public void testOneFail() {
    Scalar scalar = StringScalar.of("abc");
    AssertFail.of(() -> scalar.zero());
    AssertFail.of(() -> scalar.one());
  }

  public void testNonExact() {
    assertFalse(ExactScalarQ.of(StringScalar.of("abc")));
  }
}
