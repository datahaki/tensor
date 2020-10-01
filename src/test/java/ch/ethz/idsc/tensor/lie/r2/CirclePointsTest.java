// code by jph
package ch.ethz.idsc.tensor.lie.r2;

import java.util.Arrays;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.BSplineFunction;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Decrement;
import junit.framework.TestCase;

public class CirclePointsTest extends TestCase {
  static Tensor numeric(int n) {
    if (n < 0)
      throw new RuntimeException("n=" + n);
    return Range.of(0, n).multiply(DoubleScalar.of(2 * Math.PI / n)).map(AngleVector::of);
  }

  public void testZero() {
    assertEquals(CirclePoints.of(0), Tensors.empty());
  }

  public void testExact() {
    ExactTensorQ.require(CirclePoints.of(2));
    ExactTensorQ.require(CirclePoints.of(4));
    Chop._12.requireClose(CirclePoints.of(4), numeric(4));
    Chop._12.requireClose(CirclePoints.of(6), numeric(6));
    Chop._12.requireClose(CirclePoints.of(12), numeric(12));
    assertEquals(CirclePoints.of(6).flatten(-1).map(Scalar.class::cast).filter(ExactScalarQ::of).count(), 8);
    assertEquals(CirclePoints.of(9).flatten(-1).map(Scalar.class::cast).filter(ExactScalarQ::of).count(), 4);
    assertEquals(CirclePoints.of(12).flatten(-1).map(Scalar.class::cast).filter(ExactScalarQ::of).count(), 16);
  }

  public void testNumeric() {
    for (int count = 0; count <= 32; ++count)
      Chop._12.requireClose(CirclePoints.of(count), numeric(count));
  }

  public void testNorm() {
    int n = 5;
    Tensor tensor = CirclePoints.of(n);
    assertEquals(Dimensions.of(tensor), Arrays.asList(n, 2));
    Chop._14.requireClose(Tensor.of(tensor.stream().map(Norm2Squared::ofVector)), Array.of(l -> RealScalar.ONE, n));
    Chop._14.requireAllZero(Tensor.of(tensor.stream().map(Norm2Squared::ofVector).map(Decrement.ONE)));
  }

  public void testFirst() {
    int n = 5;
    Tensor tensor = CirclePoints.of(n);
    assertEquals(tensor.get(0), Tensors.vector(1, 0));
    assertEquals(tensor.get(0), UnitVector.of(2, 0));
  }

  public void testSmall() {
    assertEquals(CirclePoints.of(0), Tensors.empty());
    assertEquals(CirclePoints.of(1), Tensors.fromString("{{1, 0}}"));
    assertEquals(Chop._14.of(CirclePoints.of(2)), Tensors.fromString("{{1, 0}, {-1, 0}}"));
  }

  public void testCirclePoints() {
    ScalarTensorFunction bSplineFunction = BSplineFunction.string(3, CirclePoints.of(10));
    Tensor polygon = Subdivide.of(0, 9, 20).map(bSplineFunction::apply);
    assertTrue(MatrixQ.of(polygon));
  }

  public void testFailNegative() {
    try {
      CirclePoints.of(-1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
