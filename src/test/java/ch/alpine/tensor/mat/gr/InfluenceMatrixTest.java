// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.IOException;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.LeastSquares;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
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

  private static Tensor imageQR(Tensor design, Tensor vector) {
    return design.dot(LeastSquares.of(design, vector));
  }

  public void testLeftKernel() throws ClassNotFoundException, IOException {
    Tensor design = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
    _check(influenceMatrix);
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor ker1 = influenceMatrix.kernel(vector);
    Tolerance.CHOP.requireAllZero(ker1.dot(design));
    Tensor ker2 = influenceMatrix.residualMaker().dot(vector);
    Tolerance.CHOP.requireClose(ker1, ker2);
  }

  private static Tensor proj(InfluenceMatrix influenceMatrix, Tensor v) {
    Tensor w1 = Transpose.of(Tensor.of(v.stream().map(influenceMatrix::image)));
    Tensor w2 = Tensor.of(v.stream().map(influenceMatrix::image));
    Tensor x = influenceMatrix.matrix();
    Tolerance.CHOP.requireClose(x.dot(w1), w1);
    Tolerance.CHOP.requireClose(w2.dot(x), w2);
    Tensor w = w1.add(w2).multiply(RationalScalar.HALF);
    SymmetricMatrixQ.require(w, Tolerance.CHOP);
    return w;
  }

  public void testLeftImage() throws ClassNotFoundException, IOException {
    int n = 10;
    Distribution distribution = NormalDistribution.standard();
    Tensor design = RandomVariate.of(distribution, n, 3);
    Tensor v0 = RandomVariate.of(distribution, n);
    Tensor v1 = RandomVariate.of(distribution, n);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    _check(influenceMatrix);
    influenceMatrix.image(v0);
    Tensor vim1 = influenceMatrix.image(v1);
    Tensor x = influenceMatrix.matrix();
    Tensor vim2 = x.dot(v1);
    Tolerance.CHOP.requireClose(vim1, vim2);
    Tolerance.CHOP.requireClose( //
        v1.dot(design), //
        vim1.dot(design));
    Tensor vim3 = imageQR(design, v1);
    Tolerance.CHOP.requireClose(vim1, vim3);
    {
      Tensor v = RandomVariate.of(distribution, n, n);
      // Tensor w1 = Transpose.of(Tensor.of(v.stream().map(influenceMatrix::image)));
      // Tensor w2 = Tensor.of(v.stream().map(influenceMatrix::image));
      // Tolerance.CHOP.requireClose(x.dot(w1), w1);
      // Tolerance.CHOP.requireClose(w2.dot(x), w2);
      Tensor w = proj(influenceMatrix, v);
      // w1.add(w2).multiply(RationalScalar.HALF);
      // System.out.println(MatrixNorm2.bound(w.subtract(v)));
      // System.out.println(MatrixNorm2.bound(w.subtract(x.dot(w).add(w.dot(x)))));
      w = proj(influenceMatrix, v);
      w.length();
      // System.out.println(MatrixNorm2.bound(w.subtract(x.dot(w).add(w.dot(x)))));
      // Chop._08.requireClose(x.dot(w).add(w.dot(x)),w);
      // Chop._08.requireClose(w2.dot(x),w2);
      // Chop._08.requireClose(w, x.dot(w).add(w.dot(x)));
    }
  }

  public void testBicChallenge() {
    Tensor matrix = ResourceData.of("/mat/bic_fail.csv");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(matrix);
    influenceMatrix.leverages();
  }

  public void testNullFail() {
    AssertFail.of(() -> InfluenceMatrix.of(null));
  }
}
