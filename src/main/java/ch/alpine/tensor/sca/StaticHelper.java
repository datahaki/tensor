// code by jph
package ch.alpine.tensor.sca;

import java.math.BigDecimal;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.Scalar;

/** reviewed */
/* package */ enum StaticHelper {
  ;
  public static final Scalar _1 = DecimalScalar.of(new BigDecimal("0.1"));
  public static final Scalar _2 = DecimalScalar.of(new BigDecimal("0.01"));
  public static final Scalar _3 = DecimalScalar.of(new BigDecimal("0.001"));
  public static final Scalar _4 = DecimalScalar.of(new BigDecimal("0.0001"));
  public static final Scalar _5 = DecimalScalar.of(new BigDecimal("0.00001"));
  public static final Scalar _6 = DecimalScalar.of(new BigDecimal("0.000001"));
  public static final Scalar _7 = DecimalScalar.of(new BigDecimal("0.0000001"));
  public static final Scalar _8 = DecimalScalar.of(new BigDecimal("0.00000001"));
  public static final Scalar _9 = DecimalScalar.of(new BigDecimal("0.000000001"));
}
