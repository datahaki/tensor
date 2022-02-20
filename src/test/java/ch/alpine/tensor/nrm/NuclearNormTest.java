// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class NuclearNormTest extends TestCase {
  public void testSimple() {
    Tolerance.CHOP.requireClose(NuclearNorm.of(DiagonalMatrix.of(2, 1, 0)), RealScalar.of(3));
  }
}
