// code by jph
package ch.alpine.tensor.alg;

import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.spa.SparseEntryVisitor;

/* package */ class SparseEntryTranspose implements SparseEntryVisitor<Tensor> {
  private final int[] sigma;
  private final int[] array;
  private final List<Integer> index;
  private final Tensor tensor;

  public SparseEntryTranspose(int[] sigma, Tensor tensor) {
    this.sigma = sigma;
    array = new int[sigma.length];
    index = Integers.asList(array);
    this.tensor = tensor;
  }

  @Override // from SparseEntryVisitor
  public void accept(List<Integer> list, Scalar scalar) {
    for (int i = 0; i < list.size(); ++i)
      array[sigma[i]] = list.get(i);
    tensor.set(scalar, index);
  }

  @Override // from SparseEntryVisitor
  public Tensor result() {
    return tensor;
  }
}