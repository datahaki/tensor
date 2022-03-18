// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
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

public class ScalarUnaryOperatorsTest {
  public static final ScalarUnaryOperator[] ARRAY = { //
      Abs.FUNCTION, //
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
      Imag.FUNCTION, //
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
      Real.FUNCTION, //
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

  @Test
  public void testFailNull() {
    for (ScalarUnaryOperator scalarUnaryOperator : ARRAY) {
      try {
        scalarUnaryOperator.apply(null);
        System.out.println(scalarUnaryOperator.getClass());
        fail();
      } catch (Exception exception) {
        // ---
      }
    }
  }
}
