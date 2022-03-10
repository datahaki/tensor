// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/* package */ enum TestHelper {
  ;
  public static Tensor sample(CoordinateBoundingBox coordinateBoundingBox) {
    return Tensor.of(IntStream.range(0, coordinateBoundingBox.dimensions()) //
        .mapToObj(coordinateBoundingBox::getClip) //
        .map(UniformDistribution::of) //
        .map(RandomVariate::of));
  }
}
