// code by jph
package ch.alpine.tensor.sca.bes;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;

class BesselYTest {
  @Test
  void test0() {
    Scalar scalar = BesselY._0(1.2);
    Chop._08.requireClose(scalar, RealScalar.of(0.22808350322719695));
  }

  @Test
  void test0Stage() {
    Scalar scalar = BesselY._0(8.7);
    Chop._08.requireClose(scalar, RealScalar.of(0.2699991703467446));
  }

  @Test
  void test1() {
    Scalar scalar = BesselY._1(1.2);
    Chop._08.requireClose(scalar, RealScalar.of(-0.621136379748848));
  }

  @Test
  void test1Negative() {
    Scalar scalar = BesselY._1(8.7);
    Chop._08.requireClose(scalar, RealScalar.of(0.028010959176806096));
  }
}
