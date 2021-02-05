// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.sca.Sqrt;

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
    return String.format("%s[%d]", InfluenceMatrix.class.getSimpleName(), length());
  }

  /** @return Length[design] */
  protected abstract int length();
}
