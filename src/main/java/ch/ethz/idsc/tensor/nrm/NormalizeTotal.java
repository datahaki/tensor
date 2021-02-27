// code by jph
package ch.ethz.idsc.tensor.nrm;

import java.util.OptionalInt;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.DeterminateScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;

/** Hint: if the weights sum up to zero, then the normalization fails, for example
 * NormalizeTotal.FUNCTION.apply({+1, -1}) throws an Exception
 * 
 * <p>Hint: normalization is not consistent with Mathematica for empty vectors:
 * Mathematica::Normalize[{}, Total] == {}
 * Tensor-Lib.::NormalizeTotal[{}] throws an Exception */
public enum NormalizeTotal implements TensorUnaryOperator {
  FUNCTION;

  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);

  @Override
  public Tensor apply(Tensor vector) {
    OptionalInt optionalInt = indeterminate(vector);
    return optionalInt.isPresent() //
        ? UnitVector.of(vector.length(), optionalInt.getAsInt())
        : NORMALIZE.apply(vector);
  }

  /** @param vector
   * @return */
  public static OptionalInt indeterminate(Tensor vector) {
    return IntStream.range(0, vector.length()) //
        .filter(index -> !DeterminateScalarQ.of(vector.Get(index))) //
        .findFirst();
  }
}
