// code by jph
package ch.alpine.tensor.sca.pow;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SurdTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator surd = Surd.of(3);
    Scalar scalar = CubeRoot.FUNCTION.apply(RealScalar.of(27));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(3));
    Tolerance.CHOP.requireClose(scalar, surd.apply(RealScalar.of(27)));
  }

  public void testQuantity() {
    Scalar input = Quantity.of(2, "m^3");
    Scalar scalar = Surd.of(3).apply(input);
    Tolerance.CHOP.requireClose(scalar, Quantity.of(1.2599210498948732, "m"));
    Tolerance.CHOP.requireClose(Times.of(scalar, scalar, scalar), input);
  }

  public void testNegative() {
    Scalar input = Quantity.of(-2, "m^3");
    Scalar scalar = Surd.of(3).apply(input);
    Tolerance.CHOP.requireClose(scalar, Quantity.of(-1.2599210498948731648, "m"));
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

  public void testOf() {
    ScalarUnaryOperator suo = Surd.of(3);
    Tensor tensor = Tensors.vector(-27, -8, -1, 0, 1, 8, 27).map(suo);
    assertEquals(tensor, Range.of(-3, 4));
    assertEquals(suo.toString(), "Surd[3]");
  }

  public void testNegativeN() {
    ScalarUnaryOperator suo = Surd.of(-3);
    assertEquals(suo.toString(), "Surd[-3]");
    Tolerance.CHOP.requireClose(suo.apply(RationalScalar.of(1, 27)), RealScalar.of(3));
  }

  public void testNNegativeTwo() {
    ScalarUnaryOperator suo = Surd.of(-2);
    assertEquals(suo.toString(), "Surd[-2]");
    Scalar scalar = suo.apply(RationalScalar.of(9, 16));
    ExactScalarQ.require(scalar);
    assertEquals(scalar, RationalScalar.of(4, 3));
  }

  public void testComplexFail() {
    Scalar scalar = ComplexScalar.of(12, 23);
    AssertFail.of(() -> Surd.of(3).apply(scalar));
  }

  public void testZeroExpFail() {
    AssertFail.of(() -> Surd.of(0));
  }
}
