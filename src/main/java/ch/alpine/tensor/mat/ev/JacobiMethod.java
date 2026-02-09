// code by guedelmi
// modified by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Conjugate;

/** vector of eigenvalues has strictly zero imaginary part */
/* package */ abstract class JacobiMethod {
  protected static final Scalar DBL_EPSILON = DoubleScalar.of(Math.ulp(1.0));
  private static final Scalar HUNDRED = DoubleScalar.of(100);
  // TODO TENSOR MAT reintroduce adapted phase, but cap at 4?
  private static final int PHASE1 = 4;
  // ---
  protected final int n;
  protected final Scalar[][] H;
  protected final Tensor[] V;

  protected JacobiMethod(Tensor matrix) {
    n = matrix.length();
    H = ScalarArray.ofMatrix(matrix);
    V = IdentityMatrix.stream(n).toArray(Tensor[]::new);
  }

  /** @throws Exception if iteration does not converge */
  public final Eigensystem solve() {
    Scalar factor = DoubleScalar.of(0.2 / (n * n));
    int max = Eigensystem.JacobiMethod_MAX_ITERATIONS.get();
    for (int iteration = 0; iteration < max; ++iteration) {
      Scalar sum = sumAbs_offDiagonal();
      if (Scalars.isZero(sum))
        return new Eigensystem( //
            Tensor.of(IntStream.range(0, n).mapToObj(this::diag)), // values
            Unprotect.byRef(V).maps(Conjugate.FUNCTION)); // vectors
      Scalar tresh = iteration < PHASE1 //
          ? sum.multiply(factor)
          : sum.zero();
      for (int p = 0; p < n - 1; ++p)
        for (int q = p + 1; q < n; ++q) {
          Scalar hpq = H[p][q];
          Scalar apq = Abs.FUNCTION.apply(hpq);
          Scalar g = HUNDRED.multiply(apq);
          if (PHASE1 < iteration && //
              Scalars.lessEquals(g, DBL_EPSILON.multiply(Abs.FUNCTION.apply(diag(p)))) && //
              Scalars.lessEquals(g, DBL_EPSILON.multiply(Abs.FUNCTION.apply(diag(q))))) {
            H[p][q] = hpq.zero();
            H[q][p] = hpq.zero();
          } else //
          if (Scalars.lessThan(tresh, apq))
            eliminate(p, q);
        }
    }
    throw new Throw();
  }

  /** @param p
   * @param q */
  protected abstract void eliminate(int p, int q);

  /** @param p
   * @return diagonal element */
  protected final Scalar diag(int p) {
    return H[p][p];
  }

  private Scalar sumAbs_offDiagonal() {
    Scalar sum = H[0][0].zero(); // preserve unit
    for (int p = 0; p < n - 1; ++p)
      for (int q = p + 1; q < n; ++q)
        sum = sum.add(Abs.FUNCTION.apply(H[p][q]));
    return sum;
  }
}
