// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

class ArcCosTest {
  @Test
  void testReal() {
    assertEquals(ArcCos.of(Scalars.fromString("1")), RealScalar.of(Math.acos(1)));
    assertEquals(ArcCos.of(Scalars.fromString("-1")), RealScalar.of(Math.acos(-1)));
  }

  @Test
  void testRealOutside() {
    Scalar s = RealScalar.of(3);
    Scalar r = ArcCos.of(s);
    assertEquals(r, ComplexScalar.of(0, +1.7627471740390872));
  }

  @Test
  void testRealOutsideNeg() {
    Scalar s = RealScalar.of(-3);
    Scalar r = ArcCos.of(s);
    assertEquals(r, ArcCos.of(s));
    Chop._14.requireClose(r, ComplexScalar.of(3.141592653589793, -1.762747174039086));
  }

  @Test
  void testComplex() {
    Scalar s = ComplexScalar.of(5, -7);
    Scalar r = ArcCos.of(s);
    assertEquals(r, ArcCos.of(s));
    // num/(double)den double conversion:
    // 0.9537320301189085............. + 2.846288828208389
    // Mathematica:
    // 0.95373203011890309673440616093 + 2.84628882820838653446176723296 I
    // bigDecimal double conversion:
    // 0.9537320301188659............. + 2.846288828208396
    // _14 is insufficient on aarch64
    // aarch64: 0.9537320301188748+2.8462888282083836*I
    // x86_64 : 0.9537320301188659+2.846288828208396*I
    Tolerance.CHOP.requireClose(r, ComplexScalar.of(0.9537320301188659, +2.846288828208396));
  }
}
