// code by jph
package ch.alpine.tensor.chq;

import java.util.Objects;

import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.InexactScalarMarker;
import ch.alpine.tensor.mat.re.RowReduce;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.qty.Quantity;

/** predicate to test if scalar is encoded in exact precision.
 * result is determined by implementation of {@link InexactScalarMarker}.
 * 
 * <p>Examples:
 * <pre>
 * ExactScalarQ.of(RationalScalar.of(2, 3)) == true
 * ExactScalarQ.of(ComplexScalar.of(3, 4)) == true
 * ExactScalarQ.of(GaussScalar.of(4, 7)) == true
 * ExactScalarQ.of(Quantity.of(3, "m")) == true
 * 
 * ExactScalarQ.of(DoubleScalar.of(3.14)) == false
 * ExactScalarQ.of(DoubleScalar.POSITIVE_INFINITY) == false
 * ExactScalarQ.of(DoubleScalar.INDETERMINATE) == false
 * ExactScalarQ.of(DecimalScalar.of("3.14")) == false
 * ExactScalarQ.of(Quantity.of(2.71, "kg*s")) == false
 * </pre>
 * 
 * <p>The predicate is used to select the appropriate algorithm.
 * For instance, the nullspace for a matrix with all exact scalars
 * is computed using {@link RowReduce}, otherwise {@link SingularValueDecomposition}.
 * 
 * <p>Identical to Mathematica::Exact"Number"Q except for input of type {@link Quantity}.
 * 
 * @see InexactScalarMarker
 * @see IntegerQ
 * @see FiniteScalarQ */
public enum ExactScalarQ {
  ;
  /** @param scalar
   * @return true, if scalar is instance of {@link InexactScalarMarker} which evaluates to true */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof InexactScalarMarker)
      return false;
    if (scalar instanceof MultiplexScalar) {
      MultiplexScalar multiplexScalar = (MultiplexScalar) scalar;
      return multiplexScalar.allMatch(ExactScalarQ::of);
    }
    Objects.requireNonNull(scalar);
    return true;
  }

  /** @param scalar
   * @return given scalar
   * @throws Exception if given scalar is not an integer in exact precision */
  public static Scalar require(Scalar scalar) {
    if (of(scalar))
      return scalar;
    throw new Throw(scalar);
  }

  /** @param tensor
   * @return true if any scalar entry in given tensor satisfies the predicate {@link #of(Scalar)} */
  public static boolean any(Tensor tensor) {
    return tensor.flatten(-1).map(Scalar.class::cast).anyMatch(ExactScalarQ::of);
  }
}
