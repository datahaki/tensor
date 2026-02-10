// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.chq.FiniteTensorQ;
import ch.alpine.tensor.num.Boole;
import ch.alpine.tensor.red.Total;

/** Hint: if the weights sum up to zero, then the normalization fails, for example
 * NormalizeTotal.FUNCTION.apply({+1, -1}) throws an Exception
 * 
 * <p>Hint: normalization is not consistent with Mathematica for empty vectors:
 * Mathematica::Normalize[{}, Total] == {}
 * Tensor-Lib.::NormalizeTotal[{}] throws an Exception */
public enum NormalizeTotal implements TensorUnaryOperator {
  FUNCTION;

  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);
  private static final ScalarUnaryOperator UNITIZE = s -> Boole.of(!FiniteScalarQ.of(s));

  @Override
  public Tensor apply(Tensor vector) {
    return NORMALIZE.apply(FiniteTensorQ.of(vector) ? vector : vector.maps(UNITIZE));
  }
}
