// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SurdTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator surd = Surd.of(3);
    Scalar scalar = CubeRoot.FUNCTION.apply(RealScalar.of(27));
    Chop._12.requireClose(scalar, RealScalar.of(3));
    Chop._12.requireClose(scalar, surd.apply(RealScalar.of(27)));
  }

  public void testQuantity() {
    Scalar input = Quantity.of(2, "m^3");
    Scalar scalar = Surd.of(3).apply(input);
    Chop._12.requireClose(scalar, Quantity.of(1.2599210498948732, "m"));
    Chop._12.requireClose(Times.of(scalar, scalar, scalar), input);
  }

  public void testNegative() {
    Scalar input = Quantity.of(-2, "m^3");
    Scalar scalar = Surd.of(3).apply(input);
    Chop._12.requireClose(scalar, Quantity.of(-1.2599210498948731648, "m"));
  }

  public void testZero() {
    for (int exp = 1; exp < 10; ++exp) {
      Scalar scalar = Surd.of(exp).apply(RealScalar.ZERO);
      ExactScalarQ.require(scalar);
      assertEquals(scalar, RealScalar.ZERO);
    }
  }

  public void testNegativeExp() {
    Scalar scalar = Surd.of(-1).apply(RealScalar.of(4));
    ExactScalarQ.require(scalar);
    assertEquals(scalar, RationalScalar.of(1, 4));
    assertEquals(Surd.of(-2).apply(RealScalar.of(4)), RationalScalar.of(1, 2));
  }

  public void testZeroExpFail() {
    AssertFail.of(() -> Surd.of(0));
  }

  public void testOf() {
    Tensor tensor = Tensors.vector(-27, -8, -1, 0, 1, 8, 27).map(Surd.of(3));
    assertEquals(tensor, Range.of(-3, 4));
  }

  public void testComplexFail() {
    Scalar scalar = ComplexScalar.of(12, 23);
    AssertFail.of(() -> Surd.of(3).apply(scalar));
  }
}
