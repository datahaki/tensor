// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Random;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class InfluenceMatrixImplTest extends TestCase {
  public void testExactRankDefficient7x5() {
    Random random = new Random(3);
    int n = 7;
    int _m = 5;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    for (int count = 0; count < 5; ++count)
      for (int r = 1; r < _m - 1; ++r) {
        Tensor m1 = RandomVariate.of(distribution, random, n, r).map(s -> Quantity.of(s, "m"));
        Tensor m2 = RandomVariate.of(distribution, random, r, _m);
        Tensor design = m1.dot(m2);
        ExactTensorQ.require(design);
        assertEquals(Dimensions.of(design), Arrays.asList(7, 5));
        InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
        SymmetricMatrixQ.require(influenceMatrix.matrix());
        influenceMatrix.residualMaker();
        Tensor vector = RandomVariate.of(distribution, random, n);
        Tensor image = influenceMatrix.image(vector);
        {
          InfluenceMatrixImpl influenceMatrixImpl = (InfluenceMatrixImpl) influenceMatrix;
          assertFalse(influenceMatrixImpl.dotMatrix());
        }
        VectorQ.requireLength(image, vector.length());
        Chop._10.requireClose(Total.ofVector(influenceMatrix.leverages()), RealScalar.of(r));
        String string = influenceMatrix.toString();
        assertTrue(string.startsWith("InfluenceMatrix["));
        SingularValueDecomposition svd = SingularValueDecomposition.of(design);
        assertEquals(Unprotect.getUnitUnique(svd.getU()), Unit.ONE);
        assertEquals(Unprotect.getUnitUnique(svd.values()), Unit.of("m"));
      }
  }

  public void testMixedQuantity() {
    Tensor x = Tensors.fromString("{100[K], 110.0[K], 130[K], 133[K]}");
    Tensor design = VandermondeMatrix.of(x, 2);
    Tensor influe = design.dot(PseudoInverse.of(design));
    {
      assertTrue(IdempotentQ.of(influe));
      // System.out.println("IM=" + Pretty.of(influe.map(Round._2)));
      InfluenceMatrixQ.require(influe);
    }
    {
      InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
      Tolerance.CHOP.requireClose(influe, influenceMatrix.matrix());
      // Tensor matrix = influenceMatrix.matrix();
      // System.out.println("IM=" + Pretty.of(matrix.map(Round._2)));
    }
    // InfluenceMatrix.of(Transpose.of(design));
    // ... does not result in an influence matrix, since have mixed units
    // {
    // InfluenceMatrix influenceMatrix =
    // Tensor matrix = influenceMatrix.matrix();
    // System.out.println("IM=" + Pretty.of(matrix.map(Round._2)));
    // InfluenceMatrixQ.require(matrix);
    // System.out.println(Pretty.of(matrix.dot(matrix).map(Round._2)));
    // assertTrue(IdempotentQ.of(matrix, Chop._07));
    //
    // }
  }

  public void testExact3() throws ClassNotFoundException, IOException {
    int n = 7;
    int m = 3;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    Tensor design = RandomVariate.of(distribution, n, m);
    if (MatrixRank.of(design) == m) {
      InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
      assertTrue(influenceMatrix instanceof InfluenceMatrixImpl);
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
    assertEquals(Unprotect.getUnitUnique(svd.getU()), Unit.ONE);
  }

  public void testExact5() throws ClassNotFoundException, IOException {
    int n = 7;
    int m = 5;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    Tensor design = RandomVariate.of(distribution, n, m);
    if (MatrixRank.of(design) == m) {
      InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
      assertTrue(influenceMatrix instanceof InfluenceMatrixImpl);
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
    assertEquals(Unprotect.getUnitUnique(svd.getU()), Unit.ONE);
  }

  public void testUseMatrixFalse() {
    Distribution distribution = LogNormalDistribution.standard();
    Tensor design = RandomVariate.of(distribution, 10, 2);
    InfluenceMatrixImpl influenceMatrixImpl = (InfluenceMatrixImpl) InfluenceMatrix.of(design);
    influenceMatrixImpl.image(RandomVariate.of(distribution, 10));
    assertFalse(influenceMatrixImpl.dotMatrix());
  }

  public void testUseMatrixTrue() {
    Distribution distribution = LogNormalDistribution.standard();
    Tensor design = RandomVariate.of(distribution, 8, 7);
    InfluenceMatrixImpl influenceMatrixImpl = (InfluenceMatrixImpl) InfluenceMatrix.of(design);
    influenceMatrixImpl.image(RandomVariate.of(distribution, 8));
    assertTrue(influenceMatrixImpl.dotMatrix());
  }

  public void testModifierNonPublic() {
    assertFalse(Modifier.isPublic(InfluenceMatrixImpl.class.getModifiers()));
  }
}
