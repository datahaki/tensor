// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BSplineFunctionCyclicTest extends TestCase {
  public void testDegree0() {
    ScalarTensorFunction scalarTensorFunction = BSplineFunction.cyclic(0, Tensors.vector(1, 2, 3));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(-0.1)), RealScalar.of(1));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(0.0)), RealScalar.of(1));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(0.1)), RealScalar.of(1));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(0.9)), RealScalar.of(2));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(1.0)), RealScalar.of(2));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(1.1)), RealScalar.of(2));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(1.6)), RealScalar.of(3));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(2.0)), RealScalar.of(3));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(2.3)), RealScalar.of(3));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(3.0)), RealScalar.of(1));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(3.1)), RealScalar.of(1));
  }

  public void testDegree1() {
    ScalarTensorFunction scalarTensorFunction = BSplineFunction.cyclic(1, Tensors.vector(3, 4, 5));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(-0.5)), RealScalar.of(4.0));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(0.0)), RealScalar.of(3));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(0.5)), RealScalar.of(3.5));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(1.0)), RealScalar.of(4));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(1.5)), RealScalar.of(4.5));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(2.0)), RealScalar.of(5));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(2.5)), RealScalar.of(4));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(3.0)), RealScalar.of(3));
    assertEquals(scalarTensorFunction.apply(RealScalar.of(3.5)), RealScalar.of(3.5));
  }

  public void testExact() {
    Distribution distribution = UniformDistribution.of(0, 3);
    ScalarUnaryOperator scalarUnaryOperator = Round.toMultipleOf(RationalScalar.of(1, 7));
    for (int degree = 0; degree < 5; ++degree) {
      ScalarTensorFunction scalarTensorFunction = BSplineFunction.cyclic(degree, HilbertMatrix.of(3, 5));
      RandomVariate.of(distribution, 20).map(scalarTensorFunction);
      Tensor tensor = RandomVariate.of(distribution, 20).map(scalarUnaryOperator);
      Tensor result = tensor.map(scalarTensorFunction);
      ExactTensorQ.require(result.map(Sign::requirePositive));
    }
  }

  public void testEmptyFail() {
    for (int degree = -2; degree <= 4; ++degree) {
      int fd = degree;
      AssertFail.of(() -> BSplineFunction.cyclic(fd, Tensors.empty()));
    }
  }

  public void testNegativeFail() {
    AssertFail.of(() -> BSplineFunction.string(-1, Tensors.vector(1, 2, 3, 4)));
  }
}
