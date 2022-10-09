// code by jph
package ch.alpine.tensor.tmp;

/** @see TimeSeries */
public enum ResamplingMethods {
  ;
  /** linear interpolation
   * default in Mathematica as "LinearInterpolation", and "{Interpolation, 1}" */
  public static final ResamplingMethod LINEAR_INTERPOLATION = new Linear();
  public static final ResamplingMethod LINEAR_INTERPOLATION_SPARSE = new LinearSparse();
  /** in Mathematica: HoldValueFromLeft */
  public static final ResamplingMethod HOLD_VALUE_FROM_LEFT = new HoldLo();
  /** suitable for exact precision data sets */
  public static final ResamplingMethod HOLD_VALUE_FROM_LEFT_SPARSE = new HoldLoSparse();
  /** in Mathematica: HoldValueFromRight */
  public static final ResamplingMethod HOLD_VALUE_FROM_RIGHT = new HoldHi();
  /** suitable for exact precision data sets */
  public static final ResamplingMethod HOLD_VALUE_FROM_RIGHT_SPARSE = new HoldHiSparse();
  /** in Mathematica: None */
  public static final ResamplingMethod NONE = new None();
}
