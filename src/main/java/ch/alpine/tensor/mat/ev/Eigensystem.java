// code by jph
package ch.alpine.tensor.mat.ev;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixLog;
import ch.alpine.tensor.mat.ex.MatrixSqrt;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;

/** Careful: Mathematica orders the eigenvalues according to absolute value.
 * However, the tensor library does not guarantee any particular ordering.
 * 
 * @return vector of eigenvalues corresponding to the eigenvectors */
/** Mathematica specifies:
 * matrix . Transpose[vectors] == Transpose[vectors] . DiagonalMatrix[values]
 * Quote:
 * The eigenvalues and eigenvectors satisfy the matrix equation
 * m.Transpose[vectors]==Transpose[vectors].DiagonalMatrix[values].
 * 
 * where
 * A is the original matrix
 * V = Transpose.of(vectors())
 * D = diagonalMatrix()
 * 
 * @return matrix with rows as eigenvectors of given matrix
 * The eigenvectors are not necessarily scaled to unit length.
 * @see OrthogonalMatrixQ
 * 
 * <pre>
 * LinearSolve.of(vectors, Times.of(values(), vectors)) == matrix
 * Transpose.of(vectors).dot(Times.of(values, vectors)) == matrix
 * </pre>
 * 
 * Mathematica specifies:
 * matrix . Transpose[vectors] == Transpose[vectors] . DiagonalMatrix[values]
 * Quote:
 * The eigenvalues and eigenvectors satisfy the matrix equation
 * m.Transpose[vectors]==Transpose[vectors].DiagonalMatrix[values].
 * 
 * The eigenvalues of a symmetric, or hermitian matrix are real.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Eigensystem.html">Eigensystem</a> */
public record Eigensystem(Tensor values, Tensor vectors) implements Serializable {
  public static final ThreadLocal<Integer> JacobiMethod_MAX_ITERATIONS = ThreadLocal.withInitial(() -> 50);

  /** Quote from Compact Numerical Methods:
   * 1) All the eigenvalues of a real symmetric matrix are real.
   * 2) It is possible to find a complete set of n eigenvectors for an order-n real
   * symmetric matrix and these can be made mutually orthogonal.
   * 
   * @param matrix real symmetric, non-empty, and real valued
   * @return eigensystem with vectors scaled to unit length
   * @throws Exception if input is not a real symmetric matrix */
  public static Eigensystem ofSymmetric(Tensor matrix) {
    return ofSymmetric(matrix, Tolerance.CHOP);
  }

  /** @param matrix
   * @param chop threshold to check symmetry of matrix
   * @return */
  public static Eigensystem ofSymmetric(Tensor matrix, Chop chop) {
    return JacobiReal.of(new SymmetricMatrixQ(chop).requireMember(matrix));
  }

  /** @param matrix hermitian
   * @return eigenvalue decomposition of given matrix
   * @see HermitianMatrixQ */
  public static Eigensystem ofHermitian(Tensor matrix) {
    return ofHermitian(matrix, Tolerance.CHOP);
  }

  /** @param matrix hermitian
   * @param chop
   * @return eigenvalue decomposition of given matrix
   * @see HermitianMatrixQ */
  public static Eigensystem ofHermitian(Tensor matrix, Chop chop) {
    return JacobiComplex.of(new HermitianMatrixQ(chop).requireMember(matrix));
  }

  /** Careful: the general case is only for use with small matrices
   * 
   * @param matrix
   * @return */
  public static Eigensystem of(Tensor matrix) {
    return switch (matrix.length()) {
    case 1 -> Eigensystems._1(matrix);
    case 2 -> Eigensystems._2(matrix);
    default -> _of(matrix);
    };
  }

  private static Eigensystem _of(Tensor matrix) {
    if (Im.allZero(matrix) && //
        SymmetricMatrixQ.INSTANCE.isMember(matrix))
      return JacobiReal.of(matrix);
    if (HermitianMatrixQ.INSTANCE.isMember(matrix))
      return JacobiComplex.of(matrix);
    throw new Throw(matrix);
  }

  /** @return */
  public Eigensystem decreasing() {
    int[] ordering = Ordering.DECREASING.of(values);
    return new Eigensystem( //
        Tensor.of(IntStream.of(ordering).mapToObj(values::get)), //
        Tensor.of(IntStream.of(ordering).mapToObj(vectors::get)));
  }

  /** Mathematica::MatrixFunction
   * <p>inspired by
   * <a href="https://reference.wolfram.com/language/ref/MatrixFunction.html">MatrixFunction</a>
   * 
   * @param scalarUnaryOperator
   * @return
   * @see MatrixExp
   * @see MatrixLog
   * @see MatrixSqrt */
  public Tensor map(ScalarUnaryOperator scalarUnaryOperator) {
    return Transpose.of(LinearSolve.of(vectors, Times.of(values.maps(scalarUnaryOperator), vectors)));
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("Eigensystem", values, vectors);
  }
}
