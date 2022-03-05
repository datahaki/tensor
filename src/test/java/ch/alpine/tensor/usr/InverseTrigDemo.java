// code by jph
package ch.alpine.tensor.usr;

import java.util.stream.Stream;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.tri.ArcCosh;
import ch.alpine.tensor.sca.tri.ArcSinh;
import ch.alpine.tensor.sca.tri.ArcTanh;

/** inspired by Mathematica's documentation of DensityPlot */
/* package */ class InverseTrigDemo implements BivariateEvaluation {
  private static final int EXPONENT = 3;
  // ---
  private final ScalarUnaryOperator[] scalarUnaryOperators;

  public InverseTrigDemo(ScalarUnaryOperator... scalarUnaryOperator) {
    this.scalarUnaryOperators = scalarUnaryOperator;
  }

  @Override
  public Scalar apply(Scalar re, Scalar im) {
    Scalar seed = Power.of(ComplexScalar.of(re, im), EXPONENT);
    return Stream.of(scalarUnaryOperators) //
        .map(scalarUnaryOperator -> scalarUnaryOperator.apply(seed)) //
        .map(Imag.FUNCTION) //
        .reduce(Scalar::add) //
        .get();
  }

  @Override
  public Clip clipX() {
    return Clips.absolute(2.0);
  }

  @Override
  public Clip clipY() {
    return Clips.absolute(2.0);
  }

  public static void main(String[] args) throws Exception {
    StaticHelper.export( //
        new InverseTrigDemo(ArcSinh.FUNCTION, ArcCosh.FUNCTION, ArcTanh.FUNCTION), //
        ArcCosh.class, ColorDataGradients.THERMOMETER);
  }
}
