// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.UpperEvaluation;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.pow.Sqrt;

/** base for a class that implements the {@link InfluenceMatrix} interface
 * 
 * the square influece matrix is is computed lazily and is triggered only
 * when either {@link #matrix} or {@link #residualMaker()} are invoked.
 * Another exception is, if the design matrix is close to being square.
 * 
 * {@link #leverages()} are obtained by dotting each rows of the design matrix
 * with the corresponding column in the pseudoinverse design^+. */
/* package */ class InfluenceMatrixImpl implements InfluenceMatrix, Serializable {
  private final Tensor design;
  private final Tensor d_pinv;
  private final boolean dotMatrix;
  private transient Tensor matrix = null;

  public InfluenceMatrixImpl(Tensor design, Tensor d_pinv) {
    this.design = design;
    this.d_pinv = d_pinv;
    dotMatrix = design.length() < 2 * d_pinv.length();
  }

  @Override // from InfluenceMatrix
  public synchronized Tensor matrix() {
    return Objects.isNull(matrix) //
        ? matrix = UpperEvaluation.of( //
            design, Transpose.of(d_pinv), (p, q) -> (Scalar) p.dot(q), Conjugate.FUNCTION)
        : matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor image(Tensor vector) {
    return dotMatrix() //
        ? vector.dot(matrix())
        : vector.dot(design).dot(d_pinv);
  }

  @Override // from InfluenceMatrix
  public Tensor leverages() {
    if (Objects.nonNull(matrix))
      return Diagonal.of(matrix);
    AtomicInteger atomicInteger = new AtomicInteger();
    return Tensor.of(design.stream() //
        .map(row -> row.dot(d_pinv.get(Tensor.ALL, atomicInteger.getAndIncrement()))));
  }

  @Override // from InfluenceMatrix
  public Tensor leverages_sqrt() {
    return leverages().map(Sqrt.FUNCTION);
  }

  @Override // from InfluenceMatrix
  public Tensor residualMaker() {
    return StaticHelper.residualMaker(matrix());
  }

  @Override // from InfluenceMatrix
  public Tensor kernel(Tensor vector) {
    return vector.subtract(image(vector));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("InfluenceMatrix", matrix());
  }

  /** @return whether image(vector) is computed as the product vector . design . d_pinv,
   * or vector . matrix() in order to maximize efficiency */
  @PackageTestAccess
  /* package */ boolean dotMatrix() {
    return dotMatrix;
  }
}
