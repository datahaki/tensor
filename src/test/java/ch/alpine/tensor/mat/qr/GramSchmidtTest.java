// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.mat.sv.SingularValueDecompositionWrap;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;

class GramSchmidtTest {
  private static void _checkPInv(Tensor pInv, Tensor r, Tensor qInv) {
    Chop._08.requireClose(pInv, LinearSolve.of(r, qInv));
    Chop._08.requireClose(pInv, Inverse.of(r).dot(qInv));
  }

  @RepeatedTest(3)
  void testSimple() throws ClassNotFoundException, IOException {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 5, 4);
    QRDecomposition qrDecomposition = Serialization.copy(GramSchmidt.of(matrix));
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    OrthogonalMatrixQ.INSTANCE.requireMember(qrDecomposition.getQConjugateTranspose());
  }

  @Test
  void testRankDeficientLeastSquares() {
    Random random = new Random(1); // 5 yields sigma = {0,1,2}
    Distribution distribution = TrapezoidalDistribution.with(0, 1, 2);
    Tensor m1 = RandomVariate.of(distribution, random, 8, 4);
    Tensor m2 = RandomVariate.of(distribution, random, 4, 5);
    Tensor matrix = m1.dot(m2);
    assertEquals(Dimensions.of(matrix), List.of(8, 5));
    assertEquals(MatrixRank.of(matrix), 4);
    Tensor b = RandomVariate.of(distribution, random, 8);
    Tensor x1 = LeastSquares.usingSvd(matrix, b);
    assertEquals(x1.length(), 5);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor Qt = qrDecomposition.getQConjugateTranspose();
    assertEquals(Dimensions.of(Qt), List.of(4, 8));
    Tensor rhs = Qt.dot(b);
    assertEquals(rhs.length(), 4);
    assertThrows(ArrayIndexOutOfBoundsException.class, qrDecomposition::pseudoInverse);
  }

  @Test
  void testQuantity() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 5, 4).map(s -> Quantity.of(s, "m"));
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    OrthogonalMatrixQ.INSTANCE.requireMember(qrDecomposition.getQConjugateTranspose());
  }

  @Test
  void testRect() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    OrthogonalMatrixQ.INSTANCE.requireMember(qrDecomposition.getQ());
    OrthogonalMatrixQ.INSTANCE.requireMember(qrDecomposition.getQConjugateTranspose());
    UnitaryMatrixQ.INSTANCE.requireMember(qrDecomposition.getQ());
    UnitaryMatrixQ.INSTANCE.requireMember(qrDecomposition.getQConjugateTranspose());
  }

  @Test
  void testComplex() {
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, 5, 3);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    UnitaryMatrixQ.INSTANCE.requireMember(qrDecomposition.getQConjugateTranspose());
  }

  @Test
  void testComplexLarge() {
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, 100, 20);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    UnitaryMatrixQ.INSTANCE.requireMember(qrDecomposition.getQConjugateTranspose());
  }

  @Test
  void testMixedUnits() {
    Tensor x = Tensors.fromString("{100[K], 110.0[K], 130[K], 133[K]}");
    Tensor design = VandermondeMatrix.of(x, 2);
    QRDecomposition qrDecomposition = GramSchmidt.of(design);
    Tensor res = qrDecomposition.getQ().dot(qrDecomposition.getR());
    Tolerance.CHOP.requireClose(design, res);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5 })
  void testDet(int n) {
    Random random = new Random(5);
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, n, n);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    OrthogonalMatrixQ.INSTANCE.requireMember(qrDecomposition.getQ());
    OrthogonalMatrixQ.INSTANCE.requireMember(qrDecomposition.getQConjugateTranspose());
    Scalar det1 = qrDecomposition.det();
    Scalar det2 = Det.of(matrix);
    Tolerance.CHOP.requireClose(Abs.FUNCTION.apply(det1), Abs.FUNCTION.apply(det2));
  }

  @ParameterizedTest
  @ValueSource(ints = { 0, 1, 2, 4, 5 })
  void testPInv2x2(int n) {
    Random random = new Random(2);
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, 2 + n, 2 + n / 2);
    QRDecomposition gramSchmidt = GramSchmidt.of(matrix);
    Tensor pinv1 = gramSchmidt.pseudoInverse();
    Tensor pinv2 = PseudoInverse.of(SingularValueDecompositionWrap.of(matrix));
    Tolerance.CHOP.requireClose(pinv1, pinv2);
  }

  @ParameterizedTest
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  void testPInv(int n) {
    Random random = new Random(1); // 5 yields sigma = {0,1,2}
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, 3 + n, 3);
    int m = Unprotect.dimension1(matrix);
    QRDecomposition qrDecomposi = QRDecompositionWrap.of(matrix);
    Tensor pinv = PseudoInverse.of(matrix);
    Tolerance.CHOP.requireAllZero(qrDecomposi.getR().extract(m, qrDecomposi.getR().length()));
    // System.out.println(Dimensions.of(qrDecomposi.getQConjugateTranspose()));
    Tensor actu = qrDecomposi.pseudoInverse();
    _checkPInv(pinv, qrDecomposi.getR().extract(0, m), qrDecomposi.getQConjugateTranspose().extract(0, m));
    Tolerance.CHOP.requireClose(actu, pinv);
    QRDecomposition gramSchmidt = GramSchmidt.of(matrix);
    assertEquals(gramSchmidt.sigma().length, 3);
    _checkPInv(pinv, gramSchmidt.getR(), gramSchmidt.getQConjugateTranspose());
    Chop._08.requireClose(pinv, LinearSolve.of(gramSchmidt.getR(), gramSchmidt.getQConjugateTranspose()));
    Chop._08.requireClose(pinv, gramSchmidt.pseudoInverse());
    Tensor pinv1 = gramSchmidt.pseudoInverse();
    Tensor pinv2 = PseudoInverse.of(SingularValueDecompositionWrap.of(matrix));
    pinv1.add(pinv2);
    UnitaryMatrixQ.INSTANCE.requireMember(gramSchmidt.getQConjugateTranspose());
  }

  @Test
  void testDetRect1() {
    QRDecomposition qrDecomposition = GramSchmidt.of(RandomVariate.of(NormalDistribution.standard(), 3, 2));
    assertEquals(qrDecomposition.det(), RealScalar.ZERO);
  }

  @Test
  void testDetRect2() {
    assertEquals(GramSchmidt.of(RandomVariate.of(NormalDistribution.standard(), 2, 3)).det(), RealScalar.ZERO);
  }

  @Test
  void testNonOrdered() {
    Tensor matrix = Tensors.fromString("{{0,0,0,1},{0,0,1,0}}");
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    Tensor r = qrDecomposition.getR();
    assertEquals(r.get(0), UnitVector.of(4, 2));
    assertEquals(r.get(1), UnitVector.of(4, 3));
    // QRDecomposition of = QRDecomposition.of(matrix);
    // System.out.println(of.getR());
  }
}
