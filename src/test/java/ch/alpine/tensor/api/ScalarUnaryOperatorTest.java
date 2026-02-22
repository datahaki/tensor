// code by jph
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.stream.Stream;

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
import test.wrap.SerializableQ;

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
      Arg.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    try {
      ArcCos.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    try {
      ArcSin.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    ArcTan.FUNCTION.apply(scalar);
    try {
      ArcTanh.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    Ceiling.FUNCTION.apply(scalar);
    Tolerance.CHOP.apply(scalar);
    Chop._11.apply(scalar);
    try {
      Clips.unit().apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    Cos.FUNCTION.apply(scalar);
    Exp.FUNCTION.apply(scalar);
    Floor.FUNCTION.apply(scalar);
    try {
      Log.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    try {
      LogisticSigmoid.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    N.DOUBLE.apply(scalar);
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
      Ramp.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    Round.FUNCTION.apply(scalar);
    try {
      Sign.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    Sin.FUNCTION.apply(scalar);
    try {
      Sinc.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    Sinh.FUNCTION.apply(scalar);
    try {
      Sqrt.FUNCTION.apply(scalar);
    } catch (Exception exception) {
      // ---
    }
    Tan.FUNCTION.apply(scalar);
    Tanh.FUNCTION.apply(scalar);
    try {
      UnitStep.FUNCTION.apply(scalar);
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
    scalar.maps(s -> s.subtract(RealScalar.ONE));
    scalar.maps(RealScalar.ONE::add);
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
    ScalarUnaryOperator suo = ScalarUnaryOperatorTest.chain(Sin.FUNCTION, Tan.FUNCTION);
    Serialization.copy(suo);
    suo.apply(RealScalar.of(0.2));
  }

  @Test
  void testChainEmpty() {
    ScalarUnaryOperator suo1 = ScalarUnaryOperatorTest.chain();
    ScalarUnaryOperator suo2 = ScalarUnaryOperatorTest.chain();
    assertEquals(suo1, suo2);
    SerializableQ.require(suo1);
  }

  @Test
  void testChainFail() {
    assertThrows(Exception.class, () -> ScalarUnaryOperatorTest.chain(Sin.FUNCTION, null));
  }

  @Test
  void test() {
    ScalarUnaryOperator suo = ScalarUnaryOperatorTest.chain(RealScalar.of(3)::add, RealScalar.of(5)::multiply);
    Scalar scalar = suo.apply(RealScalar.of(2));
    assertEquals(scalar, RealScalar.of((2 + 3) * 5));
  }

  /** Remark:
   * The empty chain ScalarUnaryOperator.chain() returns
   * the instance {@link #IDENTITY}
   * 
   * @param scalarUnaryOperators
   * @return operator that nests given operators with execution from left to right
   * @throws Exception if any given operator is null */
  public static ScalarUnaryOperator chain(ScalarUnaryOperator... scalarUnaryOperators) {
    return Stream.of(scalarUnaryOperators) //
        .reduce(s -> s, ScalarUnaryOperator::andThen);
  }
}
