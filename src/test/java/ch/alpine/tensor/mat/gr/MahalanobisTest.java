// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.IOException;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MahalanobisTest extends TestCase {
  public void testRankDeficientQuantity() {
    int n = 7;
    int _m = 5;
    Distribution distribution = NormalDistribution.standard();
    for (int r = 1; r < _m - 1; ++r) {
      Tensor m1 = RandomVariate.of(distribution, n, r).map(s -> Quantity.of(s, "m"));
      Tensor m2 = RandomVariate.of(distribution, r, _m);
      Tensor design = m1.dot(m2);
      InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
      Mahalanobis mahalanobis = new Mahalanobis(design);
      SymmetricMatrixQ.require(influenceMatrix.matrix());
      influenceMatrix.image(RandomVariate.of(distribution, n));
      Chop._07.requireClose(influenceMatrix.leverages(), mahalanobis.leverages());
      Chop._07.requireClose(influenceMatrix.leverages_sqrt(), mahalanobis.leverages_sqrt());
      Chop._07.requireClose(influenceMatrix.matrix(), mahalanobis.matrix());
      Chop._07.requireClose(influenceMatrix.residualMaker(), mahalanobis.residualMaker());
      influenceMatrix.matrix().map(QuantityMagnitude.singleton(Unit.ONE));
      Tensor vector = RandomVariate.of(distribution, n);
      Chop._07.requireClose(influenceMatrix.image(vector), mahalanobis.image(vector));
      Chop._07.requireClose(influenceMatrix.kernel(vector), mahalanobis.kernel(vector));
    }
  }

  public void testExact() throws ClassNotFoundException, IOException {
    for (int n = 7; n < 9; ++n) {
      Distribution distribution = DiscreteUniformDistribution.of(-1000, 1000);
      Tensor design = RandomVariate.of(distribution, n, 3);
      ExactTensorQ.require(design);
      Mahalanobis mahalanobis = Serialization.copy(new Mahalanobis(design));
      InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
      Tensor vector = RandomVariate.of(distribution, n);
      assertEquals(influenceMatrix.image(vector), mahalanobis.image(vector));
      assertEquals(influenceMatrix.kernel(vector), mahalanobis.kernel(vector));
      Tensor sigma_inverse = mahalanobis.sigma_inverse();
      ExactTensorQ.require(sigma_inverse);
      assertEquals(Inverse.of(Transpose.of(design).dot(design)), sigma_inverse);
      assertEquals(PseudoInverse.of(Transpose.of(design).dot(design)), sigma_inverse);
      ExactTensorQ.require(mahalanobis.sigma_n());
      assertTrue(mahalanobis.toString().startsWith("Mahalanobis"));
      ExactTensorQ.require(mahalanobis.leverages());
      assertEquals(influenceMatrix.leverages(), mahalanobis.leverages());
      Chop._07.requireClose(influenceMatrix.leverages_sqrt(), mahalanobis.leverages_sqrt());
      ExactTensorQ.require(mahalanobis.matrix());
      assertEquals(influenceMatrix.matrix(), mahalanobis.matrix());
      assertEquals(influenceMatrix.residualMaker(), mahalanobis.residualMaker());
      Tensor vectors = RandomVariate.of(distribution, 20, n);
      ExactTensorQ.require(mahalanobis.image(vectors));
    }
  }

  public void testEmptyFail() {
    AssertFail.of(() -> new Mahalanobis(Tensors.empty()));
  }

  public void testNullFail() {
    AssertFail.of(() -> new Mahalanobis(null));
  }
}
