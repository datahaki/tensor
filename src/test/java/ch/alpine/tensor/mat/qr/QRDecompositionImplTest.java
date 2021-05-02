// code by jph
package ch.alpine.tensor.mat.qr;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QRDecompositionImplTest extends TestCase {
  public void testDegenerate0Square() {
    for (int n = 1; n < 6; ++n) {
      QRDecomposition qrDecomposition = QRDecomposition.of(Array.zeros(n, n));
      assertEquals(qrDecomposition.getQTranspose(), IdentityMatrix.of(n));
      assertEquals(qrDecomposition.getR(), Array.zeros(n, n));
    }
  }

  public void testDegenerate1Square() {
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = DiagonalMatrix.with(UnitVector.of(n, 0));
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      assertEquals(qrDecomposition.getQTranspose(), IdentityMatrix.of(n));
      assertEquals(qrDecomposition.getR(), matrix);
    }
  }

  public void testDegenerateRect() {
    for (int m = 1; m < 6; ++m) {
      int n = m + 2;
      QRDecomposition qrDecomposition = QRDecomposition.of(Array.zeros(n, m));
      assertEquals(qrDecomposition.getQTranspose(), IdentityMatrix.of(n));
      assertEquals(qrDecomposition.getR(), Array.zeros(n, m));
    }
  }

  public void testDimensionsBigSmall() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      assertEquals(Dimensions.of(qrDecomposition.getQTranspose()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, n));
      Tensor r = qrDecomposition.getR();
      assertEquals(Dimensions.of(r), Arrays.asList(n, m));
      Tensor rem = r.extract(m, n);
      Chop.NONE.requireAllZero(rem);
    }
  }

  public void testSmallBigVector() {
    for (int m = 4; m < 7; ++m) {
      int n = m - 2;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      Tolerance.CHOP.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQ()));
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQTranspose()));
      assertEquals(Dimensions.of(qrDecomposition.getQTranspose()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(n, m));
    }
  }

  public void testPseudoInverseRankDeficient() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor base = RandomVariate.of(NormalDistribution.standard(), n, m);
      Tensor mult = RandomVariate.of(NormalDistribution.standard(), m, m + 2);
      Tensor matrix = Dot.of(base, mult);
      QRDecompositionImpl qrDecompositionImpl = //
          new QRDecompositionImpl(matrix, IdentityMatrix.of(n), QRSignOperators.STABILITY);
      AssertFail.of(() -> qrDecompositionImpl.pseudoInverse());
    }
  }

  public void testToString() {
    QRDecomposition qrDecomposition = QRDecomposition.of(HilbertMatrix.of(3, 2));
    assertTrue(qrDecomposition.toString().startsWith("QRDecomposition"));
  }

  public void testBic() {
    Tensor matrix = ResourceData.of("/mat/bic1.csv");
    QRDecompositionImpl qrDecomposition = (QRDecompositionImpl) QRDecomposition.of(matrix);
    Tensor rs = Abs.of(Diagonal.of(qrDecomposition.getR()));
    Scalar max = (Scalar) rs.stream().reduce(Max::of).get();
    double thres = max.number().doubleValue() * 1e-12;
    Chop chop = Chop.below(thres);
    chop.requireAllZero(rs.stream().reduce(Min::of).get());
    AssertFail.of(() -> qrDecomposition.pseudoInverse());
  }

  public void testDecimalScalar() {
    Tensor matrix = HilbertMatrix.of(5, 3).map(N.DECIMAL128);
    QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
    Tensor tensor = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, tensor);
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(QRDecomposition.class.getModifiers()));
    assertFalse(Modifier.isPublic(QRDecompositionImpl.class.getModifiers()));
  }
}
