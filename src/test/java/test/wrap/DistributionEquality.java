package test.wrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

public record DistributionEquality(Distribution d1, Distribution d2) {
  public void checkRange(int min, int max) {
    Tensor domain = Range.of(min, max);
    {
      PDF pdf1 = PDF.of(d1);
      PDF pdf2 = PDF.of(d2);
      for (Tensor _x : domain) {
        Scalar x = (Scalar) _x;
        Scalar p1 = pdf1.at(x);
        Scalar p2 = pdf2.at(x);
        if (!p1.equals(p2)) {
          System.err.println(pdf1);
          System.err.println(pdf2);
          System.err.println(x);
          System.err.println(p1);
          System.err.println(p2);
          fail();
        }
      }
    }
    {
      CDF cdf1 = CDF.of(d1);
      CDF cdf2 = CDF.of(d2);
      for (Tensor _x : domain) {
        Scalar x = (Scalar) _x;
        Scalar p1 = cdf1.p_lessThan(x);
        Scalar p2 = cdf2.p_lessThan(x);
        if (!p1.equals(p2)) {
          System.err.println(d1);
          System.err.println(d2);
          System.err.println(x);
          System.err.println(p1);
          System.err.println(p2);
          fail();
        }
      }
      InverseCDF inverseCdf1 = InverseCDF.of(d1);
      InverseCDF inverseCdf2 = InverseCDF.of(d2);
      for (Tensor _x : domain) {
        Scalar x = (Scalar) _x;
        Scalar p1 = cdf1.p_lessEquals(x);
        Scalar p2 = cdf2.p_lessEquals(x);
        if (!p1.equals(p2)) {
          System.err.println(d1);
          System.err.println(d2);
          System.err.println(x);
          System.err.println(p1);
          System.err.println(p2);
          fail();
        }
        Scalar q1 = inverseCdf1.quantile(p1);
        Scalar q2 = inverseCdf2.quantile(p2);
        if (!q1.equals(q2)) {
          System.err.println("InverseCDF check");
          System.err.println(d1);
          System.err.println(d2);
          System.err.println(p1);
          System.err.println(p2);
          System.err.println("at " + x);
          System.err.println(q1);
          System.err.println(q2);
          fail();
        }
      }
      RandomVariate.stream(UniformDistribution.unit()).limit(100).forEach(s -> {
        assertEquals(inverseCdf1.quantile(s), inverseCdf2.quantile(s));
      });
      RandomVariate.stream(UniformDistribution.of(min, max)).limit(100).forEach(s -> {
        assertEquals(cdf1.p_lessEquals(s), cdf2.p_lessEquals(s));
        assertEquals(cdf1.p_lessThan(s), cdf2.p_lessThan(s));
      });
    }
    {
      assertEquals(Mean.of(d1), Mean.of(d2));
      assertEquals(Variance.of(d1), Variance.of(d2));
    }
  }
}
