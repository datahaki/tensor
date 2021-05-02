// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.pdf.BinomialDistribution;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.GeometricDistribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Round;

/* package */ enum PDFDemo {
  ;
  public static void main(String[] args) {
    { // random variate from a distribution
      Distribution distribution = NormalDistribution.of(RealScalar.of(-2), RealScalar.of(3));
      System.out.println(distribution.getClass().getSimpleName());
      Tensor array = RandomVariate.of(distribution, 2, 3);
      System.out.println(Pretty.of(array.map(Round._4)));
    }
    { // probability
      Distribution distribution = BinomialDistribution.of(20, RationalScalar.of(1, 3));
      System.out.println(distribution.getClass().getSimpleName());
      PDF pdf = PDF.of(distribution);
      System.out.println("P(X=14) = " + pdf.at(RealScalar.of(14)));
    }
    { // cumulative density
      Distribution distribution = GeometricDistribution.of(RationalScalar.of(1, 8));
      System.out.println(distribution.getClass().getSimpleName());
      CDF cdf = CDF.of(distribution);
      System.out.println("P(X<14) = " + cdf.p_lessThan(RealScalar.of(14)));
    }
  }
}
