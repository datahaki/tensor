// code by jph
package ch.alpine.tensor.alg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Lists;

/* package */ abstract class PadBase implements TensorUnaryOperator {
  private final Tensor element;
  private final List<Integer> dimensions;

  protected PadBase(Tensor element, List<Integer> dimensions) {
    this.element = element;
    this.dimensions = dimensions;
  }

  @Override // from TensorUnaryOperator
  public final Tensor apply(Tensor tensor) {
    int length = tensor.length();
    final int dim0 = dimensions.getFirst();
    if (1 < dimensions.size()) { // recur
      PadBase padBase = get(element, Lists.rest(dimensions));
      if (dim0 <= length)
        return Tensor.of(trim(tensor, dim0).map(padBase));
      List<Integer> copy = new ArrayList<>(dimensions);
      copy.set(0, dim0 - length);
      return join(Tensor.of(tensor.stream().map(padBase)), ConstantArray.of(element, copy));
    }
    return dim0 <= length //
        ? Tensor.of(trim(tensor, dim0).map(Tensor::copy))
        : join(tensor, ConstantArray.of(element, dim0 - length));
  }

  abstract PadBase get(Tensor element, List<Integer> rest);

  protected abstract Stream<Tensor> trim(Tensor tensor, int dim0);

  protected abstract Tensor join(Tensor tensor, Tensor pad);
}
