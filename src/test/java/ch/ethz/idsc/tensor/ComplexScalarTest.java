// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class ComplexScalarTest extends TestCase {
  public void testSign() {
    Scalar scalar = ComplexScalar.of(4, 3);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(scalar, result.multiply(RealScalar.of(5)));
  }

  public void testSignEps() {
    Scalar scalar = ComplexScalar.of(0, Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.I);
  }

  public void testSignEpsReIm() {
    Scalar scalar = ComplexScalar.of(Double.MIN_VALUE, Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.of(0.7071067811865475, 0.7071067811865475));
  }

  public void testSignEpsReImNeg() {
    Scalar scalar = ComplexScalar.of(Double.MIN_VALUE, -Double.MIN_VALUE);
    Scalar result = Sign.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(result, ComplexScalar.of(0.7071067811865475, -0.7071067811865475));
  }

  public void testConstructFail() {
    try {
      ComplexScalar.of(RealScalar.ONE, ComplexScalar.I);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ComplexScalar.of(ComplexScalar.I, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNullFail() {
    try {
      ComplexScalar.of(RealScalar.ONE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ComplexScalar.of(null, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ComplexScalar.of(null, RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testPolarFail() {
    try {
      ComplexScalar.fromPolar(RealScalar.ONE, ComplexScalar.I);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ComplexScalar.fromPolar(ComplexScalar.I, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ComplexScalar.fromPolar(ComplexScalar.I, ComplexScalar.I);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testPolarQuantityFail() {
    try {
      ComplexScalar.fromPolar(RealScalar.ONE, Quantity.of(1.3, "m"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testPolar() {
    assertTrue(ComplexScalar.fromPolar(1, 3) instanceof ComplexScalar);
    assertTrue(ComplexScalar.fromPolar(1, 0) instanceof RealScalar);
  }

  public void testPolarNumberFail() {
    try {
      ComplexScalar.fromPolar(-1, 3);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testUnitFail() {
    try {
      ComplexScalar.unit(ComplexScalar.of(-1, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ComplexScalar.unit(Quantity.of(3, "s"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
