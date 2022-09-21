// code by jph
package ch.alpine.tensor.sca.bes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

class BesselKTest {
  @Test
  void test0() {
    Scalar scalar = BesselK._0(1.2);
    Chop._08.requireClose(scalar, RealScalar.of(0.3185082202865937));
  }

  @Test
  void test0Stage() {
    Scalar scalar = BesselK._0(3.7);
    Chop._08.requireClose(scalar, RealScalar.of(0.01563065992162666));
  }

  @Test
  void test1() {
    Scalar scalar = BesselK._1(1.2);
    Chop._08.requireClose(scalar, RealScalar.of(0.4345923910607151));
  }

  @Test
  void test1Negative() {
    Scalar scalar = BesselK._1(3.7);
    Chop._08.requireClose(scalar, RealScalar.of(0.01762803510222326));
  }

  @Test
  void testLimit() {
    Tolerance.CHOP.requireZero(RealScalar.of(BesselK.kn(0, 700)));
    assertEquals(BesselK.kn(0, 701), 0.0);
  }
}
