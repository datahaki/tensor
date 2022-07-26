// code by jph
package ch.alpine.tensor.mat.ev;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.io.MathematicaFormat;

/** container class to host results of unsorted {@link Eigensystem} and convert
 * to {@link Eigensystem} with eigenvalues sorted in decreasing order. */
/* package */ class EigensystemImpl implements Eigensystem, Serializable {
  private final Tensor d;
  private final Tensor V;

  /** @param matrix symmetric, non-empty, and real valued
   * @param chop for symmetry check
   * @throws Exception if input is not a real symmetric matrix */
  public EigensystemImpl(Eigensystem eigensystem) {
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
    return MathematicaFormat.concise("Eigensystem", values(), vectors());
  }
}
