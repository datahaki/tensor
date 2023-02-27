// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.pd.Orthogonalize;
import ch.alpine.tensor.mat.qr.GramSchmidt;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Round;

class NullSpaceTest {
  private static void _checkZeros(Tensor zeros) {
    int n = zeros.length();
    Tensor nul = NullSpace.usingSvd(zeros);
    assertEquals(Dimensions.of(nul), Arrays.asList(n, n));
    assertEquals(nul.get(0, 0), RealScalar.ONE);
    assertEquals(nul, IdentityMatrix.of(n));
  }

  @RepeatedTest(10)
  void testZerosUsingSvd(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    _checkZeros(Array.zeros(n, n));
    _checkZeros(N.DOUBLE.of(Array.zeros(n, n)));
  }

  @Test
  void testRowReduce() {
    Tensor m = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}}");
    Tensor r = NullSpace.of(m);
    for (Tensor v : r)
      assertEquals(m.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(r), Arrays.asList(2, 4));
    ExactTensorQ.require(r);
  }

  @Test
  void testZeros2() {
    Tensor m = Array.zeros(5, 5);
    Tensor r = NullSpace.of(m);
    assertEquals(r, IdentityMatrix.of(5));
    ExactTensorQ.require(r);
  }

  @Test
  void testIdentity() {
    Tensor m = IdentityMatrix.of(5);
    Tensor r = NullSpace.of(m);
    assertEquals(r, Tensors.empty());
    ExactTensorQ.require(r);
  }

  @Test
  void testIdentityReversed() {
    Tensor m = Reverse.of(IdentityMatrix.of(5));
    Tensor r = NullSpace.of(m);
    assertEquals(r, Tensors.empty());
    ExactTensorQ.require(r);
  }

  @Test
  void testWikipediaKernel() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { 1, 0, -3, 0, 2, -8 }, //
        { 0, 1, 5, 0, -1, 4 }, //
        { 0, 0, 0, 1, 7, -9 }, //
        { 0, 0, 0, 0, 0, 0 } //
    });
    Tensor nul = NullSpace.of(A);
    for (Tensor v : nul)
      assertEquals(A.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(nul), Arrays.asList(3, 6));
    ExactTensorQ.require(nul);
  }

  @Test
  void testSome1() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { -1, -2, -1 }, //
        { -3, 1, 5 }, //
        { 3, 6, 3 }, //
        { 1, 2, 1 } //
    });
    Tensor nul = NullSpace.of(A);
    for (Tensor v : nul)
      assertEquals(A.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(nul), Arrays.asList(1, 3));
    ExactTensorQ.require(nul);
    Tensor nrr = NullSpace.usingRowReduce(A);
    assertEquals(nul, nrr);
  }

  @Test
  void testSome2() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { 1, 0, -3, 0, 2, -8 }, //
        { 0, 0, 1, 0, -1, 4 }, //
        { 0, 0, 0, 1, 7, -9 }, //
        { 0, 0, 0, 0, 0, 0 } //
    });
    Tensor nul = NullSpace.of(A);
    for (Tensor v : nul)
      assertEquals(A.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(nul), Arrays.asList(3, 6));
    ExactTensorQ.require(nul);
  }

  @Test
  void testSome3() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 1, 0, -1, 4 }, //
        { 0, 0, 0, 0, 1, -9 }, //
        { 1, 9, -3, 1, 2, -8 } //
    });
    Tensor nul = NullSpace.of(A);
    for (Tensor v : nul)
      assertEquals(A.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(nul), Arrays.asList(3, 6));
    ExactTensorQ.require(nul);
  }

  @Test
  void testSingleVector() {
    Tensor nullsp = NullSpace.of(Tensors.of(Tensors.vector(0.0, 1.0)));
    Chop._12.requireClose(nullsp, Tensors.of(Tensors.vector(1.0, 0.0)));
  }

  @Test
  void testComplex() {
    // {{17/101-32/101*I, 0, 1, -99/101+20/101*I},
    // {106/505-253/505*I, 1, 0, -89/101+19/101*I}}
    Tensor m = Tensors.fromString("{{1+3*I, 2, 3, 4+I}, {5, 6+I, 7, 8}}");
    Tensor nul = NullSpace.of(m);
    // {{1, 0, 17/13+32/13*I, -23/13-28/13*I},
    // {0, 1, -98/65+9/65*I, 37/65-16/65*I}}
    assertEquals(Dimensions.of(nul), Arrays.asList(2, 4));
    for (Tensor v : nul)
      assertEquals(m.dot(v), Array.zeros(2));
    ExactTensorQ.require(nul);
  }

  @Test
  void testMatsim() {
    Tensor matrix = Tensors.matrixDouble(new double[][] { //
        { 1.0, -0.2, -0.8 }, //
        { -0.2, 1.0, -0.8 }, //
        { -0.2, -0.8, 1.0 } });
    Tensor nullspace = NullSpace.of(matrix);
    assertEquals(Dimensions.of(nullspace), Arrays.asList(1, 3));
    assertTrue(Chop._14.isClose(nullspace.get(0), Vector2Norm.NORMALIZE.apply(Tensors.vector(1, 1, 1))) //
        || Chop._14.isClose(nullspace.get(0), Vector2Norm.NORMALIZE.apply(Tensors.vector(-1, -1, -1))));
  }

  @Test
  void testQuantity() {
    Tensor mat = Tensors.of(QuantityTensor.of(Tensors.vector(1, 2), "m"));
    Tensor nul = NullSpace.of(mat);
    assertEquals(nul, Tensors.fromString("{{1, -1/2}}"));
    ExactTensorQ.require(nul);
  }

  @Test
  void testQuantityMixed() {
    Tensor mat = Tensors.of( //
        Tensors.of(Quantity.of(-2, "m"), Quantity.of(1, "kg"), Quantity.of(3, "s")));
    Tensor nul = NullSpace.of(mat);
    Chop.NONE.requireAllZero(mat.dot(Transpose.of(nul)));
  }

  @Test
  void testQuantityMixed2() {
    Tensor mat = Tensors.of( //
        Tensors.of(Quantity.of(-2, "m"), Quantity.of(1, "kg"), Quantity.of(3, "s")), //
        Tensors.of(Quantity.of(-4, "m"), Quantity.of(2, "kg"), Quantity.of(6, "s")), //
        Tensors.of(Quantity.of(+1, "m"), Quantity.of(3, "kg"), Quantity.of(1, "s")) //
    );
    Tensor nul = NullSpace.of(mat);
    assertEquals(Dimensions.of(nul), Arrays.asList(1, 3));
    Chop.NONE.requireAllZero(mat.dot(Transpose.of(nul)));
  }

  @Test
  void testRectangle2x3() {
    Tensor matrix = Tensors.fromString("{{1, 0, 0}, {0, 0, 0}}");
    Tensor tensor = NullSpace.of(matrix);
    assertEquals(tensor.get(0), UnitVector.of(3, 1));
    assertEquals(tensor.get(1), UnitVector.of(3, 2));
    assertThrows(Throw.class, () -> Det.of(matrix));
  }

  @Test
  void testRectangle3x2() {
    Tensor matrix = Tensors.fromString("{{1, 0}, {0, 0}, {0, 0}}");
    Tensor tensor = NullSpace.of(matrix);
    assertEquals(tensor.get(0), UnitVector.of(2, 1));
    assertThrows(Throw.class, () -> Det.of(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 3, 4, 5 })
  void testZeros(int d) {
    Tensor matrix = Array.zeros(3, d);
    Tensor id = IdentityMatrix.of(d);
    assertEquals(id, NullSpace.of(matrix));
    assertEquals(id, NullSpace.usingQR(matrix));
    assertEquals(id, NullSpace.usingQR(matrix.map(N.DOUBLE)));
  }

  @Test
  void testExtended() {
    Distribution distribution = CauchyDistribution.of(-1, 2);
    RandomGenerator random = new Random(1344343);
    int n = 10;
    for (int d = 1; d < n; ++d) {
      Tensor matrix = RandomVariate.of(distribution, random, n, d);
      assertEquals(NullSpace.of(matrix), Tensors.empty());
      Tensor mt = Transpose.of(matrix);
      {
        Tensor nullspace = NullSpace.of(mt);
        assertEquals(Dimensions.of(nullspace), Arrays.asList(n - d, n));
        Chop._08.requireAllZero(MatrixDotTranspose.of(mt, nullspace));
      }
      {
        Tensor nullspace = NullSpace.usingQR(mt);
        assertEquals(Dimensions.of(nullspace), Arrays.asList(n - d, n));
        Chop._10.requireAllZero(MatrixDotTranspose.of(mt, nullspace));
        Chop._10.requireClose(MatrixDotTranspose.of(nullspace, nullspace), IdentityMatrix.of(n - d));
      }
      {
        Tensor nullspace = NullSpace.of(mt);
        assertEquals(Dimensions.of(nullspace), Arrays.asList(n - d, n));
        Chop._10.requireAllZero(MatrixDotTranspose.of(mt, nullspace));
      }
    }
  }

  @Test
  void testGaussScalar() {
    int prime = 7879;
    RandomGenerator random = new Random();
    Tensor matrix = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(), prime), 3, 7);
    Tensor nullsp = NullSpace.of(matrix);
    assertEquals(nullsp.length(), 4);
    for (Tensor vector : nullsp)
      Chop.NONE.requireAllZero(Dot.of(matrix, vector));
  }

  @Test
  void testDecimalScalar() {
    Tensor matrix = HilbertMatrix.of(3, 5).map(N.DECIMAL128);
    Tensor ns = NullSpace.of(matrix);
    Tolerance.CHOP.requireAllZero(matrix.dot(Transpose.of(ns)));
  }

  @RepeatedTest(6)
  void testNullSpaceReal(RepetitionInfo repetitionInfo) {
    int r = 2;
    int n = 5;
    int c = r + repetitionInfo.getCurrentRepetition() - 1;
    Tensor v = RandomVariate.of(NormalDistribution.standard(), r, n);
    Tensor w = RandomVariate.of(NormalDistribution.standard(), c, r);
    Tensor matrix = w.dot(v);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    int rank = qrDecomposition.getR().length();
    assertEquals(rank, r);
    Tensor augm = Join.of(qrDecomposition.getR(), IdentityMatrix.of(n));
    Tensor frame = Orthogonalize.of(augm).extract(rank, n);
    Tensor verify = matrix.dot(ConjugateTranspose.of(frame));
    Tolerance.CHOP.requireAllZero(verify);
    Tolerance.CHOP.requireAllZero(MatrixDotTranspose.of(matrix, NullSpace.of(matrix)));
  }

  @RepeatedTest(6)
  void testNullSpaceComplex(RepetitionInfo repetitionInfo) {
    int r = 2;
    int n = 5;
    int c = r + repetitionInfo.getCurrentRepetition() - 1;
    Tensor v = RandomVariate.of(ComplexNormalDistribution.STANDARD, r, n);
    Tensor w = RandomVariate.of(ComplexNormalDistribution.STANDARD, c, r);
    Tensor matrix = w.dot(v);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    int rank = qrDecomposition.getR().length();
    assertEquals(rank, r);
    Tensor augm = Join.of(qrDecomposition.getR(), IdentityMatrix.of(n));
    Tensor frame = Orthogonalize.of(augm).extract(rank, n);
    Tensor verify = matrix.dot(ConjugateTranspose.of(frame));
    Tolerance.CHOP.requireAllZero(verify);
    Tolerance.CHOP.requireAllZero(MatrixDotTranspose.of(matrix, NullSpace.of(matrix)));
  }

  @RepeatedTest(6)
  void testNullSpaceComplexExact(RepetitionInfo repetitionInfo) {
    int r = 2;
    int n = 5;
    int c = r + repetitionInfo.getCurrentRepetition() - 1;
    Tensor v = RandomVariate.of(ComplexNormalDistribution.STANDARD, r, n).map(RealScalar.of(10)::multiply).map(Round.FUNCTION);
    Tensor w = RandomVariate.of(ComplexNormalDistribution.STANDARD, c, r).map(RealScalar.of(10)::multiply).map(Round.FUNCTION);
    Tensor matrix = w.dot(v);
    Tensor ns = NullSpace.of(matrix);
    ExactTensorQ.require(ns);
    Tolerance.CHOP.requireAllZero(matrix.dot(Transpose.of(ns)));
  }

  @Test
  void testFailScalar() {
    assertThrows(Throw.class, () -> NullSpace.of(RealScalar.ONE));
  }

  @Test
  void testFailVector() {
    assertThrows(Throw.class, () -> NullSpace.of(Tensors.vector(1, 2, 3, 1)));
  }

  @Test
  void testFailRank3() {
    assertThrows(ClassCastException.class, () -> NullSpace.of(LeviCivitaTensor.of(3)));
  }
}
