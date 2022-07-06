// code by jph
package ch.alpine.tensor.mat.pi;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ArrayFlatten;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.cd.CholeskyDecomposition;

/** Solves the linear system
 * 
 * matrix * x == b
 * 
 * with
 * 
 * matrix=
 * [square linear^t]
 * [linear 0]
 * 
 * b=[target;rhs]
 * 
 * @see ArrayFlatten */
public final class LagrangeMultiplier implements Serializable {
  private final int n;
  private final Tensor matrix;

  /** @param square hermite matrix of dimensions n x n
   * @param linear matrix of dimensions d x n
   * @see HermitianMatrixQ */
  public LagrangeMultiplier(Tensor square, Tensor linear) {
    n = square.length();
    int d = linear.length();
    Scalar zero = square.Get(0, 0).zero();
    matrix = ArrayFlatten.of(new Tensor[][] { //
        { square, ConjugateTranspose.of(linear) }, //
        { linear, ConstantArray.of(zero, d, d) } }); // Array.zeros(d, d)
  }

  /** matrix=
   * [square linear^t]
   * [linear 0]
   * 
   * @return square matrix of the form above */
  public Tensor matrix() {
    return matrix;
  }

  /** @param target vector of length n
   * @param rhs vector of length d
   * @return
   * @throws Exception if dimensions of input parameters do not match */
  public Tensor b(Tensor target, Tensor rhs) {
    Integers.requireEquals(n, target.length());
    Tensor b = Join.of(target, rhs);
    Integers.requireEquals(matrix.length(), b.length());
    return b;
  }

  /** @return vector x of length n that satisfies eqs.x == rhs and is close to eye.x ~ target */
  public Tensor solve(Tensor target, Tensor rhs) {
    try {
      return usingCholesky(target, rhs); // typically faster than usingSvd
    } catch (Exception exception) {
      // matrix does not have full rank
    }
    return usingSvd(target, rhs);
  }

  /** Hint: only use function if rank of eye and eqs is maximal
   * 
   * @return vector x of length n that satisfies eqs.x == rhs and is close to eye.x ~ target
   * @throws Exception if rank of matrix is not maximal */
  public Tensor usingCholesky(Tensor target, Tensor rhs) {
    return Tensor.of(CholeskyDecomposition.of(matrix).solve(b(target, rhs)).stream().limit(n));
  }

  /** robust solver in case rank of eye or eqs is not maximal
   * but slower than {@link #usingCholesky()}.
   * 
   * @return vector x of length n that satisfies eqs.x == rhs and is close to eye.x ~ target */
  public Tensor usingSvd(Tensor target, Tensor rhs) {
    return Tensor.of(LeastSquares.usingSvd(matrix, b(target, rhs)).stream().limit(n));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("LagrangeMultiplier", matrix);
  }
}
