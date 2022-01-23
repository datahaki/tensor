// code by jph
package ch.alpine.tensor.mat.qr;

import java.util.Random;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class RSolveTest extends TestCase {
  public void testSimple2x2() {
    Tensor r = Tensors.fromString("{{2,5},{3,0}}");
    Tensor rhs = Tensors.vector(1, 2);
    Tensor expect = LinearSolve.of(r, rhs);
    int[] sigma = new int[] { 1, 0 };
    Tensor actual = RSolve.of(r, sigma, rhs);
    assertEquals(expect, actual);
  }

  public void testSimple3x3() {
    Tensor r = Tensors.fromString("{{2,5,1},{2,0,3},{4,0,0}}");
    Tensor rhs = Tensors.vector(1, 2, 3);
    Tensor expect = LinearSolve.of(r, rhs);
    int[] sigma = new int[] { 1, 2, 0 };
    Tensor actual = RSolve.of(r, sigma, rhs);
    assertEquals(expect, actual);
  }

  public void testSimple4x4() {
    Tensor r = Tensors.fromString("{{2,5,1,7},{2,0,3,0},{4,0,0,0},{0,3,0,0}}");
    Tensor rhs = Tensors.vector(1, 2, 3, 4);
    Tensor expect = LinearSolve.of(r, rhs);
    int[] sigma = new int[] { 3, 2, 0, 1 };
    Tensor actual = RSolve.of(r, sigma, rhs);
    assertEquals(expect, actual);
  }

  public void testGs() {
    Random random = new Random(1);
    Distribution distribution = UniformDistribution.unit();
    for (int n = 2; n < 8; ++n) {
      Tensor matrix = RandomVariate.of(distribution, random, n, n);
      QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
      Tensor r = qrDecomposition.getR();
      Tensor x = RSolve.of(qrDecomposition, IdentityMatrix.of(n));
      Chop._08.requireClose(Inverse.of(r), x);
    }
  }

  public void testLeastSquaresWithGramSchmidt() {
    Random random = new Random(1);
    Distribution distribution = UniformDistribution.of(-2, 2);
    int n = 10;
    Tensor matrix = RandomVariate.of(distribution, random, n, 4);
    Tensor b = RandomVariate.of(distribution, random, n);
    Tensor expect = LeastSquares.of(matrix, b);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    // TODO this shows that the RSolve API is not yet finished, since there is no way to
    // ... perform the below computation with the public tensor API
    Tensor actual = RSolve.of(qrDecomposition, qrDecomposition.getQConjugateTranspose().dot(b));
    Tolerance.CHOP.requireClose(expect, actual);
  }
}
