// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.Random;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Imag;
import junit.framework.TestCase;

public class QRDecompositionTest extends TestCase {
  private static QRDecomposition specialOps(Tensor A) {
    QRDecomposition qr = QRDecomposition.of(A);
    Tensor Q = qr.getQ();
    Tensor Qi = qr.getInverseQ();
    Tensor R = qr.getR();
    assertTrue(Chop._10.close(Q.dot(R), A));
    assertTrue(Chop._10.close(Q.dot(Qi), IdentityMatrix.of(A.length())));
    Scalar qrDet = Det.of(Q).multiply(Det.of(R));
    assertTrue(Chop._10.close(qrDet, Det.of(A)));
    if (Scalars.isZero(Imag.of(qrDet))) {
      assertTrue(Chop._10.close(qrDet, qr.det()));
      // Scalar qrRef = ;
      // System.out.println("---");
      // System.out.println(qrDet);
      // System.out.println(qrRef);
    }
    return qr;
  }

  public void testExampleP32() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { -1, -1, 1 }, //
        { 1, 3, 3 }, //
        { -1, -1, 5 }, //
        { 1, 3, 7 } });
    specialOps(A);
  }

  public void testRandomReal() {
    Random rnd = new Random();
    Tensor A = Tensors.matrix((i, j) -> RealScalar.of(rnd.nextDouble()), 5, 3);
    specialOps(A);
  }

  public void testRandomReal2() {
    Random rnd = new Random();
    Tensor A = Tensors.matrix((i, j) -> RealScalar.of(rnd.nextDouble()), 3, 5);
    specialOps(A);
  }

  public void testRandomRealSquare() {
    Distribution distribution = NormalDistribution.standard();
    for (int d = 1; d <= 10; ++d)
      specialOps(RandomVariate.of(distribution, d, d));
  }

  public void testDiag() {
    Tensor A = DiagonalMatrix.of(Tensors.vector(2, 3, 4));
    specialOps(A);
  }

  public void testDiag2() {
    Tensor A = DiagonalMatrix.of(2, -3, 0, 0, -1e-10, 0, 4e20);
    specialOps(A);
  }

  public void testZeros() {
    Tensor A = Array.zeros(4, 3);
    specialOps(A);
  }

  public void testRandomComplex1() {
    Random rnd = new Random();
    Tensor A = Tensors.matrix((i, j) -> ComplexScalar.of(rnd.nextGaussian(), rnd.nextGaussian()), 5, 3);
    specialOps(A);
  }

  public void testRandomComplex2() {
    Random rnd = new Random();
    Tensor A = Tensors.matrix((i, j) -> ComplexScalar.of(rnd.nextGaussian(), rnd.nextGaussian()), 5, 5);
    specialOps(A);
  }

  public void testHilbert() {
    Tensor matrix = HilbertMatrix.of(4, 7);
    specialOps(matrix);
    QRDecomposition qr = QRDecomposition.of(matrix);
    assertEquals(qr.getR().get(1, 0), RealScalar.ZERO);
    assertEquals(qr.getR().get(2, 0), RealScalar.ZERO);
    assertEquals(qr.getR().get(2, 1), RealScalar.ZERO);
    assertEquals(qr.getR().get(3, 0), RealScalar.ZERO);
    assertEquals(qr.getR().get(3, 1), RealScalar.ZERO);
    assertEquals(qr.getR().get(3, 2), RealScalar.ZERO);
  }

  public void testWikipedia() {
    // example gives symbolic results
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 12, -51, 4 }, { 6, 167, -68 }, { -4, 24, -41 } });
    specialOps(matrix);
  }

  public void testEmpty() {
    try {
      QRDecomposition.of(Tensors.empty());
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
