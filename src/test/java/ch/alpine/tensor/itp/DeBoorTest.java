// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;

class DeBoorTest {
  @Test
  void testDegree0() throws ClassNotFoundException, IOException {
    Tensor knots = Tensors.empty().unmodifiable();
    Tensor control = Tensors.vector(-1).unmodifiable();
    DeBoor deBoor = Serialization.copy(DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control));
    assertEquals(deBoor.apply(RealScalar.of(9)), RealScalar.of(-1));
    assertEquals(deBoor.apply(RealScalar.of(9.25)), RealScalar.of(-1));
    assertEquals(deBoor.apply(RealScalar.of(16)), RealScalar.of(-1));
    assertEquals(deBoor.degree(), 0);
    assertEquals(deBoor.knots(), knots);
    assertEquals(deBoor.control(), control);
  }

  @Test
  void testLinearUnit() {
    Tensor knots = Tensors.vector(9, 10).unmodifiable();
    Tensor control = Tensors.vector(-1, 3).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.degree(), 1);
    assertEquals(deBoor.apply(RealScalar.of(9)), RealScalar.of(-1));
    assertEquals(deBoor.apply(RealScalar.of(9.25)), RealScalar.of(0));
    assertEquals(deBoor.apply(RealScalar.of(10)), RealScalar.of(3));
  }

  @Test
  void testLinearDouble() {
    Tensor knots = Tensors.vector(9, 11).unmodifiable();
    Tensor control = Tensors.vector(-1, 3).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.apply(RealScalar.of(9)), RealScalar.of(-1));
    assertEquals(deBoor.apply(RealScalar.of(9.5)), RealScalar.of(0));
    assertEquals(deBoor.apply(RealScalar.of(10)), RealScalar.of(1));
    assertEquals(deBoor.apply(RealScalar.of(10.5)), RealScalar.of(2));
    assertEquals(deBoor.apply(RealScalar.of(11)), RealScalar.of(3));
    assertEquals(deBoor.degree(), 1);
    assertEquals(deBoor.knots(), knots);
    assertEquals(deBoor.control(), control);
  }

  @Test
  void testQuadratic() {
    Tensor knots = Tensors.vector(9, 10, 11, 12).unmodifiable();
    Tensor control = Tensors.vector(-1, 3, 2).unmodifiable();
    Tensor points = Tensors.empty();
    for (Tensor x : Subdivide.of(10, 11, 5))
      points.append(Tensors.of(x, DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control).apply((Scalar) x)));
    Tensor result = Tensors.fromString( //
        "{{10, 1}, {51/5, 17/10}, {52/5, 11/5}, {53/5, 5/2}, {54/5, 13/5}, {11, 5/2}}");
    assertEquals(result, points);
    ExactTensorQ.require(points);
  }

  @Test
  void testQuadratic2() {
    Tensor knots = Tensors.vector(9, 10, 11, 12).unmodifiable();
    Tensor control = Tensors.vector(-1, 3, 2).unmodifiable();
    Tensor points = Tensors.empty();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    for (Tensor x : Subdivide.of(10, 11, 5))
      points.append(Tensors.of(x, deBoor.apply((Scalar) x)));
    Tensor result = Tensors.fromString( //
        "{{10, 1}, {51/5, 17/10}, {52/5, 11/5}, {53/5, 5/2}, {54/5, 13/5}, {11, 5/2}}");
    assertEquals(result, points);
    ExactTensorQ.require(points);
    assertEquals(deBoor.degree(), 2);
    assertEquals(deBoor.knots(), knots);
    assertEquals(deBoor.control(), control);
  }

  @Test
  void testCubic() {
    Tensor knots = Tensors.vector(9, 10, 11, 12, 13, 14).unmodifiable();
    Tensor control = Tensors.vector(-1, 3, 2, 7).unmodifiable();
    Tensor points = Tensors.empty();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    for (Tensor x : Subdivide.of(11, 12, 5))
      points.append(Tensors.of(x, deBoor.apply((Scalar) x)));
    Tensor result = Tensors.fromString( //
        "{{11, 13/6}, {56/5, 893/375}, {57/5, 621/250}, {58/5, 961/375}, {59/5, 2029/750}, {12, 3}}");
    assertEquals(result, points);
    ExactTensorQ.require(points);
    assertEquals(deBoor.degree(), 3);
    assertEquals(deBoor.knots(), knots);
    assertEquals(deBoor.control(), control);
  }

  @Test
  void testCubicLimit() {
    Tensor knots = Tensors.vector(11, 11, 11, 12, 13, 14).unmodifiable();
    Tensor control = Tensors.vector(3, 3, 2, 7).unmodifiable();
    Tensor points = Tensors.empty();
    for (Tensor x : Subdivide.of(11, 12, 10))
      points.append(Tensors.of(x, DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control).apply((Scalar) x)));
    Tensor result = Tensors.fromString( //
        "{{11, 3}, {111/10, 35839/12000}, {56/5, 4429/1500}, {113/10, 11631/4000}, {57/5, 1073/375}, {23/2, 271/96}, {58/5, 1401/500}, {117/10, 33697/12000}, {59/5, 1069/375}, {119/10, 11757/4000}, {12, 37/12}}");
    assertEquals(result, points);
    ExactTensorQ.require(points);
  }

  @Test
  void testWikiStyleConstant0() throws ClassNotFoundException, IOException {
    Tensor knots = Tensors.unmodifiableEmpty();
    Tensor control = Tensors.vector(90).unmodifiable();
    DeBoor deBoor = Serialization.copy(DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control));
    assertEquals(deBoor.degree(), 0);
    assertEquals(deBoor.apply(RealScalar.of(0)), RealScalar.of(90));
    assertEquals(deBoor.apply(Rational.of(1, 2)), RealScalar.of(90));
    assertEquals(deBoor.apply(RealScalar.of(1)), RealScalar.of(90));
  }

  @Test
  void testWikiStyleLinear0() {
    Tensor knots = Tensors.vector(0, 1).unmodifiable();
    Tensor control = Tensors.vector(90, 100).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.degree(), 1);
    assertEquals(deBoor.apply(RealScalar.of(0)), RealScalar.of(90));
    assertEquals(deBoor.apply(Rational.of(1, 2)), RealScalar.of(95));
    assertEquals(deBoor.apply(RealScalar.of(1)), RealScalar.of(100));
  }

  @Test
  void testWikiStyleLinear1() {
    Tensor knots = Tensors.vector(1, 2).unmodifiable();
    Tensor control = Tensors.vector(100, 120).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.degree(), 1);
    assertEquals(deBoor.apply(RealScalar.of(1)), RealScalar.of(100));
    assertEquals(deBoor.apply(Rational.of(3, 2)), RealScalar.of(110));
    assertEquals(deBoor.apply(RealScalar.of(2)), RealScalar.of(120));
  }

  /** example from Wikipedia */
  @Test
  void testWikiQuadratic0() {
    Tensor knots = Tensors.vector(0, 0, 1, 2).unmodifiable();
    Tensor control = Tensors.vector(0, 0, 1).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.degree(), 2);
    assertEquals(deBoor.apply(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(deBoor.apply(Rational.of(1, 2)), Rational.of(1, 8));
    assertEquals(deBoor.apply(RealScalar.of(1)), Rational.of(1, 2));
  }

  @Test
  void testWikiQuadratic1() {
    Tensor knots = Tensors.vector(0, 1, 2, 3).unmodifiable();
    Tensor control = Tensors.vector(0, 1, 0).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.degree(), 2);
    assertEquals(deBoor.apply(RealScalar.of(1)), Rational.of(1, 2));
    assertEquals(deBoor.apply(Rational.of(3, 2)), Rational.of(3, 4));
    assertEquals(deBoor.apply(RealScalar.of(2)), Rational.of(1, 2));
  }

  @Test
  void testWikiQuadratic2() {
    Tensor knots = Tensors.vector(1, 2, 3, 3).unmodifiable();
    Tensor control = Tensors.vector(1, 0, 0).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.degree(), 2);
    assertEquals(deBoor.apply(RealScalar.of(2)), Rational.of(1, 2));
    assertEquals(deBoor.apply(Rational.of(5, 2)), Rational.of(1, 8));
    assertEquals(deBoor.apply(RealScalar.of(3)), Rational.of(0, 2));
  }

  /** example from Wikipedia */
  @Test
  void testWikiCubic0() {
    Tensor knots = Tensors.vector(-2, -2, -2, -1, 0, 1).unmodifiable();
    Tensor control = Tensors.vector(0, 0, 0, 6).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.degree(), 3);
    assertEquals(deBoor.apply(RealScalar.of(-2)), RealScalar.of(0));
    assertEquals(deBoor.apply(RealScalar.of(-1.5)), Rational.of(1, 8));
    assertEquals(deBoor.apply(RealScalar.of(-1)), RealScalar.of(1));
  }

  @Test
  void testWikiCubic1() {
    Tensor knots = Tensors.vector(-2, -2, -1, 0, 1, 2).unmodifiable();
    Tensor control = Tensors.vector(0, 0, 6, 0).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.degree(), 3);
    assertEquals(deBoor.apply(RealScalar.of(-1)), RealScalar.of(1));
    assertEquals(deBoor.apply(Rational.of(-1, 2)), Rational.of(23, 8));
    assertEquals(deBoor.apply(RealScalar.of(0)), RealScalar.of(4));
  }

  @Test
  void testWikiCubic2() {
    Tensor knots = Tensors.vector(-2, -1, 0, 1, 2, 2).unmodifiable();
    Tensor control = Tensors.vector(0, 6, 0, 0).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.degree(), 3);
    assertEquals(deBoor.apply(RealScalar.of(0)), RealScalar.of(4));
    assertEquals(deBoor.apply(Rational.of(1, 2)), Rational.of(23, 8));
    assertEquals(deBoor.apply(RealScalar.of(1)), RealScalar.of(1));
  }

  @Test
  void testWikiCubic3() {
    Tensor knots = Tensors.vector(-1, 0, 1, 2, 2, 2).unmodifiable();
    Tensor control = Tensors.vector(6, 0, 0, 0).unmodifiable();
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertEquals(deBoor.apply(RealScalar.of(1)), RealScalar.of(1));
    assertEquals(deBoor.apply(Rational.of(3, 2)), Rational.of(1, 8));
    assertEquals(deBoor.apply(RealScalar.of(2)), RealScalar.of(0));
  }

  @Test
  void testNullFail() {
    Tensor knots = Tensors.vector(-1, 0, 1, 2, 2, 2).unmodifiable();
    Tensor control = Tensors.vector(6, 0, 0, 0).unmodifiable();
    DeBoor.of(LinearBinaryAverage.INSTANCE, knots, control);
    assertThrows(NullPointerException.class, () -> DeBoor.of(null, knots, control));
  }

  @Test
  void testKnotsScalarFail() {
    assertThrows(IllegalArgumentException.class, () -> DeBoor.of(LinearBinaryAverage.INSTANCE, RealScalar.ONE, Tensors.empty()));
  }

  @Test
  void testKnotsMatrixFail() {
    assertThrows(Throw.class, () -> DeBoor.of(LinearBinaryAverage.INSTANCE, HilbertMatrix.of(2), Tensors.vector(1, 2)));
  }

  @Test
  void testKnotsLengthOdd() {
    DeBoor deBoor = DeBoor.of(LinearBinaryAverage.INSTANCE, Tensors.vector(1, 2), Range.of(0, 2));
    assertEquals(deBoor.degree(), 1);
    assertThrows(Exception.class, () -> DeBoor.of(LinearBinaryAverage.INSTANCE, Tensors.vector(1, 2, 3), Range.of(0, 2)));
  }

  @Test
  void testControlFail() {
    for (int length = 0; length < 10; ++length)
      if (length != 2) {
        int fl = length;
        assertThrows(IllegalArgumentException.class, () -> DeBoor.of(LinearBinaryAverage.INSTANCE, Tensors.vector(1, 2), Range.of(0, fl)));
      }
  }
}
