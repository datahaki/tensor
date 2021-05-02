// code by jph
package ch.alpine.tensor.usr;

import java.util.stream.Stream;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.sca.ArcCosh;
import ch.alpine.tensor.sca.ArcSinh;
import ch.alpine.tensor.sca.ArcTanh;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Power;

/** inspired by Mathematica's documentation of DensityPlot */
/* package */ class InverseTrigDemo extends BivariateEvaluation {
  private static final int EXPONENT = 3;
  // ---
  private final ScalarUnaryOperator[] scalarUnaryOperators;

  InverseTrigDemo(ScalarUnaryOperator... scalarUnaryOperator) {
    super(Clips.absolute(2.0), Clips.absolute(2.0));
    this.scalarUnaryOperators = scalarUnaryOperator;
  }

  @Override
  protected Scalar function(Scalar re, Scalar im) {
    Scalar seed = Power.of(ComplexScalar.of(re, im), EXPONENT);
    return Stream.of(scalarUnaryOperators) //
        .map(scalarUnaryOperator -> scalarUnaryOperator.apply(seed)) //
        .map(Imag.FUNCTION) //
        .reduce(Scalar::add) //
        .get();
  }

  public static void main(String[] args) throws Exception {
    StaticHelper.export( //
        new InverseTrigDemo(ArcSinh.FUNCTION, ArcCosh.FUNCTION, ArcTanh.FUNCTION), //
        ArcCosh.class, ColorDataGradients.THERMOMETER);
  }
}
