// code by jph
package ch.alpine.tensor.sca.bes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BesselTest {
  @Test
  void testI0() {
    double i0 = BesselI._0(2.3).number().doubleValue();
    assertEquals(i0, 2.8296056006275854);
  }

  @Test
  void testI1() {
    double i1 = BesselI._1(2.3).number().doubleValue();
    assertEquals(i1, 2.097800027517421);
  }

  @Test
  void testJ0() {
    double i0 = BesselJ._0(2.3).number().doubleValue();
    assertEquals(i0, 0.055539786578263826); // !
    // ............. 0.055539784445602064);
  }

  @Test
  void testJ1() {
    double i1 = BesselJ._1(2.3).number().doubleValue();
    assertEquals(i1, 0.5398725327300683);
    // ............. 0.5398725326043137
  }

  @Test
  void testJ2() {
    double i1 = BesselJ.of(2, 2.3).number().doubleValue();
    // System.out.println(i1);
    // ............. 0.41391459173206196
    assertEquals(i1, 0.41391458970875217);
  }

  @Test
  void testK0() {
    double i0 = BesselK._0(2.3).number().doubleValue();
    assertEquals(i0, 0.07913993300209364); // !
    // ............. 0.07913993300209367
  }

  @Test
  void testK1() {
    double i1 = BesselK._1(2.3).number().doubleValue();
    assertEquals(i1, 0.09498244384536267);
    // ............. 0.09498244384536267
  }

  @Test
  void testK2() {
    double i1 = Bessel.kn(2, 2.3);
    assertEquals(i1, 0.16173336243284375);
    // ............. 0.1617333624328438
  }

  @Test
  void testY0() {
    double i0 = BesselY._0(2.3).number().doubleValue();
    // System.out.println(i0);
    assertEquals(i0, 0.5180753919477299); // !
    // ............. 0.5180753962076221
  }

  @Test
  void testY1() {
    double i1 = BesselY._1(2.3).number().doubleValue();
    // System.out.println(i1);
    assertEquals(i1, 0.0522773155615776);
    // ............. 0.05227731584422475
  }

  @Test
  void testY5() {
    double i1 = BesselY.of(5, 2.3).number().doubleValue();
    // System.out.println(i1);
    assertEquals(i1, -5.4143236590500425);
    // ............. -5.414323703733118
  }
}
