// code by jph
package ch.alpine.tensor.img;

import java.util.stream.Stream;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.red.MinMax;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.ply.TripleReduceExtrapolation;

public record MatrixGradient(Tensor dx, Tensor dy) {
  private static final TripleReduceExtrapolation INSTANCE = new TripleReduceExtrapolation() {
    @Override
    protected Tensor reduce(Tensor p, Tensor q, Tensor r) {
      return r.subtract(p).multiply(Rational.HALF);
    }
  };

  public static MatrixGradient of(Tensor matrix) {
    return new MatrixGradient( //
        INSTANCE.apply(matrix), //
        INSTANCE.slash(matrix) //
    );
  }

  public Tensor array() {
    return Transpose.of(Tensors.of(dx, dy), 2, 0, 1);
  }

  public Tensor cross() {
    return Transpose.of(Tensors.of(dy.negate(), dx), 2, 0, 1);
  }

  public Tensor get(int i, int j) {
    return Unprotect.byRef(dx.Get(i, j), dy.Get(i, j));
  }

  public Tensor cross(int i, int j) {
    return Unprotect.byRef(dy.Get(i, j).negate(), dx.Get(i, j));
  }

  public MatrixGradient rescale(Scalar h0, Scalar h1) {
    return new MatrixGradient(dx.multiply(h0), dy.multiply(h1));
  }

  public Clip range() {
    return Stream.concat(Flatten.scalars(dx), Flatten.scalars(dy)).collect(MinMax.toClip());
  }

  public MatrixGradient rescale() {
    Clip clip = Clips.symmetrize(range());
    ScalarUnaryOperator suo = clip::rescale;
    ScalarUnaryOperator fin = suo.andThen(LinearInterpolation.of(Clips.absoluteOne()));
    return new MatrixGradient(dx.maps(fin), dy.maps(fin));
  }
}
