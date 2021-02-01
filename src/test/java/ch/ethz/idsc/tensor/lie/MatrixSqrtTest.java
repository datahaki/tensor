// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixSqrtTest extends TestCase {
  private static void _check(Tensor g, MatrixSqrt matrixSqrt) {
    Chop._08.requireClose(matrixSqrt.sqrt().dot(matrixSqrt.sqrt_inverse()), IdentityMatrix.of(g.length()));
    Chop._04.requireClose(matrixSqrt.sqrt().dot(matrixSqrt.sqrt()), g);
    Chop._04.requireClose(matrixSqrt.sqrt_inverse().dot(matrixSqrt.sqrt_inverse()), Inverse.of(g));
  }

  public void testSimple() {
    Tensor a = Tensors.fromString("{{4, 10}, {0, 9}}");
    MatrixSqrt matrixSqrt = MatrixSqrt.of(a);
    _check(a, matrixSqrt);
    Tolerance.CHOP.requireClose(matrixSqrt.sqrt(), Tensors.fromString("{{2, 2}, {0, 3}}"));
  }
  // public void testQuantity() {
  // Tensor x = Tensors.fromString("{{2[m], 2[m]}, {0[m], 3[m]}}");
  // Tensor a = x.dot(x);
  // System.out.println(a);
  // new MatrixSqrtImpl(a, Tolerance.CHOP);
  // System.out.println(matrixSqrt.sqrt());
  // _check(a, matrixSqrt);
  // Tolerance.CHOP.requireClose(matrixSqrt.sqrt(), Tensors.fromString("{{2, 2}, {0, 3}}"));
  // }
  // public void testZeros() {
  // for (int n = 1; n < 5; ++n) {
  // Tensor x = Array.zeros(n, n);
  // _check(x, MatrixSqrt.of(x));
  // _check(x, MatrixSqrt.ofSymmetric(x));
  // }
  // }

  public void testIdentity() {
    for (int n = 1; n <= 5; ++n) {
      Tensor x = IdentityMatrix.of(n);
      _check(x, MatrixSqrt.of(x));
      _check(x, MatrixSqrt.ofSymmetric(x));
    }
  }

  public void testRandomNormal() {
    for (int n = 1; n < 10; ++n) {
      Tensor x = RandomVariate.of(NormalDistribution.standard(), n, n);
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.of(x2));
    }
  }

  public void testRandomDiscreteUniform() {
    for (int n = 1; n < 10; ++n) {
      Tensor x = RandomVariate.of(DiscreteUniformDistribution.of(-200, 200), n, n);
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.of(x2));
    }
  }

  public void testRandomSymmetric() {
    for (int n = 1; n < 5; ++n) {
      Tensor x = Symmetrize.of(RandomVariate.of(NormalDistribution.of(0, 0.2), n, n));
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.of(x2));
      _check(x2, MatrixSqrt.ofSymmetric(x2));
    }
  }

  public void testRandomSymmetricQuantity() {
    for (int n = 1; n < 5; ++n) {
      Tensor x = Symmetrize.of(RandomVariate.of(NormalDistribution.of(0, 0.2), n, n)).map(s -> Quantity.of(s, "m"));
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.ofSymmetric(x2));
    }
  }

  public void testSymNegativeDiagonal() {
    Tensor matrix = DiagonalMatrix.of(-1, -2, -3);
    _check(matrix, MatrixSqrt.of(matrix));
    _check(matrix, MatrixSqrt.ofSymmetric(matrix));
  }

  public void testNonSquareFail() {
    AssertFail.of(() -> MatrixSqrt.of(RandomVariate.of(UniformDistribution.of(-2, 2), 2, 3)));
  }

  public void testNonSymmetricFail() {
    AssertFail.of(() -> MatrixSqrt.ofSymmetric(RandomVariate.of(UniformDistribution.of(-2, 2), 4, 4)));
  }
}
