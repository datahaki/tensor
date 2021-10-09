// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;

/* package */ class InfluenceMatrixSvd extends InfluenceMatrixBase implements Serializable {
  private final Tensor design;
  private final SingularValueDecomposition svd;
  /* matrix computed on demand */
  private Tensor matrix;
  /* matrix u computed on demand, svd.getU() with columns associated to non-zero singular value */
  private Tensor u_;

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
  public synchronized Tensor image(Tensor vector) {
    return u_().dot(vector.dot(u_()));
  }

  private Tensor u_() {
    if (Objects.isNull(u_)) {
      Tensor values = svd.values();
      List<Integer> list = IntStream.range(0, values.length()) //
          .filter(index -> !Tolerance.CHOP.isZero(values.Get(index))) //
          .boxed().collect(Collectors.toList());
      u_ = Tensor.of(svd.getU().stream().map(row -> Tensor.of(list.stream().map(row::Get))));
    }
    return u_;
  }
}
