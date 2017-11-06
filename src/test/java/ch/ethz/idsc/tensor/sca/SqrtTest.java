// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.StringScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SqrtTest extends TestCase {
  public void testNegative() {
    Scalar n2 = RealScalar.of(-2);
    Scalar sr = Sqrt.FUNCTION.apply(n2);
    assertEquals(Rationalize.of(AbsSquared.FUNCTION.apply(sr), 10000), RealScalar.of(2));
    assertEquals(Rationalize.of(sr.multiply(sr), 10000), n2);
  }

  public void testMixingTemplates() {
    {
      Scalar tensor = RealScalar.of(-2);
      Sqrt.of(tensor);
      Scalar scalar = Sqrt.of(tensor);
      scalar.zero();
    }
    {
      RationalScalar tensor = (RationalScalar) RationalScalar.of(-2, 3);
      Sqrt.of(tensor);
      Scalar scalar = Sqrt.of(tensor);
      scalar.zero();
    }
  }

  public void testComplex() {
    Scalar scalar = ComplexScalar.of(0, 2);
    Scalar root = Sqrt.FUNCTION.apply(scalar);
    Scalar res = ComplexScalar.of(1, 1);
    assertEquals(Chop._12.of(root.subtract(res)), RealScalar.ZERO);
  }

  public void testZero() {
    assertEquals(RealScalar.ZERO, Sqrt.FUNCTION.apply(RealScalar.ZERO));
  }

  public void testRational() {
    assertEquals(Sqrt.of(RationalScalar.of(16, 25)).toString(), "4/5");
    assertEquals(Sqrt.of(RationalScalar.of(-16, 25)).toString(), "4/5*I");
  }

  public void testReal() {
    assertEquals(((RealScalar) RealScalar.of(16 / 25.)).sqrt(), Scalars.fromString("4/5"));
    assertEquals(((RealScalar) RealScalar.of(-16 / 25.)).sqrt(), Scalars.fromString("4/5*I"));
  }

  public void testTensor() {
    Tensor r = Sqrt.of(Tensors.vector(1, 4, 9, 16));
    assertEquals(r, Tensors.vector(1, 2, 3, 4));
  }

  public void testInfty() {
    Scalar res = Sqrt.of(DoubleScalar.POSITIVE_INFINITY);
    assertEquals(res, DoubleScalar.POSITIVE_INFINITY);
  }

  public void testInftyNeg() {
    Scalar res = Sqrt.of(DoubleScalar.NEGATIVE_INFINITY);
    assertEquals(res, ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY));
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(9, "m^2");
    Scalar qs2 = Quantity.of(3, "m");
    assertEquals(Sqrt.of(qs1), qs2);
  }

  public void testFail() {
    try {
      Sqrt.of(StringScalar.of("string"));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}