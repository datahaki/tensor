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
import ch.alpine.tensor.nrm.Vector2Norm;

/** TODO in the current implementation r is a permutation of an upper triangular matrix
 * 
 * Reference:
 * Chapter "Gram-Schmidt with Column Pivoting"
 * in "Linear Algebra and Learning from Data", p. 129
 * by Gilbert Strang, 2019 */
/* package */ class GramSchmidt extends QRDecompositionBase implements Serializable {
  private final Tensor qInv = Tensors.empty();
  private final Tensor r = Tensors.empty();

  public GramSchmidt(Tensor matrix) {
    int m = Unprotect.dimension1(matrix);
    int[] ind = new int[m];
    for (int i = 0; i < m; ++i) {
      Tensor a = matrix;
      Tensor norms = Tensor.of(IntStream.range(0, m).mapToObj(l -> Vector2Norm.of(a.get(Tensor.ALL, l))));
      int j = ArgMax.of(norms);
      ind[i] = j;
      // System.out.println(j);
      Tensor q = Vector2Norm.NORMALIZE.apply(a.get(Tensor.ALL, j));
      qInv.append(q);
      Tensor ri = q.dot(matrix);
      r.append(ri);
      matrix = matrix.subtract(TensorProduct.of(q, ri));
    }
    // System.out.println(Tensors.vectorInt(ind));
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return r;
  }

  @Override // from QRDecomposition
  public Tensor getQConjugateTranspose() {
    return qInv;
  }

  @Override // from QRDecomposition
  public Scalar det() {
    return null;
  }
}
