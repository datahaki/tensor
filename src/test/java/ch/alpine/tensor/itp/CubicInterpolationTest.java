// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CubicInterpolationTest extends TestCase {
  public void testSimple() {
    Interpolation interpolation = CubicInterpolation.of(Tensors.vector(1, 0, 0, 2, 0));
    Tensor tensor = Subdivide.of(0, 4, 8);
    Tensor values = tensor.map(interpolation::At);
    assertEquals(values, Tensors.fromString("{1, 293/448, 0, -177/448, 0, 583/448, 2, 421/448, 0}"));
  }

  public void testSingle() {
    Interpolation interpolation = CubicInterpolation.of(Tensors.vector(3));
    assertEquals(interpolation.At(RealScalar.ZERO), RealScalar.of(3));
  }

  public void testTuple() {
    Interpolation interpolation = CubicInterpolation.of(Tensors.vector(3, 5));
    Scalar scalar = interpolation.At(RationalScalar.HALF);
    assertEquals(scalar, RealScalar.of(4));
    ExactScalarQ.require(scalar);
  }

  public void testFail() {
    AssertFail.of(() -> CubicInterpolation.of(null));
    AssertFail.of(() -> CubicInterpolation.of(Tensors.vector()));
  }
}
