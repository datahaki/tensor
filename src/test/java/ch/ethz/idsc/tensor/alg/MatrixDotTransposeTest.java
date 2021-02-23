// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.usr.AssertFail;
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
