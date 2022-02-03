// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/* package */ enum TestHelper {
  ;
  public static Tensor sample(CoordinateBoundingBox box) {
    return Tensor.of(IntStream.range(0, box.dimensions()) //
        .mapToObj(index -> box.getClip(index)) //
        .map(UniformDistribution::of) //
        .map(RandomVariate::of));
  }
}
