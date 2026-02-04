// code by jph
package ch.alpine.tensor.mat.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.mat.sv.SingularValueDecompositionWrap;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.InvertUnlessZero;

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
    InfluenceMatrixSvd influenceMatrixSvd = new InfluenceMatrixSvd(SingularValueDecompositionWrap.of(design));
    Tolerance.CHOP.requireClose(matrix, influenceMatrixSvd.matrix());
    Tensor x = TensorWedge.of(RandomVariate.of(distribution, n, n));
    Tensor a = MatrixExp.of(x);
    a.copy();
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
      InfluenceMatrixQ.INSTANCE.requireMember(influenceMatrix.matrix());
      influenceMatrix.residualMaker();
      Tensor vector = RandomVariate.of(distribution, n);
      Tensor image = influenceMatrix.image(vector);
      VectorQ.requireLength(image, vector.length());
      Chop._10.requireClose(Total.ofVector(influenceMatrix.leverages()), RealScalar.of(r));
      String string = influenceMatrix.toString();
      assertTrue(string.startsWith("InfluenceMatrix["));
      InfluenceMatrixSvd influenceMatrixSvd = new InfluenceMatrixSvd(SingularValueDecompositionWrap.of(design));
      Tolerance.CHOP.requireClose(influenceMatrix.matrix(), influenceMatrixSvd.matrix());
    }
  }

  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);

  /** @param scalar
   * @return numerical 0 or 1
   * @see InvertUnlessZero */
  private static Scalar unitize_chop(Scalar scalar) {
    return Tolerance.CHOP.isZero(scalar) ? _0 : _1;
  }

  @Test
  void testRequireUnit() {
    Scalar scalar = RealScalar.of(1.0 + 1e-13);
    Scalar mapped = InfluenceMatrixSvd.requireUnit(scalar);
    Clips.unit().requireInside(mapped);
  }

  @Test
  void testRequireUnitFail() {
    Scalar scalar = RealScalar.of(1.0 + 1e-5);
    assertThrows(Throw.class, () -> InfluenceMatrixSvd.requireUnit(scalar));
  }

  @Test
  void testUnitizeChop() {
    assertEquals(unitize_chop(RealScalar.of(1e-13)), RealScalar.ZERO);
    assertEquals(unitize_chop(RealScalar.of(1e-11)), RealScalar.ONE);
    assertEquals(unitize_chop(RealScalar.of(123)), RealScalar.ONE);
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(InfluenceMatrixSvd.class.getModifiers()));
  }
}
