// code by jph
package ch.ethz.idsc.tensor.opt.rn;

import java.io.Serializable;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.CholeskyDecomposition;
import ch.ethz.idsc.tensor.mat.LeastSquares;

/** Solves the following linear system
 * matrix=
 * [eye eqs^t]
 * [eqs 0]
 * b=[target;rhs] */
public class LagrangeMultiplier implements Serializable {
  private static final long serialVersionUID = 7812090787080389056L;
  // ---
  private final int n;
  private final Tensor matrix;
  private final Tensor b;

  /** @param eye symmetric matrix of dimensions n x n
   * @param target vector of length n
   * @param eqs matrix of dimensions d x n
   * @param rhs vector of length d */
  public LagrangeMultiplier(Tensor eye, Tensor target, Tensor eqs, Tensor rhs) {
    n = eye.length();
    if (target.length() != n)
      throw TensorRuntimeException.of(eye, target);
    int d = eqs.length();
    if (rhs.length() != d)
      throw TensorRuntimeException.of(eqs, rhs);
    matrix = Tensor.of(Stream.concat( //
        Join.of(1, eye, Transpose.of(eqs)).stream(), //
        Join.of(1, eqs, Array.zeros(d, d)).stream()));
    b = Tensor.of(Stream.concat( //
        target.stream(), //
        rhs.stream()));
  }

  /** @return */
  public Tensor matrix() {
    return matrix;
  }

  /** @return */
  public Tensor b() {
    return b;
  }

  /** robust solver in case rank of eye or eqs is not maximal
   * 
   * @return vector x of length n that satisfies eqs.x == rhs and is close to eye.x ~ target */
  public Tensor usingSvd() {
    return LeastSquares.usingSvd(matrix, b).extract(0, n);
  }

  /** Hint: only use function if rank of eye and eqs is maximal
   * 
   * @return vector x of length n that satisfies eqs.x == rhs and is close to eye.x ~ target */
  public Tensor linearSolve() {
    return CholeskyDecomposition.of(matrix).solve(b).extract(0, n);
  }
}
