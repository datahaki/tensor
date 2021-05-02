// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LastTest extends TestCase {
  public void testScalarReturn() {
    Scalar scalar = Last.of(Range.of(1, 4));
    assertEquals(scalar, RealScalar.of(3));
  }

  public void testUseCase() {
    Clip clip = Clips.interval(RealScalar.of(2), Last.of(Range.of(1, 4)));
    clip.requireInside(RealScalar.of(3));
  }

  public void testLast() {
    assertEquals(Last.of(Tensors.vector(3, 2, 6, 4)), RealScalar.of(4));
  }

  public void testMatrix() {
    assertEquals(Last.of(IdentityMatrix.of(4)), UnitVector.of(4, 3));
  }

  public void testFailEmpty() {
    AssertFail.of(() -> Last.of(Tensors.empty()));
  }

  public void testFailScalar() {
    AssertFail.of(() -> Last.of(RealScalar.of(99)));
  }
}
