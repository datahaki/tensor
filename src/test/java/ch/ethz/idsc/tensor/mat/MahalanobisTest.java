// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
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
      influenceMatrix.matrix().map(QuantityMagnitude.singleton(Unit.ONE));
    }
  }

  public void testExact() throws ClassNotFoundException, IOException {
    Distribution distribution = DiscreteUniformDistribution.of(-1000, 1000);
    Tensor design = RandomVariate.of(distribution, 8, 3);
    Mahalanobis mahalanobis = Serialization.copy(new Mahalanobis(design));
    assertEquals(ExactTensorQ.require(mahalanobis.design()), design);
    ExactTensorQ.require(mahalanobis.sigma_inverse());
    ExactTensorQ.require(mahalanobis.sigma_n());
    assertTrue(mahalanobis.toString().startsWith("Mahalanobis"));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> new Mahalanobis(Tensors.empty()));
  }

  public void testNullFail() {
    AssertFail.of(() -> new Mahalanobis(null));
  }
}
