// code by jph
package ch.alpine.tensor.sca.bes;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;

class BesselJTest {
  @Test
  void test0() {
    Scalar scalar = BesselJ._0(1.2);
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
}
