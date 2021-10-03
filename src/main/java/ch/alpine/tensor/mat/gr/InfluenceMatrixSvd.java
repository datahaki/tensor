// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;

/* package */ class InfluenceMatrixSvd extends InfluenceMatrixBase implements Serializable {
  private final Tensor design;
  private final SingularValueDecomposition svd;
  /* matrix is computed on demand */
  private Tensor matrix;

  public InfluenceMatrixSvd(Tensor design) {
    this.design = design;
    svd = SingularValueDecomposition.of(design);
  }

  @Override // from InfluenceMatrix
  public synchronized Tensor matrix() {
    return Objects.isNull(matrix) //
        ? matrix = _matrix()
        : matrix;
  }

  private Tensor _matrix() {
    Tensor matrix = design.dot(PseudoInverse.of(svd));
    // theory guarantees that entries of diagonal are in interval [0, 1]
    // but the numerics don't always reflect that.
    AtomicInteger atomicInteger = new AtomicInteger();
    matrix.stream() //
        .forEach(row -> row.set(StaticHelper::requireUnit, atomicInteger.getAndIncrement()));
    return matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor image(Tensor vector) {
    Tensor u = svd.getU();
    Tensor kron = Tensor.of(svd.values().stream() //
        .map(Scalar.class::cast) //
        .map(StaticHelper::unitize_chop)); // emulates v / v for v != 0
    // LONGTERM could still optimize further by extracting elements from rows in u
    // Tensor U = Tensor.of(u.stream().map(kron::pmul)); // extract instead of pmul!
    // return U.dot(vector.dot(U));
    return u.dot(kron.pmul(vector.dot(u)));
  }
}
