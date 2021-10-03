// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.IOException;
import java.util.Random;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class InfluenceMatrixSplitTest extends TestCase {
  public void testExact() throws ClassNotFoundException, IOException {
    int n = 7;
    int m = 3;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    Tensor design = RandomVariate.of(distribution, n, m);
    if (MatrixRank.of(design) == m) {
      InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
      assertTrue(influenceMatrix instanceof InfluenceMatrixSplit);
      ExactTensorQ.require(influenceMatrix.matrix());
      Tensor vector = RandomVariate.of(distribution, n);
      Tensor image = influenceMatrix.image(vector);
      ExactTensorQ.require(image);
      SymmetricMatrixQ.require(influenceMatrix.matrix());
      assertEquals(Total.ofVector(influenceMatrix.leverages()), RealScalar.of(m));
      String string = influenceMatrix.toString();
      assertTrue(string.startsWith("InfluenceMatrix"));
    }
    SingularValueDecomposition svd = SingularValueDecomposition.of(design);
    TestHelper.requireNonQuantity(svd.getU());
  }

  public void testGaussScalar() throws ClassNotFoundException, IOException {
    int n = 7;
    int m = 3;
    int prime = 6577;
    Random random = new Random();
    Tensor design = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(), prime), n, m);
    if (MatrixRank.of(design) == m) {
      InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
      assertTrue(influenceMatrix instanceof InfluenceMatrixSplit);
      Tensor matrix = influenceMatrix.matrix();
      SymmetricMatrixQ.require(matrix);
      assertEquals(Total.ofVector(influenceMatrix.leverages()), GaussScalar.of(m, prime));
      Tensor zeros = Dot.of(influenceMatrix.residualMaker(), matrix);
      Chop.NONE.requireAllZero(zeros);
      assertEquals(zeros, Array.fill(() -> GaussScalar.of(0, prime), n, n));
    }
  }
}
