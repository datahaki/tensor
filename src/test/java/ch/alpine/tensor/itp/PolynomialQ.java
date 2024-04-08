// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.ply.Polynomial;

enum PolynomialQ {
  ;
  // TODO TENSOR ugly, magic const
  public static boolean of(ScalarUnaryOperator suo, Clip clip, int degree) {
    int n = 1000;
    Distribution distribution = DiscreteUniformDistribution.of(0, n);
    Tensor vector = RandomVariate.of(distribution, degree * 2 + 1);
    ScalarUnaryOperator sca = LinearInterpolation.of(clip);
    Tensor xdata = Tensor.of(vector.stream().distinct().limit(degree + 2).map(s -> s.divide(RealScalar.of(n)).map(sca)));
    Polynomial polynomial = Polynomial.fit(xdata, xdata.map(suo), degree + 1);
    return polynomial.degree() == degree;
  }
}
