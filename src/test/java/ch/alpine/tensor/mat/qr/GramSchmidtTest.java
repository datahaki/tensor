// code by jph
package ch.alpine.tensor.mat.qr;

import java.io.IOException;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Abs;
import junit.framework.TestCase;

public class GramSchmidtTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 5, 4);
    QRDecomposition qrDecomposition = Serialization.copy(GramSchmidt.of(matrix));
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    OrthogonalMatrixQ.require(qrDecomposition.getQConjugateTranspose());
  }

  public void testQuantity() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 5, 4).map(s -> Quantity.of(s, "m"));
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    OrthogonalMatrixQ.require(qrDecomposition.getQConjugateTranspose());
  }

  public void testRect() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    OrthogonalMatrixQ.require(qrDecomposition.getQ());
    OrthogonalMatrixQ.require(qrDecomposition.getQConjugateTranspose());
  }

  public void testComplex() {
    Tensor re = RandomVariate.of(NormalDistribution.standard(), 5, 3);
    Tensor im = RandomVariate.of(NormalDistribution.standard(), 5, 3);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
  }

  public void testMixedUnits() {
    Tensor x = Tensors.fromString("{100[K], 110.0[K], 130[K], 133[K]}");
    Tensor design = VandermondeMatrix.of(x, 2);
    QRDecomposition qrDecomposition = GramSchmidt.of(design);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(design, res);
  }

  public void testDet() {
    for (int n = 2; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
      QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
      OrthogonalMatrixQ.require(qrDecomposition.getQ());
      OrthogonalMatrixQ.require(qrDecomposition.getQConjugateTranspose());
      Scalar det1 = qrDecomposition.det();
      Scalar det2 = Det.of(matrix);
      Tolerance.CHOP.requireClose(Abs.FUNCTION.apply(det1), Abs.FUNCTION.apply(det2));
    }
  }

  public void testDetRect() {
    assertEquals(GramSchmidt.of(RandomVariate.of(NormalDistribution.standard(), 3, 2)).det(), RealScalar.ZERO);
    assertEquals(GramSchmidt.of(RandomVariate.of(NormalDistribution.standard(), 2, 3)).det(), RealScalar.ZERO);
  }
}
