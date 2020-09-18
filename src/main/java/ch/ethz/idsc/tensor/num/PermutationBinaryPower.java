// code by jph
package ch.ethz.idsc.tensor.num;

import java.math.BigInteger;

import ch.ethz.idsc.tensor.alg.BinaryPower;

/** Hint: implementation is not most efficient! */
/* package */ class PermutationBinaryPower extends BinaryPower<Cycles> {
  public static Cycles of(Cycles cycles, long exponent) {
    return new PermutationBinaryPower().apply(cycles, exponent);
  }

  public static Cycles of(Cycles cycles, BigInteger exponent) {
    return new PermutationBinaryPower().apply(cycles, exponent);
  }

  /***************************************************/
  private PermutationBinaryPower() {
    // ---
  }

  @Override // from BinaryPower
  protected Cycles zeroth() {
    return Cycles.identity();
  }

  @Override // from BinaryPower
  protected Cycles invert(Cycles cycles) {
    return cycles.inverse();
  }

  @Override // from BinaryPower
  protected Cycles multiply(Cycles cycles1, Cycles cycles2) {
    return cycles1.product(cycles2);
  }
}
