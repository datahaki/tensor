// code by jph
package ch.ethz.idsc.tensor.mat.gr;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.SymmetricMatrixQ;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.mat.sv.SingularValueDecomposition;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class InfluenceMatrixSvdTest extends TestCase {
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
    }
  }

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
        TestHelper.requireNonQuantity(svd.getU());
        InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
        InfluenceMatrixQ.require(influenceMatrix.matrix());
        influenceMatrix.residualMaker();
        Tensor vector = RandomVariate.of(distribution, n);
        Tensor image = influenceMatrix.image(vector);
        VectorQ.requireLength(image, vector.length());
        Chop._10.requireClose(Total.ofVector(influenceMatrix.leverages()), RealScalar.of(r));
        String string = influenceMatrix.toString();
        assertTrue(string.startsWith("InfluenceMatrix["));
      }
  }

  public void testProblem0() {
    Tensor design = Tensors.fromString( //
        "{{-304[m], -144[m], 16[m]}, {-19[m], -9[m], 1[m]}, {285[m], 135[m], -15[m]}, {-152[m], -72[m], 8[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    SymmetricMatrixQ.require(influenceMatrix.matrix());
  }

  public void testProblem1() {
    Tensor design = Tensors.fromString( //
        "{{0[m], 0[m], 0[m]}, {0[m], -228[m], 0[m]}, {0[m], -266[m], 0[m]}, {0[m], 342[m], 0[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    SymmetricMatrixQ.require(influenceMatrix.matrix());
  }

  public void testProblem2() {
    Tensor design = Tensors.fromString( //
        "{{0[m], 0[m], 0[m]}, {0[m], 30[m], -285[m]}, {0[m], -32[m], 304[m]}, {0[m], 10[m], -95[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    SymmetricMatrixQ.require(influenceMatrix.matrix());
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(InfluenceMatrixSvd.class.getModifiers()));
  }
}
