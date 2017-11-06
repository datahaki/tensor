// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.mat.RowReduce;
import ch.ethz.idsc.tensor.mat.SingularValueDecomposition;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ExactScalarQInterface;

/** predicate to test if scalar is encoded in exact precision.
 * result is determined by implementation of {@link ExactScalarQInterface}.
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
 * see also {@link MachineNumberQ} */
public enum ExactScalarQ {
  ;
  /** @param tensor
   * @return true, if tensor is instance of {@link ExactScalarQInterface} which evaluates to true */
  public static boolean of(Tensor tensor) {
    if (tensor instanceof ExactScalarQInterface) {
      ExactScalarQInterface exactNumberQInterface = (ExactScalarQInterface) tensor;
      return exactNumberQInterface.isExactScalar();
    }
    return false;
  }

  /** @param tensor
   * @return true if all scalar entries in given tensor satisfy the predicate {@link ExactScalarQ#of(Tensor)} */
  public static boolean all(Tensor tensor) {
    return tensor.flatten(-1).allMatch(ExactScalarQ::of);
  }
}