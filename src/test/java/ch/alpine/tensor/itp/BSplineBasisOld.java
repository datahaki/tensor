// code by jph
package ch.alpine.tensor.itp;

abstract class BSplineBasisOld {
  public static final BSplineBasisOld constant = new BSplineBasisOld() {
    @Override
    public double at(double t) {
      return -0.5 <= t && t < +0.5 ? 1 : 0;
    }
  };
  public static final BSplineBasisOld linear = new BSplineBasisOld() {
    @Override
    public double at(double t) {
      return Math.max(0, 1 - Math.abs(t));
    }
  };
  public static final BSplineBasisOld quadratic = new BSplineBasisOld() {
    @Override
    public double at(double t) {
      if (t < 0)
        return at(-t);
      double t2 = t * t;
      if (t < +0.5)
        return 0.75 - t2;
      if (+0.5 <= t && t < +1.5)
        return 1.125 - 1.5 * t + 0.5 * t2;
      return 0;
    }
  };
  public static final BSplineBasisOld cubic = new BSplineBasisOld() {
    @Override
    public double at(double t) {
      if (t < 0)
        return at(-t);
      if (t < 1) {
        double t2 = t * t;
        return 2 / 3. - t2 + 0.5 * t2 * t;
      }
      if (t < 2) {
        double t2 = 2 - t;
        return 1 / 6. * t2 * t2 * t2;
      }
      return 0;
    }
  };
  public static final BSplineBasisOld[] of = new BSplineBasisOld[] { constant, linear, quadratic, cubic };

  public abstract double at(double t);
}
