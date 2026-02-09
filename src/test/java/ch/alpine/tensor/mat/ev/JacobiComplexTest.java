// code by jph
package ch.alpine.tensor.mat.ev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ComplexDiskUniformDistribution;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;
import test.wrap.EigensystemQ;

class JacobiComplexTest {
  @RepeatedTest(5)
  void testHermitian(RepetitionInfo repetitionInfo) {
    Distribution distribution = TriangularDistribution.with(0, 1);
    int n = repetitionInfo.getCurrentRepetition();
    Tensor real = Symmetrize.of(RandomVariate.of(distribution, n, n));
    Tensor imag = TensorWedge.of(RandomVariate.of(distribution, n, n));
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(real, imag);
    HermitianMatrixQ.INSTANCE.requireMember(matrix);
    JacobiComplex jacobiComplex = new JacobiComplex(matrix);
    Eigensystem eigensystem = jacobiComplex.solve();
    new EigensystemQ(matrix).require(eigensystem);
    Chop.NONE.requireAllZero(eigensystem.values().maps(Im.FUNCTION));
    Tensor h = Tensors.matrix(jacobiComplex.H);
    Tolerance.CHOP.requireClose(DiagonalMatrix.with(Diagonal.of(h)), h);
    // Tolerance.CHOP.requireClose( //
    // BasisTransform.ofMatrix(h, jacobiComplex.vectors()), //
    // matrix);
  }

  @Test
  void testComplex() {
    Tensor matrix = Tensors.fromString("{{2, 3-3*I}, {3+3*I, 5}}");
    HermitianMatrixQ.INSTANCE.requireMember(matrix);
    Scalar det = Det.of(matrix);
    assertEquals(det, RealScalar.of(-8));
    Tensor ns8 = NullSpace.of(matrix.subtract(IdentityMatrix.of(2).multiply(RealScalar.of(8))));
    assertEquals(ns8.length(), 1);
    Tensor ns1 = NullSpace.of(matrix.subtract(IdentityMatrix.of(2).multiply(RealScalar.of(-1))));
    assertEquals(ns1.length(), 1);
    Eigensystem eigensystem = Eigensystem.ofHermitian(matrix);
    new EigensystemQ(matrix).require(eigensystem);
    Tolerance.CHOP.requireClose(eigensystem.decreasing().values(), Tensors.vector(8, -1));
    Eigensystem eigensystem2 = Eigensystem.of(matrix);
    new EigensystemQ(matrix).require(eigensystem2);
  }

  @Test
  void testComplexUnits() {
    Tensor matrix = Tensors.fromString("{{2[m], 3-3*I[m]}, {3+3*I[m], 5[m]}}");
    Eigensystem eigensystem = Eigensystem.ofHermitian(matrix);
    new EigensystemQ(matrix).require(eigensystem);
    EigensystemQ eigensystemQ = new EigensystemQ(matrix);
    eigensystemQ.require(Eigensystems._2(matrix));
  }

  @RepeatedTest(5)
  void testRealComparison(RepetitionInfo repetitionInfo) {
    Distribution distribution = TriangularDistribution.with(0, 1);
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = Symmetrize.of(RandomVariate.of(distribution, n, n));
    Eigensystem e1 = Eigensystem.ofHermitian(matrix);
    new EigensystemQ(matrix).require(e1);
    Eigensystem e2 = Eigensystem.ofSymmetric(matrix);
    new EigensystemQ(matrix).require(e2);
    Tolerance.CHOP.requireClose(e1.decreasing().values(), e2.decreasing().values());
  }

  @RepeatedTest(5)
  void testHermitian2(RepetitionInfo repetitionInfo) {
    Distribution distribution = TriangularDistribution.with(0, 1);
    int n = repetitionInfo.getCurrentRepetition();
    Tensor real = Symmetrize.of(RandomVariate.of(distribution, n, n));
    Tensor imag = TensorWedge.of(RandomVariate.of(distribution, n, n));
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(real, imag);
    HermitianMatrixQ.INSTANCE.requireMember(matrix);
    Eigensystem eigensystem = JacobiComplex.of(matrix);
    new EigensystemQ(matrix).require(eigensystem);
    JacobiComplex jacobiComplex = new JacobiComplex(matrix);
    jacobiComplex.solve();
    Chop.NONE.requireAllZero(eigensystem.values().maps(Im.FUNCTION));
    Tensor h = Tensors.matrix(jacobiComplex.H);
    Tolerance.CHOP.requireClose(DiagonalMatrix.with(Diagonal.of(h)), h);
  }

  @Test
  void testComplex2() {
    Tensor matrix = Tensors.fromString("{{2, 3-3*I}, {3+3*I, 5}}");
    HermitianMatrixQ.INSTANCE.requireMember(matrix);
    Scalar det = Det.of(matrix);
    assertEquals(det, RealScalar.of(-8));
    Tensor ns8 = NullSpace.of(matrix.subtract(IdentityMatrix.of(2).multiply(RealScalar.of(8))));
    assertEquals(ns8.length(), 1);
    Tensor ns1 = NullSpace.of(matrix.subtract(IdentityMatrix.of(2).multiply(RealScalar.of(-1))));
    assertEquals(ns1.length(), 1);
    Eigensystem eigensystem = Eigensystem.ofHermitian(matrix);
    new EigensystemQ(matrix).require(eigensystem);
    Tolerance.CHOP.requireClose(eigensystem.decreasing().values(), Tensors.vector(8, -1));
    EigensystemQ eigensystemQ = new EigensystemQ(matrix);
    eigensystemQ.require(Eigensystem.ofHermitian(matrix));
    eigensystemQ.require(Eigensystem.of(matrix));
    eigensystemQ.require(Eigensystems._2(matrix));
  }

  @RepeatedTest(5)
  void testRealComparison2(RepetitionInfo repetitionInfo) {
    Distribution distribution = TriangularDistribution.with(0, 1);
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = Symmetrize.of(RandomVariate.of(distribution, n, n));
    EigensystemQ eigensystemQ = new EigensystemQ(matrix);
    Eigensystem e1 = Eigensystem.ofHermitian(matrix);
    eigensystemQ.require(e1);
    Eigensystem e2 = Eigensystem.ofSymmetric(matrix);
    eigensystemQ.require(e2);
    Tolerance.CHOP.requireClose(e1.decreasing().values(), e2.decreasing().values());
  }

  @RepeatedTest(10)
  void testHermitan(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor prelim = RandomVariate.of(ComplexDiskUniformDistribution.of(1), n, n);
    Tensor matrix = prelim.add(ConjugateTranspose.of(prelim));
    HermitianMatrixQ.INSTANCE.requireMember(matrix);
    EigensystemQ eigensystemQ = new EigensystemQ(matrix);
    Eigensystem eigensystem = Eigensystem.of(matrix);
    eigensystemQ.require(eigensystem);
  }

  @Test
  void testPackage() {
    assertFalse(Modifier.isPublic(JacobiComplex.class.getModifiers()));
  }
}
