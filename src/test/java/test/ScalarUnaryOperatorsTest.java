// code by jph
package test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Gudermannian;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.InvertUnlessZero;
import ch.alpine.tensor.sca.Mod;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Ramp;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.UnitStep;
import ch.alpine.tensor.sca.Unitize;
import ch.alpine.tensor.sca.erf.Fresnel;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.exp.Log10;
import ch.alpine.tensor.sca.exp.LogisticSigmoid;
import ch.alpine.tensor.sca.gam.Gamma;
import ch.alpine.tensor.sca.gam.LogGamma;
import ch.alpine.tensor.sca.pow.CubeRoot;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.pow.Surd;
import ch.alpine.tensor.sca.tri.ArcCos;
import ch.alpine.tensor.sca.tri.ArcCosh;
import ch.alpine.tensor.sca.tri.ArcSin;
import ch.alpine.tensor.sca.tri.ArcSinh;
import ch.alpine.tensor.sca.tri.ArcTan;
import ch.alpine.tensor.sca.tri.ArcTanh;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Cosh;
import ch.alpine.tensor.sca.tri.Cot;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinc;
import ch.alpine.tensor.sca.tri.Sinh;
import ch.alpine.tensor.sca.tri.Tan;
import ch.alpine.tensor.sca.tri.Tanh;

class ScalarUnaryOperatorsTest {
  private static ScalarUnaryOperator[] suos() {
    return new ScalarUnaryOperator[] { //
        Abs.FUNCTION, //
        AbsSquared.FUNCTION, //
        ArcCos.FUNCTION, //
        ArcCosh.FUNCTION, //
        ArcSin.FUNCTION, //
        ArcSinh.FUNCTION, //
        ArcTan.FUNCTION, //
        ArcTanh.FUNCTION, //
        Arg.FUNCTION, //
        Ceiling.FUNCTION, //
        Chop.NONE, //
        Chop._01, //
        Clips.unit(), //
        Conjugate.FUNCTION, //
        Cos.FUNCTION, //
        Cosh.FUNCTION, //
        Cot.FUNCTION, //
        CubeRoot.FUNCTION, //
        s -> s.subtract(RealScalar.ONE), //
        Exp.FUNCTION, //
        Floor.FUNCTION, //
        Fresnel.FUNCTION, //
        Gamma.FUNCTION, //
        Gudermannian.FUNCTION, //
        Im.FUNCTION, //
        RealScalar.ONE::add, //
        InvertUnlessZero.FUNCTION, //
        Log.FUNCTION, //
        Log10.FUNCTION, //
        LogGamma.FUNCTION, //
        LogisticSigmoid.FUNCTION, //
        Mod.function(3), //
        N.DOUBLE, //
        N.DECIMAL32, //
        N.DECIMAL64, //
        Power.function(3), //
        Ramp.FUNCTION, //
        Re.FUNCTION, //
        Round.FUNCTION, //
        Sign.FUNCTION, //
        Sin.FUNCTION, //
        Sinc.FUNCTION, //
        Sinh.FUNCTION, //
        Sqrt.FUNCTION, //
        Surd.of(3), //
        Tan.FUNCTION, //
        Tanh.FUNCTION, //
        Unitize.FUNCTION, //
        UnitStep.FUNCTION //
    };
  }

  @ParameterizedTest
  @MethodSource("suos")
  void testFailNull(ScalarUnaryOperator scalarUnaryOperator) {
    assertThrows(Exception.class, () -> scalarUnaryOperator.apply(null));
  }
}
