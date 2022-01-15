// code by jph
package ch.alpine.tensor.mat.gr;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.qr.GramSchmidt;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

/** {@link GramSchmidt} supersedes InfluenceMatrixSvd */
/* package */ class InfluenceMatrixSvd {
  /** matrix u is svd.getU() with columns associated to non-zero singular value
   * u may be of the form {{}, {}, ..., {}} */
  private final Tensor u;

  public InfluenceMatrixSvd(SingularValueDecomposition svd) {
    Tensor values = svd.values();
    List<Integer> list = IntStream.range(0, values.length()) //
        .filter(index -> !Tolerance.CHOP.isZero(values.Get(index))) //
        .boxed().collect(Collectors.toList());
    u = Tensor.of(svd.getU().stream().map(row -> Tensor.of(list.stream().map(row::Get))));
  }

  public Tensor matrix() {
    int n = u.length();
    if (Unprotect.dimension1Hint(u) == 0)
      return Array.zeros(n, n);
    Tensor matrix = MatrixDotTranspose.of(u, u);
    // theory guarantees that diagonal entries of matrix are in the unit interval [0, 1]
    // but the numerics don't always reflect that.
    IntStream.range(0, n).forEach(i -> matrix.set(InfluenceMatrixSvd::requireUnit, i, i));
    return matrix;
  }

  public Tensor image(Tensor vector) {
    return u.dot(vector.dot(u));
  }

  /** @param scalar
   * @return clips given scalar to unit interval [0, 1]
   * @throws Exception if given scalar is significantly outside of unit interval */
  public static Scalar requireUnit(Scalar scalar) {
    Scalar result = Clips.unit().apply(scalar);
    Chop._06.requireClose(result, scalar);
    return result;
  }
}
