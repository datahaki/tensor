package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.ply.Polynomial;

/* package */ enum Eigensystems {
  ;
  /** @param matrix
   * @return */
  public static Eigensystem _1(Tensor matrix) {
    Integers.requireEquals(matrix.length(), 1);
    return new Eigensystem(matrix.get(0), IdentityMatrix.of(matrix));
  }

  /** @param matrix
   * @return */
  public static Eigensystem _2(Tensor matrix) {
    Integers.requireEquals(matrix.length(), 2);
    Scalar a = matrix.Get(0, 0);
    Scalar b = matrix.Get(0, 1);
    Scalar c = matrix.Get(1, 0);
    Scalar d = matrix.Get(1, 1);
    Tensor coeffs = Tensors.of(a.multiply(d).subtract(b.multiply(c)), a.add(d).negate(), a.one());
    Polynomial polynomial = Polynomial.of(coeffs);
    Tensor values = Reverse.of(polynomial.roots());
    Tensor id = IdentityMatrix.of(matrix);
    if (Tolerance.CHOP.isClose(values.Get(0), values.Get(1))) {
      Tensor vectors = NullSpace.of(matrix.subtract(id.multiply(values.Get(0))));
      if (vectors.length() == 2)
        return new Eigensystem(values, vectors);
      throw new Throw(matrix);
    }
    return new Eigensystem(values, Tensor.of(values.stream() //
        .map(Scalar.class::cast) //
        .map(root -> NullSpace.of(matrix.subtract(id.multiply(root))).get(0))));
  }
}
