// code by jph
package ch.alpine.tensor.nrm;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.Tolerance;

class NuclearNormTest {
  @Test
  public void testSimple() {
    Tolerance.CHOP.requireClose(NuclearNorm.of(DiagonalMatrix.of(2, 1, 0)), RealScalar.of(3));
  }
}
