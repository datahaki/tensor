// code by jph
package ch.alpine.tensor.lie.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.itp.BSplineFunctionString;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.sca.Chop;

class CirclePointsTest {
  static Tensor numeric(int n) {
    if (n < 0)
      throw new RuntimeException("n=" + n);
    return Range.of(0, n).multiply(DoubleScalar.of(2 * Math.PI / n)).map(AngleVector::of);
  }

  @Test
  public void testZero() {
    assertEquals(CirclePoints.of(0), Tensors.empty());
  }

  @Test
  public void testExact() {
    ExactTensorQ.require(CirclePoints.of(2));
    ExactTensorQ.require(CirclePoints.of(4));
    Tolerance.CHOP.requireClose(CirclePoints.of(4), numeric(4));
    Tolerance.CHOP.requireClose(CirclePoints.of(6), numeric(6));
    Tolerance.CHOP.requireClose(CirclePoints.of(12), numeric(12));
    assertEquals(CirclePoints.of(6).flatten(-1).map(Scalar.class::cast).filter(ExactScalarQ::of).count(), 8);
    assertEquals(CirclePoints.of(9).flatten(-1).map(Scalar.class::cast).filter(ExactScalarQ::of).count(), 4);
    assertEquals(CirclePoints.of(12).flatten(-1).map(Scalar.class::cast).filter(ExactScalarQ::of).count(), 16);
  }

  @Test
  public void testNumeric() {
    for (int count = 0; count <= 32; ++count)
      Tolerance.CHOP.requireClose(CirclePoints.of(count), numeric(count));
  }

  @Test
  public void testNorm() {
    int n = 5;
    Tensor tensor = CirclePoints.of(n);
    assertEquals(Dimensions.of(tensor), Arrays.asList(n, 2));
    Chop._14.requireClose(Tensor.of(tensor.stream().map(Vector2NormSquared::of)), Array.of(l -> RealScalar.ONE, n));
    Chop._14.requireAllZero(Tensor.of(tensor.stream().map(Vector2NormSquared::of).map(s -> s.subtract(RealScalar.ONE))));
  }

  @Test
  public void testFirst() {
    int n = 5;
    Tensor tensor = CirclePoints.of(n);
    assertEquals(tensor.get(0), Tensors.vector(1, 0));
    assertEquals(tensor.get(0), UnitVector.of(2, 0));
  }

  @Test
  public void testSmall() {
    assertEquals(CirclePoints.of(0), Tensors.empty());
    assertEquals(CirclePoints.of(1), Tensors.fromString("{{1, 0}}"));
    assertEquals(Chop._14.of(CirclePoints.of(2)), Tensors.fromString("{{1, 0}, {-1, 0}}"));
  }

  @Test
  public void testCirclePoints() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(3, CirclePoints.of(10));
    Tensor polygon = Subdivide.of(0, 9, 20).map(bSplineFunction::apply);
    assertTrue(MatrixQ.of(polygon));
  }

  @Test
  public void testFailNegative() {
    assertThrows(IllegalArgumentException.class, () -> CirclePoints.of(-1));
  }
}
