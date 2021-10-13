// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class InfluenceMatrixBaseTest extends TestCase {
  public void testExactRankDefficient() {
    int n = 7;
    int _m = 5;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    for (int count = 0; count < 10; ++count)
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
        SingularValueDecomposition svd = SingularValueDecomposition.of(design);
        TestHelper.requireNonQuantity(svd.getU());
        TestHelper.requireUnit(svd.values(), Unit.of("m"));
      }
  }

  public void testZeroQuantity() {
    Tensor design = ConstantArray.of(Quantity.of(0, "m"), 4, 3);
    SingularValueDecomposition svd = SingularValueDecomposition.of(design);
    TestHelper.requireNonQuantity(svd.getU());
    TestHelper.requireUnit(svd.values(), Unit.of("m"));
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    Tensor matrix = SymmetricMatrixQ.require(influenceMatrix.matrix());
    ExactTensorQ.require(matrix);
    assertEquals(matrix, Array.zeros(4, 4));
    Tensor image = influenceMatrix.image(Tensors.vector(1, 2, 3, 4));
    assertEquals(image, Array.zeros(4));
  }

  public void testNumericZeroQuantity() {
    Tensor design = ConstantArray.of(Quantity.of(0.0, "m"), 4, 3);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    Tensor matrix = SymmetricMatrixQ.require(influenceMatrix.matrix());
    assertEquals(matrix, Array.zeros(4, 4));
    SingularValueDecomposition svd = SingularValueDecomposition.of(design);
    TestHelper.requireNonQuantity(svd.getU());
    TestHelper.requireUnit(svd.values(), Unit.of("m"));
  }
}
