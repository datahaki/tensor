// code by jph
package ch.alpine.tensor.mat.qr;

import java.io.IOException;
import java.util.Random;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
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
    Random random = new Random(5);
    for (int n = 2; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, n, n);
      QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
      OrthogonalMatrixQ.require(qrDecomposition.getQ());
      OrthogonalMatrixQ.require(qrDecomposition.getQConjugateTranspose());
      Scalar det1 = qrDecomposition.det();
      Scalar det2 = Det.of(matrix);
      Tolerance.CHOP.requireClose(Abs.FUNCTION.apply(det1), Abs.FUNCTION.apply(det2));
    }
  }

  public void testPInv2x2() {
    Random random = new Random(2);
    for (int n = 0; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, 2 + n, 2 + n / 2);
      QRDecomposition gramSchmidt = GramSchmidt.of(matrix);
      Tensor pinv1 = gramSchmidt.pseudoInverse();
      Tensor pinv2 = PseudoInverse.of(SingularValueDecomposition.of(matrix));
      Tolerance.CHOP.requireClose(pinv1, pinv2);
    }
  }

  public void testPInv() {
    Random random = new Random(1); // 5 yields sigma = {0,1,2}
    for (int n = 0; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, 3 + n, 3);
      int m = Unprotect.dimension1(matrix);
      QRDecomposition qrDecomposi = QRDecomposition.of(matrix);
      Tensor pinv = PseudoInverse.of(matrix);
      Tolerance.CHOP.requireAllZero(qrDecomposi.getR().extract(m, qrDecomposi.getR().length()));
      // System.out.println(Dimensions.of(qrDecomposi.getQConjugateTranspose()));
      Tensor actu = qrDecomposi.pseudoInverse();
      TestHelper.checkPInv(pinv, qrDecomposi.getR().extract(0, m), qrDecomposi.getQConjugateTranspose().extract(0, m));
      Tolerance.CHOP.requireClose(actu, pinv);
      QRDecomposition gramSchmidt = GramSchmidt.of(matrix);
      assertEquals(gramSchmidt.sigma().length, 3);
      TestHelper.checkPInv(pinv, gramSchmidt.getR(), gramSchmidt.getQConjugateTranspose());
      Chop._08.requireClose(pinv, LinearSolve.of(gramSchmidt.getR(), gramSchmidt.getQConjugateTranspose()));
      Chop._08.requireClose(pinv, gramSchmidt.pseudoInverse());
      Tensor pinv1 = gramSchmidt.pseudoInverse();
      Tensor pinv2 = PseudoInverse.of(SingularValueDecomposition.of(matrix));
      pinv1.add(pinv2);
    }
  }

  public void testDetRect() {
    // FIXME
    // assertEquals(GramSchmidt.of(RandomVariate.of(NormalDistribution.standard(), 3, 2)).det(), RealScalar.ZERO);
    assertEquals(GramSchmidt.of(RandomVariate.of(NormalDistribution.standard(), 2, 3)).det(), RealScalar.ZERO);
  }
}
