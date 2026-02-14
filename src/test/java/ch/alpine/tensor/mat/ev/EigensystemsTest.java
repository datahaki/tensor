// code by jph
package ch.alpine.tensor.mat.ev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.rot.RotationMatrix;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixLog;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.sv.SingularValueList;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ComplexDiskUniformDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.ply.ChebyshevNodes;
import test.wrap.EigensystemQ;

class EigensystemsTest {
  @Test
  void test() {
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(), 2, 2);
    Eigensystem eigensystem = Eigensystems._2(matrix);
    new EigensystemQ(matrix).require(eigensystem);
  }

  @Test
  void test2x2() {
    Tensor matrix = Tensors.fromString("{{1,2},{0,1/2}}");
    Eigensystem eigensystem = Eigensystems._2(matrix);
    new EigensystemQ(matrix).require(eigensystem, Tolerance.CHOP);
    ExactTensorQ.require(eigensystem.values());
    assertEquals(eigensystem.values(), Tensors.fromString("{1, 1/2}"));
    ExactTensorQ.require(eigensystem.vectors());
    Tensor Vt = Transpose.of(eigensystem.vectors());
    Tensor alt = Vt.dot(DiagonalMatrix.sparse(eigensystem.values().maps(Log.FUNCTION))).dot(Inverse.of(Vt));
    Tensor log = MatrixLog.of(matrix);
    Tolerance.CHOP.requireClose(log, alt);
    Tolerance.CHOP.requireClose(matrix.dot(Vt), Vt.dot(DiagonalMatrix.sparse(eigensystem.values())));
    Tolerance.CHOP.requireClose(matrix, Vt.dot(DiagonalMatrix.sparse(eigensystem.values())).dot(Inverse.of(Vt)));
    Tolerance.CHOP.requireClose(MatrixExp.of(log), matrix);
  }

  @Test
  void testMathematica() {
    Tensor matrix = Tensors.fromString("{{-3, 2}, {-15, 8}}");
    Eigensystem eigensystem = Eigensystems._2(matrix);
    new EigensystemQ(matrix).require(eigensystem, Tolerance.CHOP);
    assertEquals(eigensystem.values(), Tensors.fromString("{3, 2}"));
    ExactTensorQ.require(eigensystem.vectors());
  }

  @Test
  void test2x2Jordan() {
    Tensor matrix = Tensors.fromString("{{2, 1}, {0, 2}}");
    assertThrows(Exception.class, () -> Eigensystems._2(matrix));
  }

  @Test
  void test2x2JordanSpecial() {
    Tensor matrix = Tensors.fromString("{{1,10},{0,1}}");
    assertThrows(Exception.class, () -> Eigensystems._2(matrix));
  }

  @Test
  void test2x2Diagonal() {
    Tensor matrix = Tensors.fromString("{{2, 0}, {0, 2}}");
    Eigensystem eigensystem = Eigensystems._2(matrix);
    new EigensystemQ(matrix).require(eigensystem, Tolerance.CHOP);
    assertEquals(eigensystem.values(), Tensors.fromString("{2, 2}"));
    ExactTensorQ.require(eigensystem.vectors());
  }

  @Test
  void test2x2Zeros() {
    Tensor matrix = Tensors.fromString("{{0, 0}, {0, 0}}");
    Eigensystem eigensystem = Eigensystems._2(matrix);
    new EigensystemQ(matrix).require(eigensystem);
    Tensor log = eigensystem.map(Log.FUNCTION);
    assertEquals(log.toString(), "{{NaN, NaN}, {NaN, NaN}}");
    // IO.println(log);
  }

  @Test
  void testRotation() {
    Tensor matrix = RotationMatrix.of(.3);
    Eigensystem eigensystem = Eigensystems._2(matrix);
    new EigensystemQ(matrix).require(eigensystem, Tolerance.CHOP);
  }

  @Test
  void test2x2Quantity() {
    Tensor matrix = Tensors.fromString("{{1,2},{0,1/2}}").maps(s -> Quantity.of(s, "m"));
    Eigensystem eigensystem = Eigensystems._2(matrix);
    new EigensystemQ(matrix).require(eigensystem, Tolerance.CHOP);
    assertEquals(eigensystem.values(), Tensors.fromString("{1[m], 1/2[m]}"));
  }

  @Test
  void testComplex() {
    Tensor matrix = Tensors.fromString("{{2+5*I,-2-8*I},{3,-4-7*I}}");
    Eigensystem eigensystem = Eigensystems._2(matrix);
    new EigensystemQ(matrix).require(eigensystem, Tolerance.CHOP);
    Scalar l1 = Scalars.fromString("-2.02813 - 6.83584*I");
    Scalar l2 = Scalars.fromString("0.0281293 + 4.83584*I");
    Chop._04.requireClose(l1, eigensystem.values().get(1));
    Chop._04.requireClose(l2, eigensystem.values().get(0));
    Tensor v1 = Tensors.fromString("{0.65729 + 0.0547194 *I, 1}");
    Tensor v2 = Tensors.fromString("{1.34271 + 3.94528 *I, 1}");
    // λ_1≈-2.02813 - 6.83584 i, v_1≈(0.65729 + 0.0547194 i, 1)
    // λ_2≈0.0281293 + 4.83584 i, v_2≈(1.34271 + 3.94528 i, 1)
    Chop._04.requireClose(Vector2Norm.NORMALIZE.apply(v1), eigensystem.vectors().get(1));
    Chop._04.requireClose(Vector2Norm.NORMALIZE.apply(v2), eigensystem.vectors().get(0));
  }

  @RepeatedTest(5)
  void testComplexRandom() {
    Distribution distribution = ComplexDiskUniformDistribution.of(1);
    Tensor matrix = RandomVariate.of(distribution, 2, 2);
    Eigensystem eigensystem = Eigensystem.of(matrix);
    new EigensystemQ(matrix).require(eigensystem, Tolerance.CHOP);
  }

  @Test
  void testSimple() {
    Tensor matrix = ChebyshevNodes._0.matrix(32);
    Tensor vector = Eigensystem.ofSymmetric(matrix).decreasing().values();
    Tensor lo = vector.extract(1, 15);
    Tensor hi = vector.extract(17, 31);
    Tolerance.CHOP.requireClose(lo, ConstantArray.of(RealScalar.of(+4), lo.length()));
    Tolerance.CHOP.requireClose(hi, ConstantArray.of(RealScalar.of(-4), hi.length()));
  }

  @Test
  void testHermitian() {
    Tensor matrix = ChebyshevNodes._0.matrix(32);
    Tensor vector = Eigensystem.ofHermitian(matrix).decreasing().values();
    Tensor lo = vector.extract(1, 15);
    Tensor hi = vector.extract(17, 31);
    Tolerance.CHOP.requireClose(lo, ConstantArray.of(RealScalar.of(+4), lo.length()));
    Tolerance.CHOP.requireClose(hi, ConstantArray.of(RealScalar.of(-4), hi.length()));
  }

  @Test
  void testComplex2() {
    Tensor matrix = Tensors.fromString("{{2, 3-3*I}, {3+3*I, 5}}");
    HermitianMatrixQ.INSTANCE.require(matrix);
    Tensor vector = Eigensystem.ofHermitian(matrix).decreasing().values();
    Tolerance.CHOP.requireClose(vector, Tensors.vector(8, -1));
  }

  @Test
  void testSimpleImpl() {
    Distribution distribution = UniformDistribution.of(-1, 1);
    Tensor x = RandomVariate.of(distribution, 4, 3);
    Tensor matrix = MatrixDotTranspose.of(x, x);
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor values1 = eigensystem.decreasing().values();
    Tensor values2 = SingularValueList.of(matrix);
    Tolerance.CHOP.requireClose(values1, values2);
    new EigensystemQ(matrix).require(eigensystem);
  }
}
