// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.ply.Polynomial;

class PlausibleRationalTest {
  @Test
  void testComplex() {
    Scalar scalar = PlausibleRational.of(10).apply(ComplexScalar.of(0.5, 0.2344523));
    assertEquals(Rational.HALF.toString(), Re.FUNCTION.apply(scalar).toString());
  }

  @Test
  void testQuantity() {
    Scalar scalar = PlausibleRational.of(10).apply(Scalars.fromString("0.333333333333333333333333333[m]"));
    assertEquals(scalar.toString(), "1/3[m]");
  }

  @Test
  void testPlausibleRational() {
    ScalarUnaryOperator suo = PlausibleRational.of(10);
    assertDoesNotThrow(() -> Serialization.copy(suo));
    Scalar scalar = suo.apply(RealScalar.of(0.0));
    ExactScalarQ.require(scalar);
    assertEquals(suo.apply(Pi.VALUE), Pi.VALUE);
  }

  @Test
  void testRoots() {
    Polynomial polynomial = Polynomial.fromRoots(Tensors.fromString("{1/2,7/5,20/3}"));
    Tensor roots = polynomial.roots();
    ScalarUnaryOperator suo = PlausibleRational.of(10);
    Tensor maps = roots.maps(suo);
    IO.println(maps);
  }
}
