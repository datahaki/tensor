// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.sca.Abs;

/** vector of eigen{@link #values()} has strictly zero imaginary part */
/* package */ class JacobiMethod implements Eigensystem {
  static final int MAX_ITERATIONS = 50;
  // higher phase 1 count increases numerical precision
  static final int[] PHASE1 = { //
      0, 0, 0, // n==0,1,2
      4, // n==3
      5, 5, // n==4,5
      6, 6, 6, 6, // n==6,...,9
      7 };
  static final Scalar HUNDRED = DoubleScalar.of(100);
  static final Scalar EPS = DoubleScalar.of(Math.ulp(1));
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
  }

  protected final Scalar diag(int p) {
    return H[p][p];
  }

  protected final Scalar sumAbs_offDiagonal() {
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
