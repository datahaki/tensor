// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.sca.Abs;

/* package */ class JacobiMethod implements Eigensystem {
  static final int MAX_ITERATIONS = 50;
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
