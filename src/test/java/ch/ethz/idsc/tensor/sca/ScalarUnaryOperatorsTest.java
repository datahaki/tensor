// code by jph
package ch.ethz.idsc.tensor.sca;

import junit.framework.TestCase;

public class ScalarUnaryOperatorsTest extends TestCase {
  public static final ScalarUnaryOperator[] ARRAY = { Abs.FUNCTION, //
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
      Decrement.ONE, //
      Exp.FUNCTION, //
      Floor.FUNCTION, //
      Fresnel.FUNCTION, //
      Gamma.FUNCTION, //
      Gudermannian.FUNCTION, //
      Imag.FUNCTION, //
      Increment.ONE, //
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
