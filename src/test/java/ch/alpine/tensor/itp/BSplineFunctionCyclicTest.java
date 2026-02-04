// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.RotateRight;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;

class BSplineFunctionCyclicTest {
  @Test
  void testDegree0() {
    ScalarTensorFunction scalarTensorFunction = BSplineFunctionCyclic.of(0, Tensors.vector(1, 2, 3));
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

  @Test
  void testDegree1() {
    ScalarTensorFunction scalarTensorFunction = BSplineFunctionCyclic.of(1, Tensors.vector(3, 4, 5));
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

  @Test
  void testCyclic() {
    RandomGenerator randomGenerator = ThreadLocalRandom.current();
    Distribution distribution = DiscreteUniformDistribution.of(-50, 50);
    for (int n = 1; n < 10; ++n)
      for (int degree = 0; degree < 6; ++degree) {
        Tensor control = RandomVariate.of(distribution, n, 3);
        ScalarTensorFunction stf1 = BSplineFunctionCyclic.of(degree, control);
        int shift = randomGenerator.nextInt(20);
        ScalarTensorFunction stf2 = BSplineFunctionCyclic.of(degree, RotateRight.of(control, shift));
        Scalar x1 = RandomVariate.of(distribution).divide(RealScalar.of(7));
        Scalar x2 = x1.add(RealScalar.of(shift));
        Tensor y1 = stf1.apply(x1);
        Tensor y2 = stf2.apply(x2);
        assertEquals(y1, y2);
        ExactTensorQ.require(y1);
        ExactTensorQ.require(y2);
      }
  }

  @Test
  void testExact() {
    Distribution distribution = UniformDistribution.of(0, 3);
    ScalarUnaryOperator scalarUnaryOperator = Round.toMultipleOf(RationalScalar.of(1, 7));
    for (int degree = 0; degree < 5; ++degree) {
      ScalarTensorFunction scalarTensorFunction = BSplineFunctionCyclic.of(degree, HilbertMatrix.of(3, 5));
      RandomVariate.of(distribution, 20).map(scalarTensorFunction);
      Tensor tensor = RandomVariate.of(distribution, 20).map(scalarUnaryOperator);
      Tensor result = tensor.map(scalarTensorFunction);
      ExactTensorQ.require(result.map(Sign::requirePositive));
    }
  }

  @Test
  void testEmptyFail() {
    assertThrows(IllegalArgumentException.class, () -> BSplineFunctionCyclic.of(-2, Tensors.empty()));
    assertThrows(IllegalArgumentException.class, () -> BSplineFunctionCyclic.of(-1, Tensors.empty()));
    assertThrows(Throw.class, () -> BSplineFunctionCyclic.of(+0, Tensors.empty()));
    assertThrows(Throw.class, () -> BSplineFunctionCyclic.of(+1, Tensors.empty()));
    assertThrows(Throw.class, () -> BSplineFunctionCyclic.of(+2, Tensors.empty()));
  }

  @Test
  void testNegativeFail() {
    assertThrows(IllegalArgumentException.class, () -> BSplineFunctionString.of(-1, Tensors.vector(1, 2, 3, 4)));
  }
}
