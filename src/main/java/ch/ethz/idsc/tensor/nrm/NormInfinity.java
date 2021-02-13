// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Abs;

/** infinity-norm, for vectors max_i |a_i| */
/* package */ enum NormInfinity implements NormInterface {
  INSTANCE;

  @Override // from VectorNormInterface
  public Scalar ofVector(Tensor vector) {
    return vector.stream() //
        .map(Scalar.class::cast) //
        .map(Abs.FUNCTION) //
        .reduce(Max::of).get();
  }

  @Override // from NormInterface
  public Scalar ofMatrix(Tensor matrix) {
    return matrix.stream() //
        .map(Norm1.INSTANCE::ofVector) //
        .reduce(Max::of).get();
  }
}
