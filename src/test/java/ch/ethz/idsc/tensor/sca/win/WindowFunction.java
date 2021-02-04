// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/* package */ enum WindowFunction implements ScalarUnaryOperator {
  Bartlett(BartlettWindow.FUNCTION), //
  BartlettHann(BartlettHannWindow.FUNCTION), //
  BlackmanHarris(BlackmanHarrisWindow.FUNCTION), //
  BlackmanNuttall(BlackmanNuttallWindow.FUNCTION), //
  Blackman(BlackmanWindow.FUNCTION), //
  Bohman(BohmanWindow.FUNCTION), //
  Cauchy(CauchyWindow.FUNCTION), //
  Connes(ConnesWindow.FUNCTION), //
  Cosine(CosineWindow.FUNCTION), //
  Dirichlet(DirichletWindow.FUNCTION), //
  ExactBlackman(ExactBlackmanWindow.FUNCTION), //
  FlatTop(FlatTopWindow.FUNCTION), //
  Gaussian(GaussianWindow.FUNCTION), //
  Hamming(HammingWindow.FUNCTION), //
  Hann(HannWindow.FUNCTION), //
  HannPoisson(HannPoissonWindow.FUNCTION), //
  Lanczos(LanczosWindow.FUNCTION), //
  Nuttall(NuttallWindow.FUNCTION), //
  Parzen(ParzenWindow.FUNCTION), //
  Poisson(PoissonWindow.FUNCTION), //
  Tukey(TukeyWindow.FUNCTION), //
  Welch(WelchWindow.FUNCTION), //
  ;

  private final ScalarUnaryOperator scalarUnaryOperator;

  private WindowFunction(ScalarUnaryOperator scalarUnaryOperator) {
    this.scalarUnaryOperator = scalarUnaryOperator;
  }

  @Override
  public Scalar apply(Scalar tensor) {
    return scalarUnaryOperator.apply(tensor);
  }

  public ScalarUnaryOperator get() {
    return scalarUnaryOperator;
  }
}
