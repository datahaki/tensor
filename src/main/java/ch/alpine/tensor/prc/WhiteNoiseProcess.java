// code by jph
package ch.alpine.tensor.prc;

import ch.alpine.tensor.pdf.c.NormalDistribution;

/** Quote from Mathematica:
 * <blockquote>
 * WhiteNoiseProcess is also known as independent identically distributed (iid) process.
 * WhiteNoiseProcess is a discrete-time random process.
 * The slices of WhiteNoiseProcess are assumed to be independent and identically distributed random variables.
 * </blockquote>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/WhiteNoiseProcess.html">WhiteNoiseProcess</a> */
public enum WhiteNoiseProcess {
  ;
  private static final RandomProcess INSTANCE = DiscreteTimeIidProcess.of(NormalDistribution.standard());

  public static RandomProcess instance() {
    return INSTANCE;
  }
}
