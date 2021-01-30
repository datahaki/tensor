// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class InfluenceMatrixBaseTest extends TestCase {
  public void testExactRankDefficient() {
    int n = 7;
    int _m = 5;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    for (int r = 1; r < _m - 1; ++r) {
      Tensor m1 = RandomVariate.of(distribution, n, r).map(s -> Quantity.of(s, "m"));
      Tensor m2 = RandomVariate.of(distribution, r, _m);
      Tensor design = m1.dot(m2);
      ExactTensorQ.require(design);
      InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
      SymmetricMatrixQ.require(influenceMatrix.matrix());
      influenceMatrix.residualMaker();
      Tensor vector = RandomVariate.of(distribution, n);
      Tensor image = influenceMatrix.image(vector);
      VectorQ.requireLength(image, vector.length());
      Chop._10.requireClose(Total.ofVector(influenceMatrix.leverages()), RealScalar.of(r));
      String string = influenceMatrix.toString();
      assertTrue(string.startsWith("InfluenceMatrix["));
    }
  }
}
