// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BinomialProcess.html">BinomialProcess</a> */
/* package */ class BinomialProcess implements RandomProcess, Serializable {
  private final Scalar p;

  public BinomialProcess(Scalar p) {
    this.p = p;
  }

  @Override
  public Tensor path() {
    p.zero();
    // TODO TENSOR PRC Auto-generated method stub
    return null;
  }
}
