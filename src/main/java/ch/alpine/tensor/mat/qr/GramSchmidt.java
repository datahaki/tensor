// code by jph
package ch.alpine.tensor.mat.qr;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.ArgMax;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Conjugate;

/** the {@link GramSchmidt} algorithm is efficient for obtaining the {@link InfluenceMatrix}
 * for matrices, including rank-deficient matrices.
 * 
 * Reference:
 * Chapter "Gram-Schmidt with Column Pivoting"
 * in "Linear Algebra and Learning from Data", p. 129
 * by Gilbert Strang, 2019
 * 
 * @see InfluenceMatrix */
public class GramSchmidt extends QRDecompositionBase implements Serializable {
  /** @param matrix of any dimension
   * @return */
  public static QRDecomposition of(Tensor matrix) {
    return new GramSchmidt(matrix);
  }

  // ---
  private final Tensor qInv = Tensors.empty();
  private final Tensor r = Tensors.empty();
  private final int[] sigma;

  private GramSchmidt(Tensor matrix) {
    int m = Unprotect.dimension1(matrix);
    int[] _sigma = new int[m];
    for (int i = 0; i < m; ++i) {
      Tensor a = matrix;
      Tensor norms = Tensor.of(IntStream.range(0, m).mapToObj(l -> Vector2Norm.of(a.get(Tensor.ALL, l))));
      _sigma[i] = ArgMax.of(norms.map(Unprotect::withoutUnit));
      Scalar norm = norms.Get(_sigma[i]);
      if (Tolerance.CHOP.isZero(norm))
        break;
      Tensor q = a.get(Tensor.ALL, _sigma[i]).divide(norm);
      q = Vector2Norm.NORMALIZE.apply(q); // thorough normalization does not make a difference in the tests
      Tensor qc = Conjugate.of(q);
      qInv.append(qc);
      Tensor ri = qc.dot(matrix);
      r.append(ri);
      matrix = matrix.add(TensorProduct.of(q, ri.negate()));
    }
    sigma = IntStream.of(_sigma).limit(r.length()).toArray();
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return r;
  }

  @Override // from QRDecomposition
  public Tensor getQConjugateTranspose() {
    return qInv;
  }

  @Override
  public int[] sigma() {
    return sigma;
  }
}
