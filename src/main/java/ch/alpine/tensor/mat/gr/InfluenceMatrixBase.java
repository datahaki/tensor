// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Sqrt;

/** base for a class that implements the {@link InfluenceMatrix} interface */
/* package */ abstract class InfluenceMatrixBase implements InfluenceMatrix {
  @Override // from LeveragesInterface
  public final Tensor leverages() {
    return Diagonal.of(matrix());
  }

  @Override // from LeveragesInterface
  public final Tensor leverages_sqrt() {
    return leverages().map(Sqrt.FUNCTION);
  }

  @Override // from InfluenceMatrix
  public final Tensor residualMaker() {
    return StaticHelper.residualMaker(matrix());
  }

  @Override // from InfluenceMatrix
  public final Tensor kernel(Tensor vector) {
    return vector.subtract(image(vector));
  }

  @Override // from Object
  public final String toString() {
    return String.format("%s[%s]", InfluenceMatrix.class.getSimpleName(), Tensors.message(matrix()));
  }
}
