// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Ordering;
import ch.ethz.idsc.tensor.sca.Chop;

/** The Jacobi transformations of a real symmetric matrix establishes the
 * diagonal matrix D
 * 
 * D == V* . A . V,
 * 
 * where the matrix V,
 * 
 * V == P1 * P2 * P3 * ...,
 * 
 * is the product of the successive Jacobi rotation matrices Pi. The diagonal
 * entries of D are the eigenvalues of A and the columns of V are the
 * eigenvectors of A.
 * 
 * Implementation also works for matrices with entries of type Quantity of
 * the same unit. */
/* package */ class EigensystemImpl implements Eigensystem, Serializable {
  private final Tensor d;
  private final Tensor V;

  /** @param matrix symmetric, non-empty, and real valued
   * @param chop for symmetry check
   * @throws Exception if input is not a real symmetric matrix */
  public EigensystemImpl(Tensor matrix, Chop chop) {
    Eigensystem eigensystem = new JacobiMethod(matrix, chop);
    Tensor values = eigensystem.values();
    int[] ordering = Ordering.DECREASING.of(values);
    d = Tensor.of(IntStream.of(ordering).mapToObj(values::Get));
    V = Tensor.of(IntStream.of(ordering).mapToObj(eigensystem.vectors()::get));
  }

  @Override // from Eigensystem
  public Tensor values() {
    return d;
  }

  @Override // from Eigensystem
  public Tensor vectors() {
    return V;
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", //
        Eigensystem.class.getSimpleName(), //
        Tensors.message(values(), vectors()));
  }
}
