// code by jph
package ch.ethz.idsc.tensor.api;

import java.io.IOException;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.ArcCos;
import ch.ethz.idsc.tensor.sca.ArcSin;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.ArcTanh;
import ch.ethz.idsc.tensor.sca.Arg;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Decrement;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.LogisticSigmoid;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Sinc;
import ch.ethz.idsc.tensor.sca.Sinh;
import ch.ethz.idsc.tensor.sca.Sqrt;
import ch.ethz.idsc.tensor.sca.Tan;
import ch.ethz.idsc.tensor.sca.Tanh;
import ch.ethz.idsc.tensor.sca.UnitStep;
import junit.framework.TestCase;

/** the purpose of the test is to demonstrate that
 * none of the special input cases: NaN, Infty
 * result in a stack overflow error when provided to the
 * scalar unary operators */
public class ScalarUnaryOperatorTest extends TestCase {
  public void testFunctionalInterface() {
    assertNotNull(ScalarUnaryOperator.class.getAnnotation(FunctionalInterface.class));
  }

  static void _checkOps(Scalar scalar) {
    try {
      Abs.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    try {
      Arg.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    try {
      ArcCos.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    try {
      ArcSin.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    ArcTan.of(scalar);
    try {
      ArcTanh.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    Ceiling.of(scalar);
    Tolerance.CHOP.of(scalar);
    Chop._11.of(scalar);
    try {
      Clips.unit().apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    Cos.of(scalar);
    Exp.of(scalar);
    Floor.of(scalar);
    try {
      Log.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    LogisticSigmoid.of(scalar);
    N.DOUBLE.of(scalar);
    try {
      Power.of(scalar, RealScalar.of(0.3));
    } catch (Exception exception) {
      // ---
    }
    try {
      Power.of(scalar, RealScalar.of(-0.3));
    } catch (Exception exception) {
      // ---
    }
    Power.of(scalar, RealScalar.ZERO);
    try {
      Ramp.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    Round.of(scalar);
    try {
      Sign.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    Sin.of(scalar);
    try {
      Sinc.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    Sinh.of(scalar);
    try {
      Sqrt.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    Tan.of(scalar);
    Tanh.of(scalar);
    try {
      UnitStep.of(scalar);
    } catch (Exception exception) {
      // ---
    }
    // ---
    try {
      Scalars.compare(scalar, RealScalar.ONE);
    } catch (Exception exception) {
      // ---
    }
    // ---
    scalar.map(Decrement.ONE);
    scalar.map(RealScalar.ONE::add);
  }

  public void testTrinity() {
    _checkOps(DoubleScalar.INDETERMINATE);
    _checkOps(DoubleScalar.POSITIVE_INFINITY);
    _checkOps(DoubleScalar.NEGATIVE_INFINITY);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator scalarUnaryOperator = t -> t;
    ScalarUnaryOperator copy = Serialization.copy(scalarUnaryOperator);
    assertEquals(copy.apply(ComplexScalar.I), ComplexScalar.I);
  }
}
