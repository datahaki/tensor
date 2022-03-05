// code by jph
package ch.alpine.tensor.sca.pow;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Rationalize;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SqrtTest extends TestCase {
  public void testNegative() {
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(RealScalar.of(10000));
    Scalar n2 = RealScalar.of(-2);
    Scalar sr = Sqrt.FUNCTION.apply(n2);
    assertEquals(suo.apply(AbsSquared.FUNCTION.apply(sr)), RealScalar.of(2));
    assertEquals(suo.apply(sr.multiply(sr)), n2);
  }

  public void testMixingTemplates() {
    {
      Scalar tensor = RealScalar.of(-2);
      Sqrt.of(tensor);
      Scalar scalar = Sqrt.of(tensor);
      scalar.zero();
    }
    {
      Scalar tensor = RationalScalar.of(-2, 3);
      Sqrt.of(tensor);
      Scalar scalar = Sqrt.of(tensor);
      scalar.zero();
    }
  }

  public void testComplex() {
    Scalar scalar = ComplexScalar.of(0, 2);
    Scalar root = Sqrt.FUNCTION.apply(scalar);
    Scalar res = ComplexScalar.of(1, 1);
    Tolerance.CHOP.requireClose(root, res);
  }

  public void testZero() {
    assertEquals(RealScalar.ZERO, Sqrt.FUNCTION.apply(RealScalar.ZERO));
  }

  public void testRational() {
    assertEquals(Sqrt.of(RationalScalar.of(16, 25)).toString(), "4/5");
    Scalar scalar = Sqrt.of(RationalScalar.of(-16, 25));
    assertTrue(scalar instanceof ComplexScalar);
    assertEquals(scalar.toString(), "4/5*I");
  }

  public void testReal() {
    assertEquals(Sqrt.of(RationalScalar.of(-16, 25)).toString(), "4/5*I");
    assertEquals(Sqrt.FUNCTION.apply(RealScalar.of(16 / 25.)), Scalars.fromString("4/5"));
    assertEquals(Sqrt.FUNCTION.apply(RealScalar.of(-16 / 25.)), Scalars.fromString("4/5*I"));
  }

  public void testTensor() {
    Tensor vector = Sqrt.of(Tensors.vector(1, 4, 9, 16));
    assertEquals(vector, Tensors.vector(1, 2, 3, 4));
  }

  public void testPositiveInfty() {
    assertEquals( //
        Sqrt.of(DoubleScalar.POSITIVE_INFINITY), //
        DoubleScalar.POSITIVE_INFINITY);
  }

  public void testNegativeInfty() {
    assertEquals( //
        Sqrt.of(DoubleScalar.NEGATIVE_INFINITY), //
        ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY));
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(9, "m^2");
    Scalar qs2 = Quantity.of(3, "m");
    assertEquals(Sqrt.of(qs1), qs2);
  }

  public void testQuantity2() {
    Scalar qs1 = Quantity.of(9, "m*s^2");
    Scalar qs2 = Quantity.of(3, "m^1/2*s");
    assertEquals(Sqrt.of(qs1), qs2);
  }

  public void testFail() {
    Scalar scalar = StringScalar.of("string");
    AssertFail.of(() -> Sqrt.of(scalar));
  }
}
