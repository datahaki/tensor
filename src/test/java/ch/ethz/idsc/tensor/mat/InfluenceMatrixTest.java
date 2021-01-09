// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class InfluenceMatrixTest extends TestCase {
  public void testSimple() {
    Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor point = RandomVariate.of(NormalDistribution.standard(), 3);
    Tensor matrix = Tensor.of(sequence.stream().map(point.negate()::add));
    Tensor nullsp = LeftNullSpace.of(matrix);
    OrthogonalMatrixQ.require(nullsp);
    Chop._08.requireClose(PseudoInverse.usingSvd(nullsp), Transpose.of(nullsp));
  }

  // TODO use in tests as comparison
  private static Tensor imageQR(Tensor design, Tensor vector) {
    return design.dot(LeastSquares.of(design, vector));
  }

  public void testLeftKernel() {
    Tensor design = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor ker1 = InfluenceMatrix.of(design).kernel(vector);
    Tolerance.CHOP.requireAllZero(ker1.dot(design));
    Tensor ker2 = InfluenceMatrix.of(design).residualMaker().dot(vector);
    Tolerance.CHOP.requireClose(ker1, ker2);
  }

  public void testLeftImage() {
    Tensor design = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor vim1 = InfluenceMatrix.of(design).image(vector);
    Tensor vim2 = InfluenceMatrix.of(design).matrix().dot(vector);
    Tolerance.CHOP.requireClose(vim1, vim2);
    Tolerance.CHOP.requireClose( //
        vector.dot(design), //
        vim1.dot(design));
    Tensor vim3 = imageQR(design, vector);
    Tolerance.CHOP.requireClose(vim1, vim3);
  }

  private static Tensor deprec(Tensor vector, Tensor nullsp) {
    return vector.dot(PseudoInverse.usingSvd(nullsp)).dot(nullsp);
  }

  public void testQR() {
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 10; ++count) {
      Tensor vector = RandomVariate.of(distribution, 10);
      Tensor design = RandomVariate.of(distribution, 10, 3);
      Tensor nullsp = LeftNullSpace.usingQR(design);
      Tensor p1 = deprec(vector, nullsp);
      Tensor p2 = Dot.of(nullsp, vector, nullsp);
      Tolerance.CHOP.requireClose(p1, p2);
    }
  }

  public void testNullFail() {
    AssertFail.of(() -> InfluenceMatrix.of(null));
  }
}
