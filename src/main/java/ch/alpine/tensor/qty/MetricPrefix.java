// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Power;

/* package */ enum MetricPrefix {
  PETA("P", 15), //
  TERA("T", 12), //
  GIGA("G", 9), //
  MEGA("M", 6), //
  KILO("k", 3), //
  HECTO("h", 2), //
  DECA("da", 1), //
  DECI("d", -1), //
  CENTI("c", -2), //
  MILLI("m", -3), //
  MICRO("u", -6), //
  NANO("n", -9), //
  PICO("p", -12), //
  FEMTO("f", -15), //
  ;

  private final String prefix;
  private final Scalar factor;
  private final String english;

  private MetricPrefix(String prefix, int exponent) {
    factor = Power.of(10, exponent);
    this.prefix = prefix;
    english = name().charAt(0) + name().substring(1).toLowerCase();
  }

  public String prefix() {
    return prefix;
  }

  public String english() {
    return english;
  }

  public Scalar factor() {
    return factor;
  }
}
