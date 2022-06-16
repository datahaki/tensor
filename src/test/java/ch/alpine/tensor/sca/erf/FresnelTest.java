// code by jph
package ch.alpine.tensor.sca.erf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;

class FresnelTest {
  @Test
  void testSimple() {
    Scalar result = Fresnel.FUNCTION.apply(RealScalar.of(2.3));
    Scalar expect = ComplexScalar.of(0.6265617097919535, 0.5531516415607111);
    Tolerance.CHOP.requireClose(result, expect);
  }

  @Test
  void testMedium() {
    Scalar result = Fresnel.FUNCTION.apply(RealScalar.of(80.3));
    Scalar expect = ComplexScalar.of(0.5005583399523246, 0.4960755102174913);
    Tolerance.CHOP.requireClose(result, expect);
  }

  @Test
  void testSimpleNegative() {
    Scalar result = Fresnel.FUNCTION.apply(RealScalar.of(-2.3));
    Scalar expect = ComplexScalar.of(0.6265617097919535, 0.5531516415607111).negate();
    Tolerance.CHOP.requireClose(result, expect);
  }

  @Test
  void testMediumNegative() {
    Scalar result = Fresnel.FUNCTION.apply(RealScalar.of(-80.3));
    Scalar expect = ComplexScalar.of(0.5005583399523246, 0.4960755102174913).negate();
    Tolerance.CHOP.requireClose(result, expect);
  }

  @Test
  void testCSimple() {
    Scalar scalar = Fresnel.FUNCTION.apply(RealScalar.ZERO);
    Tolerance.CHOP.requireClose(scalar, RealScalar.ZERO);
  }

  @Test
  void testCOneP() {
    Scalar scalar = Fresnel.FUNCTION.apply(RealScalar.ONE);
    Tolerance.CHOP.requireClose(scalar, ComplexScalar.of(0.7798934003768226, 0.43825914739035476));
  }

  @Test
  void testComplex() {
    Scalar result = Fresnel.FUNCTION.apply(ComplexScalar.of(1.3, 0.2));
    Scalar expect = ComplexScalar.of(0.5725860309661943, 0.5666620480269878);
    Tolerance.CHOP.requireClose(result, expect);
  }
}
