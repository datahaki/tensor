// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.pow.Power;

/** Reference:
 * https://en.wikipedia.org/wiki/Metric_prefix */
/* package */ enum MetricPrefix {
  // YOTTA
  // ZETTA
  // EXA
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
  // ATTO
  // ZEPTO
  // YOCTO
  ;

  private final String prefix;
  private final Scalar factor;
  private final String english;

  private MetricPrefix(String prefix, int exponent) {
    factor = Power.of(10, exponent);
    this.prefix = prefix;
    english = name().charAt(0) + name().substring(1).toLowerCase();
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
   * @param string
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
