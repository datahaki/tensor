// code by jph
package ch.alpine.tensor.mat.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;

class InfluenceMatrixSvdTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testRankDeficient(int rank) {
    int n = 7;
    int _m = 5;
    Distribution distribution = NormalDistribution.standard();
    Tensor m1 = RandomVariate.of(distribution, n, rank);
    Tensor m2 = RandomVariate.of(distribution, rank, _m);
    Tensor design = m1.dot(m2);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    influenceMatrix.image(RandomVariate.of(distribution, n));
    Tensor matrix = influenceMatrix.matrix();
    Tensor zeros = Dot.of(influenceMatrix.residualMaker(), matrix);
    Tolerance.CHOP.requireAllZero(zeros);
    InfluenceMatrixSvd influenceMatrixSvd = new InfluenceMatrixSvd(SingularValueDecomposition.of(design));
    Tolerance.CHOP.requireClose(matrix, influenceMatrixSvd.matrix());
    // System.out.println(matrix.length());
    Tensor x = TensorWedge.of(RandomVariate.of(distribution, n, n));
    Tensor a = MatrixExp.of(x);
    // Tolerance.CHOP.requireClose(matrix, Dot.of(a,matrix,Inverse.of(a)));
  }

  @Test
  void testSvdWithQuantity() {
    int n = 4;
    int _m = 4;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    for (int r = 1; r < _m - 1; ++r) {
      Tensor m1 = RandomVariate.of(distribution, n, r).map(s -> Quantity.of(s, "m"));
      Tensor m2 = RandomVariate.of(distribution, r, _m);
      Tensor design = m1.dot(m2);
      ExactTensorQ.require(design);
      SingularValueDecomposition svd = SingularValueDecomposition.of(design);
      assertEquals(EqualsReduce.zero(svd.getU()), RealScalar.ZERO);
      InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
      InfluenceMatrixQ.require(influenceMatrix.matrix());
      influenceMatrix.residualMaker();
      Tensor vector = RandomVariate.of(distribution, n);
      Tensor image = influenceMatrix.image(vector);
      VectorQ.requireLength(image, vector.length());
      Chop._10.requireClose(Total.ofVector(influenceMatrix.leverages()), RealScalar.of(r));
      String string = influenceMatrix.toString();
      assertTrue(string.startsWith("InfluenceMatrix["));
      InfluenceMatrixSvd influenceMatrixSvd = new InfluenceMatrixSvd(SingularValueDecomposition.of(design));
      Tolerance.CHOP.requireClose(influenceMatrix.matrix(), influenceMatrixSvd.matrix());
    }
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(InfluenceMatrixSvd.class.getModifiers()));
  }
}
