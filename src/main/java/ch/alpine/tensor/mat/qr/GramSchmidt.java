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
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Conjugate;

/** the {@link GramSchmidt} algorithm is efficient for obtaining the {@link InfluenceMatrix}
 * for matrices, including rank-deficient matrices.
 * 
 * Reference:
 * Chapter "Gram-Schmidt with Column Pivoting"
 * in "Linear Algebra and Learning from Data", p. 129
 * by Gilbert Strang, 2019
 * 
 * @see InfluenceMatrix
 * @see MatrixRank */
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
      Tensor norms = matrix.stream() //
          .map(Abs::of) //
          .reduce(Tensor::add) //
          .orElseThrow();
      _sigma[i] = ArgMax.of(norms.map(Unprotect::withoutUnit));
      Tensor col = matrix.get(Tensor.ALL, _sigma[i]);
      Scalar norm = Vector2Norm.of(col);
      if (Tolerance.CHOP.isZero(norm))
        break;
      Tensor q = col.divide(norm);
      // we refrain from using the more thorough Normalization for vector q
      // ... until data shows that it is necessary
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
