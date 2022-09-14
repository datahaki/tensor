// code by jph
package ch.alpine.tensor.sca.bes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

class BesselJTest {
  @Test
  void test0() {
    Scalar scalar = BesselJ.of(0, 1.2);
    Chop._08.requireClose(scalar, RealScalar.of(0.6711327442643626));
  }

  @Test
  void test0Negative() {
    Scalar scalar = BesselJ._0(-2.7);
    Chop._08.requireClose(scalar, RealScalar.of(-0.14244937004601185));
  }

  @Test
  void test0Stage() {
    Scalar scalar = BesselJ._0(8.7);
    Chop._08.requireClose(scalar, RealScalar.of(-0.012522732449664538));
  }

  @Test
  void test0Complex() {
    Scalar scalar = BesselJ._0(ComplexScalar.of(1.2, 3.4));
    Chop._04.requireClose(scalar, ComplexScalar.of(3.460244764532168, -5.544948494375138)); // mathematica
  }

  @Test
  void test1() {
    Scalar scalar = BesselJ._1(1.2);
    Chop._08.requireClose(scalar, RealScalar.of(0.4982890575672156));
  }

  @Test
  void test1Negative() {
    Scalar scalar = BesselJ._1(-2.7);
    Chop._08.requireClose(scalar, RealScalar.of(-0.4416013791182532));
  }

  @Test
  void test1Stage() {
    Scalar scalar = BesselJ._1(8.7);
    Chop._08.requireClose(scalar, RealScalar.of(0.2697190240921003));
  }

  @Test
  void test1StageNeg() {
    Scalar scalar = BesselJ._1(-8.7);
    Chop._08.requireClose(scalar, RealScalar.of(-0.2697190240921003));
  }

  @Test
  void test1Complex() {
    Scalar scalar = BesselJ._1(ComplexScalar.of(1.2, 3.4));
    Chop._04.requireClose(scalar, ComplexScalar.of(4.966740719480319, 2.654040063652766)); // mathematica
  }

  @Test
  void testNZero() {
    for (int n = 1; n < 10; ++n)
      assertEquals(BesselJ.of(n, RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  void testNGreater() {
    Scalar expect = RealScalar.of(0.011165861949063964);
    Scalar scalar = BesselJ.of(2, RealScalar.of(0.3));
    Tolerance.CHOP.requireClose(expect, scalar);
  }

  @Test
  void testNEvenXNegative() {
    Scalar expect = RealScalar.of(0.20735589952693204);
    Scalar scalar = BesselJ.of(2, RealScalar.of(-1.4));
    Chop._08.requireClose(expect, scalar);
  }

  @Test
  void testNOddXNegative() {
    Scalar expect = RealScalar.of(-0.050497713288951286);
    Scalar scalar = BesselJ.of(3, RealScalar.of(-1.4));
    Chop._10.requireClose(expect, scalar);
  }
}
