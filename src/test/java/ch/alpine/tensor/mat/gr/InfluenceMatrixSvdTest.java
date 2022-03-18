// code by jph
package ch.alpine.tensor.mat.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;

public class InfluenceMatrixSvdTest {
  @Test
  public void testRankDeficient() {
    int n = 7;
    int _m = 5;
    Distribution distribution = NormalDistribution.standard();
    for (int r = 1; r < _m - 1; ++r) {
      Tensor m1 = RandomVariate.of(distribution, n, r);
      Tensor m2 = RandomVariate.of(distribution, r, _m);
      Tensor design = m1.dot(m2);
      InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
      influenceMatrix.image(RandomVariate.of(distribution, n));
      Tensor matrix = influenceMatrix.matrix();
      Tensor zeros = Dot.of(influenceMatrix.residualMaker(), matrix);
      Tolerance.CHOP.requireAllZero(zeros);
      InfluenceMatrixSvd influenceMatrixSvd = new InfluenceMatrixSvd(SingularValueDecomposition.of(design));
      Tolerance.CHOP.requireClose(influenceMatrix.matrix(), influenceMatrixSvd.matrix());
    }
  }

  @Test
  public void testSvdWithQuantity() {
    int n = 4;
    int _m = 3;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    for (int count = 0; count < 10; ++count)
      for (int r = 1; r < _m - 1; ++r) {
        Tensor m1 = RandomVariate.of(distribution, n, r).map(s -> Quantity.of(s, "m"));
        Tensor m2 = RandomVariate.of(distribution, r, _m);
        Tensor design = m1.dot(m2);
        ExactTensorQ.require(design);
        SingularValueDecomposition svd = SingularValueDecomposition.of(design);
        assertEquals(Unprotect.getUnitUnique(svd.getU()), Unit.ONE);
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
  public void testProblem0() {
    Tensor design = Tensors.fromString( //
        "{{-304[m], -144[m], 16[m]}, {-19[m], -9[m], 1[m]}, {285[m], 135[m], -15[m]}, {-152[m], -72[m], 8[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    SymmetricMatrixQ.require(influenceMatrix.matrix());
  }

  @Test
  public void testProblem1() {
    Tensor design = Tensors.fromString( //
        "{{0[m], 0[m], 0[m]}, {0[m], -228[m], 0[m]}, {0[m], -266[m], 0[m]}, {0[m], 342[m], 0[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    SymmetricMatrixQ.require(influenceMatrix.matrix());
  }

  @Test
  public void testProblem2() {
    Tensor design = Tensors.fromString( //
        "{{0[m], 0[m], 0[m]}, {0[m], 30[m], -285[m]}, {0[m], -32[m], 304[m]}, {0[m], 10[m], -95[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    SymmetricMatrixQ.require(influenceMatrix.matrix());
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(InfluenceMatrixSvd.class.getModifiers()));
  }
}
