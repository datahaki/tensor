// code by jph
package ch.alpine.tensor.mat.pi;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.ArrayFlatten;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.cd.CholeskyDecomposition;
import ch.alpine.tensor.red.EqualsReduce;

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
 * @param n split position
 * @param square matrix of the form above
 * 
 * @see ArrayFlatten */
public record LagrangeMultiplier(int n, Tensor matrix) implements Serializable {
  private static final int CACHE_SIZE = 10;
  private static final Cache<Integer, Tensor> CACHE = Cache.of(IdentityMatrix::of, CACHE_SIZE);

  /** @param square hermite matrix of dimensions n x n
   * @param linear matrix of dimensions d x n
   * @see HermitianMatrixQ */
  public static LagrangeMultiplier of(Tensor square, Tensor linear) {
    int n = square.length();
    int d = linear.length();
    Tensor matrix = ArrayFlatten.of(new Tensor[][] { //
        { square, ConjugateTranspose.of(linear) }, //
        { linear, ConstantArray.of(EqualsReduce.zero(square), d, d) } });
    return new LagrangeMultiplier(n, matrix);
  }

  /** @param linear matrix of dimensions d x n
   * @return */
  public static LagrangeMultiplier id(Tensor linear) {
    return of(CACHE.apply(Unprotect.dimension1Hint(linear)), linear);
  }

  /** @param linear matrix of dimensions d x n
   * @return */
  public static LagrangeMultiplier id_t(Tensor linear_t) {
    // TODO unfinished business
    return id(Transpose.of(linear_t));
  }

  // ---
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
   * but slower than {@link #usingCholesky(Tensor, Tensor)}.
   * 
   * @return vector x of length n that satisfies eqs.x == rhs and is close to eye.x ~ target */
  public Tensor usingSvd(Tensor target, Tensor rhs) {
    // FIXME use LinSolve.any !?
    return Tensor.of(LeastSquares.usingSvd(matrix, b(target, rhs)).stream().limit(n));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("LagrangeMultiplier", matrix);
  }
}
