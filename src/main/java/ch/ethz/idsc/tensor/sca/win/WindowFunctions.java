// code by jph
package ch.ethz.idsc.tensor.sca.win;

import java.util.function.Supplier;

import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** Filter-Design Window Functions
 * 
 * <p>Quote from Mathematica:
 * "[...] set of window functions that are commonly used in the design of finite impulse response
 * (FIR) filters, with additional applications in spectral and spatial analysis. In filter design,
 * windows are typically used to reduce unwanted ripples in the frequency response of a filter."
 * 
 * <p>Wikipedia lists the spectrum for each window
 * https://en.wikipedia.org/wiki/Window_function
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/guide/WindowFunctions.html">WindowFunctions</a> */
public enum WindowFunctions implements Supplier<ScalarUnaryOperator> {
  BARTLETT(BartlettWindow.FUNCTION), //
  BARTLETT_HANN(BartlettHannWindow.FUNCTION), //
  BLACKMAN(BlackmanWindow.FUNCTION), //
  BLACKMAN_HARRIS(BlackmanHarrisWindow.FUNCTION), //
  BLACKMAN_NUTTALL(BlackmanNuttallWindow.FUNCTION), //
  BOHMAN(BohmanWindow.FUNCTION),
  /** default */
  CAUCHY(CauchyWindow.FUNCTION),
  /** default */
  CONNES(ConnesWindow.FUNCTION),
  /** default */
  COSINE(CosineWindow.FUNCTION), //
  DIRICHLET(DirichletWindow.FUNCTION), //
  EXACT_BLACKMAN(ExactBlackmanWindow.FUNCTION), //
  FLAT_TOP(FlatTopWindow.FUNCTION), //
  /** default */
  GAUSSIAN(GaussianWindow.FUNCTION), //
  HAMMING(HammingWindow.FUNCTION),
  /** default */
  HANN(HannWindow.FUNCTION),
  /** default */
  HANN_POISSON(HannPoissonWindow.FUNCTION), //
  LANCZOS(LanczosWindow.FUNCTION), //
  NUTTALL(NuttallWindow.FUNCTION), //
  PARZEN(ParzenWindow.FUNCTION), //
  /** default */
  POISSON(PoissonWindow.FUNCTION), //
  /** default */
  TUKEY(TukeyWindow.FUNCTION),
  /** default */
  WELCH(WelchWindow.FUNCTION), //
  ;

  private final ScalarUnaryOperator scalarUnaryOperator;

  private WindowFunctions(ScalarUnaryOperator scalarUnaryOperator) {
    this.scalarUnaryOperator = scalarUnaryOperator;
  }

  @Override // from Supplier
  public ScalarUnaryOperator get() {
    return scalarUnaryOperator;
  }
}
