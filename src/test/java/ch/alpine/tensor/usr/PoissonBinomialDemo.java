// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.HistogramDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.PoissonBinomialDistribution;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

/* package */ enum PoissonBinomialDemo {
  ;
  public static void main(String[] args) {
    Tensor p_vector = RandomVariate.of(UniformDistribution.unit(), 1000);
    Distribution distribution = PoissonBinomialDistribution.of(p_vector);
    Tensor samples;
    {
      Timing timing = Timing.started();
      samples = RandomVariate.of(distribution, 1000);
      double seconds = timing.seconds();
      System.out.println("sec  pbin = " + seconds);
    }
    Distribution histogram = HistogramDistribution.of(samples);
    System.out.println("mean hist = " + Mean.of(histogram));
    System.out.println("mean pbin = " + Mean.of(distribution));
    System.out.println("var  hist = " + Variance.of(histogram));
    System.out.println("var  pbin = " + Variance.of(distribution));
    {
      Timing timing = Timing.started();
      RandomVariate.of(histogram, 1000);
      double seconds = timing.seconds();
      System.out.println("sec  hist = " + seconds);
    }
  }
}
