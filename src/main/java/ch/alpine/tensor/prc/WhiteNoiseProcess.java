// code by jph
package ch.alpine.tensor.prc;

import ch.alpine.tensor.pdf.c.NormalDistribution;

public enum WhiteNoiseProcess {
  ;
  private static final RandomProcess INSTANCE = new DiscreteProcess(NormalDistribution.standard());

  public static RandomProcess instance() {
    return INSTANCE;
  }
}
