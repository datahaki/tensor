// code by jph
package ch.alpine.tensor.mat;

import java.util.function.Predicate;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.cd.CholeskyDecomposition;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Re;

/* package */ enum StaticHelper {
  ;
  /** @param tensor
   * @param chop
   * @param predicate
   * @return */
  public static boolean definite(Tensor tensor, Chop chop, Predicate<Scalar> predicate) {
    return SquareMatrixQ.INSTANCE.test(tensor) //
        && CholeskyDecomposition.of(tensor).diagonal().stream() //
            .map(Scalar.class::cast) //
            .map(Re.FUNCTION) //
            .map(chop) //
            .allMatch(predicate);
  }
}
