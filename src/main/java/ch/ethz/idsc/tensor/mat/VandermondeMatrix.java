// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.VectorQ;

public enum VandermondeMatrix {
  ;
  /** @param vector
   * @return */
  public static Tensor of(Tensor vector) {
    int n = vector.length();
    Tensor matrix = Tensors.reserve(n);
    VectorQ.require(vector);
    matrix.append(ConstantArray.of(RealScalar.ONE, n));
    if (1 < n) {
      matrix.append(vector);
      Tensor p = vector;
      for (int index = 2; index < n; ++index)
        matrix.append(p = p.pmul(vector));
    }
    return matrix;
  }
}
