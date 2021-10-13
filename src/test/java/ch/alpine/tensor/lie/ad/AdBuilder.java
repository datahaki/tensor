// code by jph
package ch.alpine.tensor.lie.ad;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.MatrixBracket;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.LinearSolve;

public enum AdBuilder {
  ;
  public static Tensor of(Tensor basis) {
    int n = basis.length();
    Tensor m = Transpose.of(Tensor.of(basis.stream().map(Flatten::of)));
    Tensor ad = Tensors.matrix((i, j) -> solve(m, MatrixBracket.of(basis.get(i), basis.get(j))), n, n);
    return Transpose.of(ad, 2, 1, 0);
  }

  private static Tensor solve(Tensor m, Tensor rhs) {
    Tensor b = Flatten.of(rhs);
    Tensor x = LinearSolve.any(m, b);
    Tolerance.CHOP.requireClose(m.dot(x), b);
    return x;
  }
}
