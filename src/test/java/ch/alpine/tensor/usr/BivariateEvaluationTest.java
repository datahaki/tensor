// code by jph
package ch.alpine.tensor.usr;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.tri.ArcCosh;
import ch.alpine.tensor.sca.tri.ArcSinh;
import ch.alpine.tensor.sca.tri.ArcTanh;

public class BivariateEvaluationTest {
  @Test
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
