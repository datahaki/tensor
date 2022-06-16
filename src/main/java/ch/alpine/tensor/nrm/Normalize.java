// code by jph
package ch.alpine.tensor.nrm;

import java.util.Objects;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;

/** Normalize also works for tensors with entries of type Quantity.
 * The computation is consistent with Mathematica:
 * Normalize[{Quantity[3, "Meters"], Quantity[4, "Meters"]}] == {3/5, 4/5}
 * 
 * <p>For {@link VectorInfinityNorm} the norm of the normalized vector evaluates
 * to the exact value 1.0.
 * In general, the norms of resulting vectors may deviate from 1.0 numerically.
 * The deviations depend on the type of norm.
 * Tests for vectors with 1000 normal distributed random entries exhibit
 * <pre>
 * {@link Vector1Norm} min = 0.9999999999999987; max = 1.0000000000000018
 * {@link Vector2Norm} min = 0.9999999999999996; max = 1.0000000000000004
 * </pre>
 * 
 * <p>The implementation divides a given vector by the norm until the
 * iteration stops improving. The result is checked for proximity to 1 using
 * {@link Tolerance} convention.
 * 
 * <p>Hint: normalization is not consistent with Mathematica for empty vectors:
 * Mathematica::Normalize[{}] == {}
 * Tensor-Lib.::Normalize[{}] throws an Exception
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Normalize.html">Normalize</a> */
public class Normalize implements TensorUnaryOperator {
  /** Examples:
   * <pre>
   * Normalize.with(Vector1Norm::of).apply({2, -3, 1}) == {1/3, -1/2, 1/6}
   * Normalize.with(VectorInfinityNorm::of).apply({2, -3, 1}) == {2/3, -1, 1/3}
   * </pre>
   * 
   * Hint: Mathematica requires that the function maps any tensor to
   * a non-negative scalar, whereas the tensor library does not make this
   * requirement.
   * 
   * @param tensorScalarFunction
   * @return operator that normalizes a vector using the given tensorScalarFunction */
  public static TensorUnaryOperator with(TensorScalarFunction tensorScalarFunction) {
    return with(tensorScalarFunction, Tolerance.CHOP);
  }

  /** @param tensorScalarFunction
   * @param chop
   * @return */
  public static TensorUnaryOperator with(TensorScalarFunction tensorScalarFunction, Chop chop) {
    return new Normalize(Objects.requireNonNull(tensorScalarFunction), Objects.requireNonNull(chop));
  }

  // ---
  private final TensorScalarFunction tensorScalarFunction;
  private final Chop chop;

  private Normalize(TensorScalarFunction tensorScalarFunction, Chop chop) {
    this.tensorScalarFunction = tensorScalarFunction;
    this.chop = chop;
  }

  @Override
  public Tensor apply(Tensor vector) {
    return normalize(vector, tensorScalarFunction.apply(vector));
  }

  /** @param vector
   * @param scalar equals to tensorScalarFunction.apply(vector)
   * @return */
  /* package */ Tensor normalize(Tensor vector, Scalar scalar) {
    vector = vector.divide(scalar); // eliminate common Unit if present
    scalar = tensorScalarFunction.apply(vector); // for verification
    Scalar error_next = Abs.between(scalar, scalar.one()); // error
    Scalar error_prev = DoubleScalar.POSITIVE_INFINITY;
    if (Scalars.nonZero(error_next))
      while (Scalars.lessThan(error_next, error_prev)) { // iteration
        vector = vector.divide(scalar);
        scalar = tensorScalarFunction.apply(vector);
        error_prev = error_next;
        error_next = Abs.between(scalar, scalar.one());
      }
    chop.requireZero(error_next);
    return vector;
  }

  @Override // from Object
  public String toString() {
    return String.format("Normalize[%s]", tensorScalarFunction);
  }
}
