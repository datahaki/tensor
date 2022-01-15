// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Sqrt;

/** base for a class that implements the {@link InfluenceMatrix} interface */
/* package */ class InfluenceMatrixImpl implements InfluenceMatrix, Serializable {
  private final Tensor design;
  private final Tensor d_pinv;
  private final boolean useMatrix;
  private transient Tensor matrix = null;

  public InfluenceMatrixImpl(Tensor design, Tensor d_pinv) {
    this.design = design;
    this.d_pinv = d_pinv;
    int n = design.length();
    int d = d_pinv.length();
    useMatrix = n < d + d;
  }

  @Override // from InfluenceMatrix
  public Tensor matrix() {
    return Objects.isNull(matrix) //
        ? matrix = design.dot(d_pinv)
        : matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor image(Tensor vector) {
    return useMatrix //
        ? vector.dot(matrix)
        : vector.dot(design).dot(d_pinv);
  }

  @Override // from InfluenceMatrix
  public final Tensor leverages() {
    return Diagonal.of(matrix());
  }

  @Override // from InfluenceMatrix
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

  @PackageTestAccess
  boolean useMatrix() {
    return useMatrix;
  }
}
