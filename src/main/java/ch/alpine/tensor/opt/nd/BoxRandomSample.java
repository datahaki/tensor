// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/** the parameters define the coordinate bounds of the axis-aligned box
 * from which the samples are drawn
 * 
 * @see CoordinateBoundingBox */
public record BoxRandomSample(CoordinateBoundingBox coordinateBoundingBox) implements RandomSampleInterface, Serializable {
  /** @param coordinateBoundingBox axis-aligned bounding box
   * @return
   * @see CoordinateBounds */
  @Override
  public Tensor randomSample(RandomGenerator randomGenerator) {
    return Tensor.of(coordinateBoundingBox.stream() //
        .map(UniformDistribution::of) //
        .map(distribution -> RandomVariate.of(distribution, randomGenerator)));
  }
}
