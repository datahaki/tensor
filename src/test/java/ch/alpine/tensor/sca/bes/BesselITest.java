// code by jph
package ch.alpine.tensor.sca.bes;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;

class BesselITest {
  @Test
  void test0() {
    Scalar scalar = BesselI._0(1.2);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(1.393725584134064));
  }

  @Test
  void test0Negative() {
    Scalar scalar = BesselI._0(-2.7);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(3.841650976595935));
  }

  @Test
  void test0Stage() {
    Scalar scalar = BesselI._0(8.7);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(824.4498841770287));
  }

  @Test
  void test1() {
    Scalar scalar = BesselI._1(1.2);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.7146779415526429));
  }

  @Test
  void test1Negative() {
    Scalar scalar = BesselI._1(-2.7);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(-3.016107693161407));
  }

  @Test
  void test1Stage() {
    Scalar scalar = BesselI._1(8.7);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(775.5115073053471));
  }
}
