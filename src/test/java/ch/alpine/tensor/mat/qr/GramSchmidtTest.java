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
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class GramSchmidtTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 5, 4);
    QRDecomposition qrDecomposition = Serialization.copy(GramSchmidt.of(matrix));
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    OrthogonalMatrixQ.require(qrDecomposition.getQConjugateTranspose());
  }

  public void testRankDeficientLeastSquares() {
    Distribution distribution = TrapezoidalDistribution.with(0, 1, 2);
    Tensor m1 = RandomVariate.of(distribution, 8, 4);
    Tensor m2 = RandomVariate.of(distribution, 4, 5);
    Tensor matrix = m1.dot(m2);
    assertEquals(MatrixRank.of(matrix), 4);
    Tensor b = RandomVariate.of(distribution, 8);
    Tensor x1 = LeastSquares.usingSvd(matrix, b);
    assertEquals(x1.length(), 5);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor rhs = qrDecomposition.getQConjugateTranspose().dot(b);
    assertEquals(rhs.length(), 4);
    AssertFail.of(() -> qrDecomposition.pseudoInverse());
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
    UnitaryMatrixQ.require(qrDecomposition.getQ());
    UnitaryMatrixQ.require(qrDecomposition.getQConjugateTranspose());
  }

  public void testComplex() {
    Tensor re = RandomVariate.of(NormalDistribution.standard(), 5, 3);
    Tensor im = RandomVariate.of(NormalDistribution.standard(), 5, 3);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    UnitaryMatrixQ.require(qrDecomposition.getQConjugateTranspose());
  }

  public void testComplexLarge() {
    Tensor re = RandomVariate.of(NormalDistribution.standard(), 100, 20);
    Tensor im = RandomVariate.of(NormalDistribution.standard(), 100, 20);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    UnitaryMatrixQ.require(qrDecomposition.getQConjugateTranspose());
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

  public void testDetRect1() {
    QRDecomposition qrDecomposition = GramSchmidt.of(RandomVariate.of(NormalDistribution.standard(), 3, 2));
    assertEquals(qrDecomposition.det(), RealScalar.ZERO);
  }

  public void testDetRect2() {
    assertEquals(GramSchmidt.of(RandomVariate.of(NormalDistribution.standard(), 2, 3)).det(), RealScalar.ZERO);
  }
}
