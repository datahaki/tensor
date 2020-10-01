// code by jph
package ch.ethz.idsc.tensor.lie.r2;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.opt.PowerIteration;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RotationMatrixTest extends TestCase {
  public void testPointThree() {
    Tensor matrix = RotationMatrix.of(RealScalar.of(0.3));
    Tensor eye = matrix.dot(Transpose.of(matrix));
    assertEquals(eye, IdentityMatrix.of(2));
    assertTrue(OrthogonalMatrixQ.of(matrix));
    assertTrue(matrix.Get(0, 1).toString().startsWith("-0.2955"));
    assertTrue(matrix.Get(1, 1).toString().startsWith("0.95533"));
  }

  public void testComplex() {
    Tensor matrix = RotationMatrix.of(ComplexScalar.of(1, 2));
    Chop._12.requireClose(matrix.Get(0, 0), ComplexScalar.of(2.0327230070196655294, -3.0518977991518000575));
    Chop._12.requireClose(matrix.Get(0, 1), ComplexScalar.of(-3.1657785132161681467, -1.9596010414216058971));
    assertTrue(OrthogonalMatrixQ.of(matrix));
  }

  public void testNumber() {
    Tensor matrix = RotationMatrix.of(0.2);
    assertFalse(Chop._12.isClose(matrix, IdentityMatrix.of(2)));
    Chop._12.requireClose(matrix.dot(RotationMatrix.of(-0.2)), IdentityMatrix.of(2));
  }

  public void testRotationFail() {
    assertFalse(PowerIteration.of(RotationMatrix.of(DoubleScalar.of(0.3))).isPresent());
  }

  public void testFail() {
    try {
      RotationMatrix.of(GaussScalar.of(2, 7));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
