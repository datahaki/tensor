// code by jph
package ch.alpine.tensor.api;

import java.io.IOException;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Ramp;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.UnitStep;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.exp.LogisticSigmoid;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.ArcCos;
import ch.alpine.tensor.sca.tri.ArcSin;
import ch.alpine.tensor.sca.tri.ArcTan;
import ch.alpine.tensor.sca.tri.ArcTanh;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinc;
import ch.alpine.tensor.sca.tri.Sinh;
import ch.alpine.tensor.sca.tri.Tan;
import ch.alpine.tensor.sca.tri.Tanh;
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
    scalar.map(s -> s.subtract(RealScalar.ONE));
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
