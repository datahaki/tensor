// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.pow.Power;

/** Reference:
 * https://en.wikipedia.org/wiki/Metric_prefix */
/* package */ enum MetricPrefix {
  // YOTTA("Y", 24), //
  // ZETTA("Z", 21), //
  // EXA("E", 18), //
  PETA("P", 15), //
  TERA("T", 12), //
  GIGA("G", 9), //
  MEGA("M", 6), //
  KILO("k", 3), //
  HECTO("h", 2), //
  DECA("da", 1), //
  NULL("", 0), //
  DECI("d", -1), //
  CENTI("c", -2), //
  MILLI("m", -3), //
  MICRO("u", -6), //
  NANO("n", -9), //
  PICO("p", -12), //
  FEMTO("f", -15), //
  // ATTO("a", -18), //
  // ZEPTO("z", -21), //
  // YOCTO("y", -24), //
  ;

  private final int exponent;
  private final String prefix;
  private final Scalar factor;
  private final String english;

  MetricPrefix(String prefix, int exponent) {
    this.exponent = exponent;
    factor = Power.of(10, exponent);
    this.prefix = prefix;
    english = name().charAt(0) + name().substring(1).toLowerCase();
  }

  /** @return */
  public int exponent() {
    return exponent;
  }

  /** Example:
   * GIGA.prefix("Hz") == "GHz"
   * 
   * @param string
   * @return */
  public String prefix(String string) {
    return prefix + string;
  }

  /** Example:
   * GIGA.english("Hertz") == "Gigahertz"
   * 
   * @param string with at least one character
   * @return */
  public String english(String string) {
    return equals(NULL) //
        ? string
        : english + Character.toLowerCase(string.charAt(0)) + string.substring(1);
  }

  public Scalar factor() {
    return factor;
  }
}
