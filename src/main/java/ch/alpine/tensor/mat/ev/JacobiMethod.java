// code by guedelmi
// modified by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.sca.Abs;

/** vector of eigen{@link #values()} has strictly zero imaginary part */
/* package */ abstract class JacobiMethod implements Eigensystem {
  private static final int MAX_ITERATIONS = 50;
  private static final Scalar DBL_EPSILON = DoubleScalar.of(Math.ulp(1.0));
  private static final Scalar HUNDRED = DoubleScalar.of(100);
  // ---
  protected final int n;
  protected final Scalar[][] H;
  protected final Tensor[] V;

  public JacobiMethod(Tensor matrix) {
    n = matrix.length();
    H = ScalarArray.ofMatrix(matrix);
    V = IntStream.range(0, n) //
        .mapToObj(k -> UnitVector.of(n, k)) //
        .toArray(Tensor[]::new);
    // ---
    init();
    Scalar factor = DoubleScalar.of(0.2 / (n * n));
    for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {
      Scalar sum = sumAbs_offDiagonal();
      if (Scalars.isZero(sum))
        return;
      Scalar tresh = iteration < 4 //
          ? sum.multiply(factor)
          : sum.zero();
      for (int p = 0; p < n - 1; ++p)
        for (int q = p + 1; q < n; ++q) {
          Scalar hpq = H[p][q];
          Scalar apq = Abs.FUNCTION.apply(hpq);
          Scalar g = HUNDRED.multiply(apq);
          if (4 < iteration && //
              Scalars.lessEquals(g, DBL_EPSILON.multiply(Abs.FUNCTION.apply(diag(p)))) && //
              Scalars.lessEquals(g, DBL_EPSILON.multiply(Abs.FUNCTION.apply(diag(q))))) {
            H[p][q] = hpq.zero();
            H[q][p] = hpq.zero();
          } else //
          if (Scalars.lessThan(tresh, apq))
            run(p, q);
        }
    }
    throw TensorRuntimeException.of(matrix);
  }

  protected abstract void init();

  /** @param p
   * @param q
   * @param apq Abs[H[p][q]] */
  protected abstract void run(int p, int q);

  /** @param p
   * @return diagonal element */
  protected final Scalar diag(int p) {
    return H[p][p];
  }

  private final Scalar sumAbs_offDiagonal() {
    Scalar sum = H[0][0].zero(); // preserve unit
    for (int p = 0; p < n - 1; ++p)
      for (int q = p + 1; q < n; ++q)
        sum = sum.add(Abs.FUNCTION.apply(H[p][q]));
    return sum;
  }

  @Override // from Eigensystem
  public final Tensor values() {
    return Tensor.of(IntStream.range(0, n).mapToObj(this::diag));
  }

  @Override // from Eigensystem
  public final Tensor vectors() {
    return Unprotect.byRef(V);
  }

  @PackageTestAccess
  final Tensor package_H() {
    return Tensors.matrix(H);
  }
}
