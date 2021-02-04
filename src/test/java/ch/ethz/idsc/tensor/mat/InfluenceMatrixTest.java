// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class InfluenceMatrixTest extends TestCase {
  private static void _check(InfluenceMatrix influenceMatrix) throws ClassNotFoundException, IOException {
    InfluenceMatrix _influenceMatrix = Serialization.copy(influenceMatrix);
    Tensor leverages = _influenceMatrix.leverages();
    leverages.stream() //
        .map(Scalar.class::cast) //
        .forEach(Clips.unit()::requireInside);
    Tensor leverages_sqrt = _influenceMatrix.leverages_sqrt();
    leverages_sqrt.stream() //
        .map(Scalar.class::cast) //
        .forEach(Clips.unit()::requireInside);
  }

  public void testSimple() {
    Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor point = RandomVariate.of(NormalDistribution.standard(), 3);
    Tensor matrix = Tensor.of(sequence.stream().map(point.negate()::add));
    Tensor nullsp = LeftNullSpace.of(matrix);
    OrthogonalMatrixQ.require(nullsp);
    Chop._08.requireClose(PseudoInverse.usingSvd(nullsp), Transpose.of(nullsp));
  }

  private static Tensor imageQR(Tensor design, Tensor vector) {
    return design.dot(LeastSquares.of(design, vector));
  }

  public void testLeftKernel() throws ClassNotFoundException, IOException {
    Tensor design = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), 10);
    InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
    _check(influenceMatrix);
    Tensor ker1 = influenceMatrix.kernel(vector);
    Tolerance.CHOP.requireAllZero(ker1.dot(design));
    Tensor ker2 = influenceMatrix.residualMaker().dot(vector);
    Tolerance.CHOP.requireClose(ker1, ker2);
  }

  public void testLeftImage() throws ClassNotFoundException, IOException {
    Tensor design = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor v0 = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor v1 = RandomVariate.of(NormalDistribution.standard(), 10);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    _check(influenceMatrix);
    influenceMatrix.image(v0);
    Tensor vim1 = influenceMatrix.image(v1);
    Tensor vim2 = influenceMatrix.matrix().dot(v1);
    Tolerance.CHOP.requireClose(vim1, vim2);
    Tolerance.CHOP.requireClose( //
        v1.dot(design), //
        vim1.dot(design));
    Tensor vim3 = imageQR(design, v1);
    Tolerance.CHOP.requireClose(vim1, vim3);
  }

  public void testNullFail() {
    AssertFail.of(() -> InfluenceMatrix.of(null));
  }
}
