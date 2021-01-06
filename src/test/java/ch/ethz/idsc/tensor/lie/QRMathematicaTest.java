// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.Arrays;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QRMathematicaTest extends TestCase {
  public void testSimple() {
    Tensor a = Tensors.fromString("{{1, 2, 3}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
    assertEquals(Dimensions.of(a), Arrays.asList(4, 3));
    QRDecomposition qrDecomposition = QRMathematica.wrap(QRDecomposition.of(a));
    assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(3, 3));
    assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(4, 3));
    assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(3, 4));
    Tensor r = qrDecomposition.getR();
    Tensor q = qrDecomposition.getQ();
    Chop._10.requireClose(q.dot(r), a);
    Chop._10.requireClose(ConjugateTranspose.of(q), qrDecomposition.getInverseQ());
  }

  public void testDet() {
    Distribution distribution = UniformDistribution.unit();
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n);
      QRDecomposition qrDecomposition = QRMathematica.wrap(QRDecomposition.of(matrix));
      Chop._10.requireClose(qrDecomposition.det(), Det.of(matrix));
    }
  }

  public void testRectangularBigSmall() {
    Distribution distribution = NormalDistribution.standard();
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(distribution, n, m);
      QRDecomposition qrDecomposition = QRMathematica.wrap(QRDecomposition.of(matrix));
      assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(Math.min(n, m), n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, Math.min(n, m)));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(Math.min(n, m), m));
      SquareMatrixQ.require(qrDecomposition.getR());
      Chop._07.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getInverseQ()));
    }
  }

  public void testRectangularSmallBig() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 3; n < 6; ++n) {
      int m = n + 3;
      Tensor matrix = RandomVariate.of(distribution, n, m);
      QRDecomposition qrDecomposition = QRMathematica.wrap(QRDecomposition.of(matrix));
      Chop._07.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQ()));
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getInverseQ()));
      assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(Math.min(n, m), n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, Math.min(n, m)));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(Math.min(n, m), m));
    }
  }

  public void testSolveComplex() {
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = Entrywise.with(ComplexScalar::of).apply( //
          RandomVariate.of(NormalDistribution.standard(), n, n), //
          RandomVariate.of(NormalDistribution.standard(), n, n));
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 3);
      Tensor sol1 = QRDecomposition.solve(matrix, b, QRSignOperators.STABILITY);
      Tensor sol2 = LinearSolve.of(matrix, b);
      Chop._10.requireClose(sol1, sol2);
    }
  }

  public void testNullFail() {
    AssertFail.of(() -> QRMathematica.wrap(null));
  }
}
