// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

/* package */ enum TestHelper {
  ;
  public static Tensor sample(NdBox ndBox) {
    return Tensor.of(IntStream.range(0, ndBox.dimensions()) //
        .mapToObj(index -> ndBox.clip(index)) //
        .map(UniformDistribution::of) //
        .map(RandomVariate::of));
  }
}
