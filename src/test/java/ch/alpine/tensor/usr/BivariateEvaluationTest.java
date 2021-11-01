// code by jph
package ch.alpine.tensor.usr;

import java.util.Arrays;
import java.util.List;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.ArcCosh;
import ch.alpine.tensor.sca.ArcSinh;
import ch.alpine.tensor.sca.ArcTanh;
import junit.framework.TestCase;

public class BivariateEvaluationTest extends TestCase {
  public void testSimple() {
    List<BivariateEvaluation> list = Arrays.asList( //
        BetaDemo.INSTANCE, //
        GammaDemo.INSTANCE, //
        GaussScalarDemo.INSTANCE, //
        new InverseTrigDemo(ArcSinh.FUNCTION, ArcCosh.FUNCTION, ArcTanh.FUNCTION), //
        new JuliaSinDemo(ComplexScalar.of(1.1, 0.5)), //
        MandelbrotDemo.INSTANCE, //
        new NewtonDemo(Tensors.vector(1, 5, 0, 1)), //
        SinDemo.INSTANCE, //
        WeierstrassDemo.INSTANCE);
    for (BivariateEvaluation bivariateEvaluation : list)
      StaticHelper.image(bivariateEvaluation, 10);
  }
}
