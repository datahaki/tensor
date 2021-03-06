// code by jph
package ch.alpine.tensor.mat;

import java.security.SecureRandom;
import java.util.Random;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.MatrixQ;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.fft.FourierMatrix;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Pivot;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class InverseTest extends TestCase {
  public void testInverse() {
    int n = 25;
    Tensor A = RandomVariate.of(NormalDistribution.standard(), n, n);
    Tensor Ai = Inverse.of(A);
    Tensor id = IdentityMatrix.of(A.length());
    Chop._09.requireClose(A.dot(Ai), id);
    Chop._09.requireClose(Ai.dot(A), id);
  }

  public void testInverseNoAbs() {
    int n = 12;
    int p = 20357;
    Random random = new SecureRandom();
    Tensor A = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(p), p), n, n);
    int iter = 0;
    while (Scalars.isZero(Det.of(A, Pivots.FIRST_NON_ZERO)) && iter < 5) {
      A = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(p), p), n, n);
      ++iter;
    }
    Tensor b = Tensors.vector(i -> GaussScalar.of(random.nextInt(p), p), n);
    Tensor x = LinearSolve.of(A, b, Pivots.FIRST_NON_ZERO);
    assertEquals(A.dot(x), b);
    Tensor id = DiagonalMatrix.of(n, GaussScalar.of(1, p));
    Tensor Ai = LinearSolve.of(A, id, Pivots.FIRST_NON_ZERO);
    assertEquals(A.dot(Ai), id);
    assertEquals(Ai.dot(A), id);
  }

  public void testGeneralIdentity() {
    Tensor A = HilbertMatrix.of(3, 3);
    Tensor b = UnitVector.of(3, 1);
    Tensor x = LinearSolve.of(A, b);
    assertEquals(A.dot(x), b);
    assertEquals(Inverse.of(A).dot(b), x);
  }

  public void testFourier() {
    Tensor inv1 = Inverse.of(FourierMatrix.of(5), Pivots.FIRST_NON_ZERO);
    Tensor inv2 = Inverse.of(FourierMatrix.of(5), Pivots.ARGMAX_ABS);
    Tolerance.CHOP.requireClose(inv1, inv2);
  }

  public void testGaussian() {
    int prime = 3121;
    Distribution distribution = DiscreteUniformDistribution.of(0, prime);
    Scalar one = GaussScalar.of(1, prime);
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n).map(s -> GaussScalar.of(s.number().intValue(), prime));
      if (Scalars.nonZero(Det.of(matrix)))
        for (Pivot pivot : Pivots.values()) {
          Tensor revers = Inverse.of(matrix, pivot);
          MatrixQ.requireSize(revers, n, n);
          assertEquals(DiagonalMatrix.of(n, one), Dot.of(matrix, revers));
        }
    }
  }

  public void testDet0() {
    Tensor matrix = ResourceData.of("/mat/det0-matlab.csv"); // det(matrix) == 0
    assertNotNull(matrix);
    AssertFail.of(() -> Inverse.of(matrix));
    AssertFail.of(() -> Inverse.of(N.DOUBLE.of(matrix)));
  }

  public void testZeroFail() {
    Tensor matrix = DiagonalMatrix.of(1, 2, 0, 3);
    AssertFail.of(() -> Inverse.of(matrix));
    AssertFail.of(() -> Inverse.of(matrix, Pivots.FIRST_NON_ZERO));
  }

  public void testFailNonSquare() {
    AssertFail.of(() -> Inverse.of(HilbertMatrix.of(3, 4)));
    AssertFail.of(() -> Inverse.of(HilbertMatrix.of(4, 3)));
  }

  public void testFailRank3() {
    AssertFail.of(() -> Inverse.of(LeviCivitaTensor.of(3)));
  }

  public void testQuantity1() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(2, "m");
    Scalar qs3 = Quantity.of(3, "rad");
    Scalar qs4 = Quantity.of(4, "rad");
    Tensor ve1 = Tensors.of(qs1.multiply(qs1), qs2.multiply(qs3));
    Tensor ve2 = Tensors.of(qs2.multiply(qs3), qs4.multiply(qs4));
    Tensor mat = Tensors.of(ve1, ve2);
    Tensor eye = IdentityMatrix.of(2); // <- yey!
    Tensor inv = LinearSolve.of(mat, eye);
    Tensor res = mat.dot(inv);
    Chop.NONE.requireClose(eye, res);
    Tensor inverse = Inverse.of(mat);
    Tensor expected = Tensors.fromString( //
        "{{-4/5[m^-2], 3/10[m^-1*rad^-1]}, {3/10[m^-1*rad^-1], -1/20[rad^-2]}}");
    assertEquals(inverse, expected);
    ExactTensorQ.require(inverse);
  }

  public void testQuantity2() {
    Tensor matrix = Tensors.fromString( //
        "{{1[m^2], 2[m*rad], 3[kg*m]}, {4[m*rad], 2[rad^2], 2[kg*rad]}, {5[kg*m], 1[kg*rad], 7[kg^2]}}");
    final Tensor eye = IdentityMatrix.of(3).unmodifiable();
    for (Pivot pivot : Pivots.values()) {
      Tensor inv = LinearSolve.of(matrix, eye, pivot);
      Tensor res = matrix.dot(inv);
      Chop.NONE.requireClose(eye, res);
    }
    {
      Tensor inv = Inverse.of(matrix);
      Chop.NONE.requireClose(matrix.dot(inv), inv.dot(matrix));
      Chop.NONE.requireClose(matrix.dot(inv), IdentityMatrix.of(3));
    }
    assertFalse(HermitianMatrixQ.of(matrix));
    assertFalse(SymmetricMatrixQ.of(matrix));
  }

  public void testDecimalScalarInverse() {
    Tensor matrix = HilbertMatrix.of(5).map(N.DECIMAL128);
    Tensor invers = Inverse.of(matrix);
    Scalar detmat = Det.of(matrix);
    Scalar detinv = Det.of(invers);
    Scalar one = detmat.multiply(detinv);
    assertTrue(one instanceof DecimalScalar);
    Tolerance.CHOP.requireClose(one, RealScalar.ONE);
  }
}
