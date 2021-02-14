// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
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

  /** predicate checks a matrix A for A . f(A) == Id
   * 
   * @param tensor
   * @param chop
   * @param tensorUnaryOperator
   * @return */
  public static boolean dotId(Tensor tensor, Chop chop, TensorUnaryOperator tensorUnaryOperator) {
    return MatrixQ.of(tensor) //
        && chop.isClose(tensor.dot(tensorUnaryOperator.apply(tensor)), IdentityMatrix.of(tensor.length()));
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
  public static Chop chop(Tensor R, int m) {
    Scalar max = IntStream.range(0, m) //
        .mapToObj(i -> Abs.FUNCTION.apply(R.Get(i, i))) //
        .reduce(Max::of).get();
    return Chop.below(Math.max(Unprotect.withoutUnit(max).number().doubleValue(), 1) * 1e-12);
  }
}
