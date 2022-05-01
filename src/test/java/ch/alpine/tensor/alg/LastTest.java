// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class LastTest {
  @Test
  public void testScalarReturn() {
    Scalar scalar = Last.of(Range.of(1, 4));
    assertEquals(scalar, RealScalar.of(3));
  }

  @Test
  public void testUseCase() {
    Clip clip = Clips.interval(RealScalar.of(2), Last.of(Range.of(1, 4)));
    clip.requireInside(RealScalar.of(3));
  }

  @Test
  public void testLast() {
    assertEquals(Last.of(Tensors.vector(3, 2, 6, 4)), RealScalar.of(4));
  }

  @Test
  public void testMatrix() {
    assertEquals(Last.of(IdentityMatrix.of(4)), UnitVector.of(4, 3));
  }

  @Test
  public void testFailEmpty() {
    assertThrows(IndexOutOfBoundsException.class, () -> Last.of(Tensors.empty()));
  }

  @Test
  public void testFailScalar() {
    assertThrows(TensorRuntimeException.class, () -> Last.of(RealScalar.of(99)));
  }
}
