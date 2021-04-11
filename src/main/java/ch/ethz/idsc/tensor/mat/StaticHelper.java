// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixDotTranspose;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Conjugate;
import ch.ethz.idsc.tensor.sca.InvertUnlessZero;

/** helper functions used in {@link SingularValueDecompositionImpl} */
/* package */ enum StaticHelper {
  ;
  /** predicate checks if tensor
   * 1) is a square matrix, then
   * 2) chop.close(tensor, f(tensor))
   * 
   * @param tensor
   * @param tensorUnaryOperator
   * @return
   * @see SquareMatrixQ */
  public static boolean addId(Tensor tensor, Chop chop, TensorUnaryOperator tensorUnaryOperator) {
    return SquareMatrixQ.of(tensor) //
        && chop.isClose(tensor, tensorUnaryOperator.apply(tensor));
  }

  /** @param tensor
   * @param chop
   * @param predicate
   * @return */
  public static boolean definite(Tensor tensor, Chop chop, Predicate<Scalar> predicate) {
    return SquareMatrixQ.of(tensor) //
        && CholeskyDecomposition.of(tensor).diagonal().stream() //
            .map(Scalar.class::cast) //
            .map(chop) //
            .allMatch(predicate);
  }

  /** @param scalar
   * @return clips given scalar to unit interval [0, 1]
   * @throws Exception if given scalar is significantly outside of unit interval */
  public static Scalar requireUnit(Scalar scalar) {
    Scalar result = Clips.unit().apply(scalar);
    Chop._06.requireClose(result, scalar);
    return result;
  }

  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);

  /** @param scalar
   * @return
   * @see InvertUnlessZero */
  public static Scalar unitize_chop(Scalar scalar) {
    return Tolerance.CHOP.isZero(scalar) ? _0 : _1;
  }

  /***************************************************/
  public static Tensor residualMaker(Tensor matrix) {
    AtomicInteger atomicInteger = new AtomicInteger();
    // I-X^+.X is projector on ker X
    return Tensor.of(matrix.stream() //
        .map(Tensor::negate) // copy
        .map(row -> {
          int index = atomicInteger.getAndIncrement();
          row.set(scalar -> scalar.add(((Scalar) scalar).one()), index);
          return row; // by ref
        }));
  }

  /***************************************************/
  /** @param R
   * @param m
   * @throws Exception if any diagonal element is below 1E-12, or the ratio
   * between min and max is below that threshold */
  public static void failFast(Tensor R, int m) {
    Tensor diag = Tensors.vector(i -> Abs.FUNCTION.apply(R.Get(i, i)), m);
    Scalar max = (Scalar) diag.stream().reduce(Max::of).get();
    Scalar min = (Scalar) diag.stream().reduce(Min::of).get();
    if (Tolerance.CHOP.isZero(min) || //
        Tolerance.CHOP.isZero(min.divide(max)))
      throw TensorRuntimeException.of(max, min);
  }

  /** @param matrix
   * @return matrix . ConjugateTranspose(matrix) */
  public static Tensor dotConjugate(Tensor matrix) {
    return MatrixDotTranspose.of(matrix, Conjugate.of(matrix));
  }
}
