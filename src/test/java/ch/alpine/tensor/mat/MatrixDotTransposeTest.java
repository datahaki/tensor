// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixDotTransposeTest extends TestCase {
  public void testSimple() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor tensor = Dot.of(matrix, Transpose.of(matrix));
    Tensor result = MatrixDotTranspose.of(matrix, matrix);
    Tolerance.CHOP.requireClose(tensor, result);
  }

  public void testTwo() {
    Tensor a = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor b = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor tensor = Dot.of(a, Transpose.of(b));
    Tensor result = MatrixDotTranspose.of(a, b);
    Tolerance.CHOP.requireClose(tensor, result);
  }

  public void testRank3() {
    Tensor a = RandomVariate.of(NormalDistribution.standard(), 4, 3);
    Tensor b = LeviCivitaTensor.of(3);
    Tensor tensor = Dot.of(a, Transpose.of(b));
    Tensor result = MatrixDotTranspose.of(a, b);
    Tolerance.CHOP.requireClose(tensor, result);
  }

  public void testVectorFail() {
    AssertFail.of(() -> MatrixDotTranspose.of(Tensors.vector(2, 1), Tensors.vector(3, 7)));
    AssertFail.of(() -> MatrixDotTranspose.of(Tensors.vector(2, 1), HilbertMatrix.of(2, 3)));
    AssertFail.of(() -> MatrixDotTranspose.of(HilbertMatrix.of(2), Tensors.vector(3, 7)));
  }

  public void testScalarFail() {
    AssertFail.of(() -> MatrixDotTranspose.of(RealScalar.ONE, RealScalar.ONE));
    AssertFail.of(() -> MatrixDotTranspose.of(RealScalar.ONE, HilbertMatrix.of(2, 3)));
    AssertFail.of(() -> MatrixDotTranspose.of(HilbertMatrix.of(2), RealScalar.ONE));
  }

  public void testThreeFail() {
    Tensor a = RandomVariate.of(NormalDistribution.standard(), 3, 5, 4);
    Tensor b = RandomVariate.of(NormalDistribution.standard(), 2, 4);
    // Tolerance.CHOP.requireClose(a.dot(Transpose.of(b)), MatrixDotTranspose.of(a, b));
    // Tolerance.CHOP.requireClose(Dot.of(a, Transpose.of(b)), MatrixDotTranspose.of(a, b));
    AssertFail.of(() -> MatrixDotTranspose.of(a, b));
  }
}
