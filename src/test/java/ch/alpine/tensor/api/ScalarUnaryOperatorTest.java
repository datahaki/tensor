// code by jph
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

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

/** the purpose of the test is to demonstrate that
 * none of the special input cases: NaN, Infty
 * result in a stack overflow error when provided to the
 * scalar unary operators */
class ScalarUnaryOperatorTest {
  @Test
  void testFunctionalInterface() {
    assertNotNull(ScalarUnaryOperator.class.getAnnotation(FunctionalInterface.class));
  }

  static void _checkOps(Scalar scalar) {
    try {
      Abs.FUNCTION.apply(scalar);
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

  @Test
  void testTrinity() {
    _checkOps(DoubleScalar.INDETERMINATE);
    _checkOps(DoubleScalar.POSITIVE_INFINITY);
    _checkOps(DoubleScalar.NEGATIVE_INFINITY);
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator scalarUnaryOperator = t -> t;
    ScalarUnaryOperator copy = Serialization.copy(scalarUnaryOperator);
    assertEquals(copy.apply(ComplexScalar.I), ComplexScalar.I);
  }

  @Test
  void testCompose() {
    ScalarUnaryOperator suo = Sin.FUNCTION.compose(Tan.FUNCTION);
    Scalar s1 = suo.apply(RealScalar.of(0.3));
    Scalar s2 = Sin.FUNCTION.apply(Tan.FUNCTION.apply(RealScalar.of(0.3)));
    assertEquals(s1, s2);
  }

  @Test
  void testComposeFail() {
    assertThrows(Exception.class, () -> Sin.FUNCTION.compose(null));
    assertThrows(Exception.class, () -> Sin.FUNCTION.andThen(null));
  }

  @Test
  void testChain() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator suo = ScalarUnaryOperator.chain(Sin.FUNCTION, Tan.FUNCTION);
    Serialization.copy(suo);
    suo.apply(RealScalar.of(0.2));
  }

  @Test
  void testChainEmpty() {
    ScalarUnaryOperator suo1 = ScalarUnaryOperator.chain();
    ScalarUnaryOperator suo2 = ScalarUnaryOperator.chain();
    assertEquals(suo1, suo2);
  }

  @Test
  void testChainFail() {
    assertThrows(Exception.class, () -> ScalarUnaryOperator.chain(Sin.FUNCTION, null));
  }

  @Test
  void test() {
    ScalarUnaryOperator suo = ScalarUnaryOperator.chain(RealScalar.of(3)::add, RealScalar.of(5)::multiply);
    Scalar scalar = suo.apply(RealScalar.of(2));
    assertEquals(scalar, RealScalar.of((2 + 3) * 5));
  }
}
