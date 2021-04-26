// code by jph
package ch.ethz.idsc.tensor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.tensor.num.Pi;

/* package */ enum TestHelper {
  ;
  static final List<Scalar> SCALARS = Arrays.asList( //
      RealScalar.ZERO, RealScalar.ONE, //
      RationalScalar.of(-7, 3), //
      RealScalar.of(-3.4), //
      DecimalScalar.of(new BigDecimal("4.123"), 34), //
      Pi.in(50), //
      ComplexScalar.of(2, 3), //
      ComplexScalar.of(6, -8.3), //
      ComplexScalar.of(RealScalar.of(2.3), RationalScalar.of(2, 9)), //
      ComplexScalar.of(Pi.in(40), RationalScalar.of(-4, 5)), //
      ComplexScalar.of(RationalScalar.of(-2, 17), Pi.in(20)), //
      ComplexScalar.of(RationalScalar.of(-7, 13), RationalScalar.of(53, 887)));
}
