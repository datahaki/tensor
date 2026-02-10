// code by jph
package ch.alpine.tensor.jet;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.ArrayFlatten;
import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.red.Times;

/** https://stackoverflow.com/questions/35819142/calculate-a-2d-homogeneous-perspective-transformation-matrix-from-4-points-in-ma
 * 
 * The bottom right entry of the matrix is always equals to 1.
 * That means the matrix lives in a normalized subset of PGL(3).
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LinearFractionalTransform.html">LinearFractionalTransform</a> */
public class LinearFractionalTransform implements TensorUnaryOperator {
  /** the dimensions of p and q are the same
   * the valid dimensions are of the form
   * 
   * 3x1
   * 4x2
   * 5x3
   * ...
   * (n+1)x(n-1) == n^2 - 1
   * 
   * determining a (n x n) matrix with the last entry equals to 1
   * 
   * @param p matrix
   * @param q matrix
   * @return
   * @throws Exception if input is degenerate */
  public static LinearFractionalTransform fit(Tensor p, Tensor q) {
    final int d = Unprotect.dimension1(p);
    final int m = d + 1;
    Tensor p1 = Tensor.of(p.stream().map(AppendOne.FUNCTION));
    Tensor zero = p1.maps(Scalar::zero);
    Tensor qt = Transpose.of(q);
    Tensor[][] array = new Tensor[d][m];
    for (int i = 0; i < d; ++i) {
      for (int j = 0; j < d; ++j)
        array[i][j] = i == j ? p1 : zero;
      array[i][d] = Times.of(qt.get(i).negate(), p);
    }
    Tensor sol = LinearSolve.of(ArrayFlatten.of(array), Flatten.of(qt));
    Tensor vec = AppendOne.FUNCTION.apply(sol);
    return new LinearFractionalTransform(ArrayReshape.of(vec, m, m));
  }

  public static LinearFractionalTransform of(Tensor matrix) {
    int n = matrix.length() - 1;
    Scalar val = matrix.Get(n, n);
    Tolerance.CHOP.requireClose(val, val.one());
    return new LinearFractionalTransform(matrix.copy());
  }
  // TODO TENSOR allow arbitrary matrix without ==1 contraint
  // .. check for Det != 0

  // ---
  private final Tensor matrix;
  private final int n;

  /** @param matrix with bottom right entry equals one */
  private LinearFractionalTransform(Tensor matrix) {
    this.matrix = matrix;
    n = matrix.length() - 1;
  }

  public LinearFractionalTransform inverse() {
    Tensor inverse = Inverse.of(matrix);
    return new LinearFractionalTransform(inverse.divide(inverse.Get(n, n)));
  }

  public Tensor matrix() {
    return matrix.unmodifiable();
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor vector) {
    Tensor affine = matrix.dot(AppendOne.FUNCTION.apply(vector));
    return affine.extract(0, vector.length()).divide(affine.Get(n));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("LinearFractionalTransform", matrix);
  }
}
