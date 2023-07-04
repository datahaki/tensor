// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

class QRDecompositionImplTest {
  @Test
  void testDegenerate0Square() {
    for (int n = 1; n < 6; ++n) {
      QRDecomposition qrDecomposition = QRDecomposition.of(Array.zeros(n, n));
      assertEquals(qrDecomposition.getQConjugateTranspose(), IdentityMatrix.of(n));
      assertEquals(qrDecomposition.getR(), Array.zeros(n, n));
    }
  }

  @Test
  void testDegenerate1Square() {
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = DiagonalMatrix.with(UnitVector.of(n, 0));
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      assertEquals(qrDecomposition.getQConjugateTranspose(), IdentityMatrix.of(n));
      assertEquals(qrDecomposition.getR(), matrix);
    }
  }

  @Test
  void testDegenerateRect() {
    for (int m = 1; m < 6; ++m) {
      int n = m + 2;
      QRDecomposition qrDecomposition = QRDecomposition.of(Array.zeros(n, m));
      assertEquals(qrDecomposition.getQConjugateTranspose(), IdentityMatrix.of(n));
      assertEquals(qrDecomposition.getR(), Array.zeros(n, m));
    }
  }

  @Test
  void testDimensionsBigSmall() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      assertEquals(Dimensions.of(qrDecomposition.getQConjugateTranspose()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, n));
      Tensor r = qrDecomposition.getR();
      assertEquals(Dimensions.of(r), Arrays.asList(n, m));
      Tensor rem = r.extract(m, n);
      Chop.NONE.requireAllZero(rem);
    }
  }

  @Test
  void testSmallBigVector() {
    for (int m = 4; m < 7; ++m) {
      int n = m - 2;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      Tolerance.CHOP.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQ()));
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQConjugateTranspose()));
      assertEquals(Dimensions.of(qrDecomposition.getQConjugateTranspose()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(n, m));
    }
  }

  @Test
  void testPseudoInverseRankDeficient() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor base = RandomVariate.of(NormalDistribution.standard(), n, m);
      Tensor mult = RandomVariate.of(NormalDistribution.standard(), m, m + 2);
      Tensor matrix = Dot.of(base, mult);
      QRDecompositionImpl qrDecompositionImpl = //
          new QRDecompositionImpl(matrix, IdentityMatrix.of(n), QRSignOperators.STABILITY);
      assertThrows(Throw.class, qrDecompositionImpl::pseudoInverse);
    }
  }

  @Test
  void testToString() {
    QRDecomposition qrDecomposition = QRDecomposition.of(HilbertMatrix.of(3, 2));
    assertTrue(qrDecomposition.toString().startsWith("QRDecomposition"));
  }

  @Test
  void testBic() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/pi/bic1.csv");
    QRDecompositionImpl qrDecomposition = (QRDecompositionImpl) QRDecomposition.of(matrix);
    Tensor rs = Diagonal.of(qrDecomposition.getR()).map(Abs.FUNCTION);
    Scalar max = (Scalar) rs.stream().reduce(Max::of).get();
    double thres = max.number().doubleValue() * 1e-12;
    Chop chop = Chop.below(thres);
    chop.requireAllZero(rs.stream().reduce(Min::of).get());
    assertThrows(Throw.class, qrDecomposition::pseudoInverse);
  }

  @Test
  void testDecimalScalar() {
    Tensor matrix = HilbertMatrix.of(5, 3).map(N.DECIMAL128);
    QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
    Tensor tensor = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, tensor);
  }

  @Test
  void testPackageVisibility() {
    assertTrue(Modifier.isPublic(QRDecomposition.class.getModifiers()));
    assertFalse(Modifier.isPublic(QRDecompositionImpl.class.getModifiers()));
  }
}
