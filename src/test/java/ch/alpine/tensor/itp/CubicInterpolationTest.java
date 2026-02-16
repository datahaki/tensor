// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.chq.ExactScalarQ;

class CubicInterpolationTest {
  @Test
  void testSimple() {
    Interpolation interpolation = CubicInterpolation.of(Tensors.vector(1, 0, 0, 2, 0));
    Tensor tensor = Subdivide.of(0, 4, 8);
    Tensor values = tensor.maps(interpolation::At);
    assertEquals(values, Tensors.fromString("{1, 293/448, 0, -177/448, 0, 583/448, 2, 421/448, 0}"));
  }

  @Test
  void testSingle() {
    Interpolation interpolation = CubicInterpolation.of(Tensors.vector(3));
    assertEquals(interpolation.At(RealScalar.ZERO), RealScalar.of(3));
  }

  @Test
  void testTuple() {
    Interpolation interpolation = CubicInterpolation.of(Tensors.vector(3, 5));
    Scalar scalar = interpolation.At(Rational.HALF);
    assertEquals(scalar, RealScalar.of(4));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testFail() {
    assertThrows(NullPointerException.class, () -> CubicInterpolation.of(null));
    assertThrows(Throw.class, () -> CubicInterpolation.of(Tensors.vector()));
  }
}
