// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.sca.ply.TripleReduceExtrapolation;

public record MatrixGradient(Tensor dx, Tensor dy) {
  public static final TripleReduceExtrapolation INSTANCE = new TripleReduceExtrapolation() {
    @Override
    protected Scalar reduce(Tensor p, Tensor q, Tensor r) {
      return (Scalar) r.subtract(p).multiply(Rational.HALF);
    }
  };

  public static MatrixGradient of(Tensor matrix) {
    return new MatrixGradient( //
        INSTANCE.slash(matrix), //
        Transpose.of(INSTANCE.slash(Transpose.of(matrix))));
  }

  public Tensor array() {
    return Transpose.of(Unprotect.byRef(dx, dy), 2, 0, 1);
  }

  public Tensor get(int i, int j) {
    return Unprotect.byRef(dx.Get(i, j), dy.Get(i, j));
  }

  public MatrixGradient rescale(Scalar h0, Scalar h1) {
    return new MatrixGradient(dx.multiply(h0), dy.multiply(h1));
  }
}
