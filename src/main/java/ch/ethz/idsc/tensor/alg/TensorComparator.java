// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** consistent with Mathematica::Sort */
public enum TensorComparator implements Comparator<Tensor> {
  INSTANCE;

  @Override // from Comparator
  public int compare(Tensor tensor1, Tensor tensor2) {
    int l1 = tensor1.length();
    int l2 = tensor2.length();
    int cmp = Integer.compare(l1, l2);
    if (cmp == 0) {
      if (l1 == Scalar.LENGTH) // implies that l2 == Scalar.LENGTH
        return Scalars.compare((Scalar) tensor1, (Scalar) tensor2);
      for (int index = 0; index < l1 && cmp == 0; ++index)
        cmp = compare(tensor1.get(index), tensor2.get(index));
    }
    return cmp;
  }
}
