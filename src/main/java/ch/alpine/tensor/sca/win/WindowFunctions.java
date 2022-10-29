// code by jph
package ch.alpine.tensor.sca.win;

import java.util.function.Supplier;

import ch.alpine.tensor.api.ScalarUnaryOperator;

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
  BARTLETT(BartlettWindow.FUNCTION),
  BARTLETT_HANN(BartlettHannWindow.FUNCTION),
  BLACKMAN(BlackmanWindow.FUNCTION),
  BLACKMAN_HARRIS(BlackmanHarrisWindow.FUNCTION),
  BLACKMAN_NUTTALL(BlackmanNuttallWindow.FUNCTION),
  BOHMAN(BohmanWindow.FUNCTION),
  /** default alpha == 3 */
  CAUCHY(CauchyWindow.FUNCTION),
  /** default alpha == 1 */
  CONNES(ConnesWindow.FUNCTION),
  /** default alpha == 1 */
  COSINE(CosineWindow.FUNCTION),
  DIRICHLET(DirichletWindow.FUNCTION),
  EXACT_BLACKMAN(ExactBlackmanWindow.FUNCTION),
  FLAT_TOP(FlatTopWindow.FUNCTION),
  /** default sigma == 3/10 */
  GAUSSIAN(GaussianWindow.FUNCTION),
  HAMMING(HammingWindow.FUNCTION),
  /** default alpha == 1/2 */
  HANN(HannWindow.FUNCTION),
  /** default alpha == 1 */
  HANN_POISSON(HannPoissonWindow.FUNCTION),
  KAISER(KaiserWindow.FUNCTION),
  KAISER_BESSEL(KaiserBesselWindow.FUNCTION),
  LANCZOS(LanczosWindow.FUNCTION),
  NUTTALL(NuttallWindow.FUNCTION),
  PARZEN(ParzenWindow.FUNCTION),
  /** default alpha == 3 */
  POISSON(PoissonWindow.FUNCTION),
  /** default alpha == 1/3 */
  TUKEY(TukeyWindow.FUNCTION),
  /** default alpha == 1 */
  WELCH(WelchWindow.FUNCTION),
  //
  ;

  private final ScalarUnaryOperator scalarUnaryOperator;

  WindowFunctions(ScalarUnaryOperator scalarUnaryOperator) {
    this.scalarUnaryOperator = scalarUnaryOperator;
  }

  @Override // from Supplier
  public ScalarUnaryOperator get() {
    return scalarUnaryOperator;
  }
}
