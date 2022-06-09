// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/* package */ enum InfluenceMatrixDemo {
  ;
  public static void main(String[] args) {
    Timing timing_im = Timing.stopped();
    Timing timing_ma = Timing.stopped();
    Distribution distribution = UniformDistribution.of(-1, 1);
    for (int count = 0; count < 10; ++count) {
      Tensor design = RandomVariate.of(distribution, 10 + count, 5);
      // InfluenceMatrix influenceMatrix = ;
      timing_im.start();
      InfluenceMatrix.of(design).leverages_sqrt();
      timing_im.stop();
      timing_ma.start();
      new Mahalanobis(design).leverages_sqrt();
      timing_ma.stop();
    }
    System.out.println(timing_im.seconds());
    System.out.println(timing_ma.seconds());
  }
}
