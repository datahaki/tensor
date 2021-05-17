// code by jph
package ch.alpine.tensor.itp;

import java.io.IOException;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class MitchellNetravaliKernelTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(MitchellNetravaliKernel.of( //
        RationalScalar.of(1, 5), //
        RationalScalar.of(1, 3)));
    Scalar s01 = scalarUnaryOperator.apply(RationalScalar.of(1, 4));
    assertEquals(s01, RationalScalar.of(1561, 1920));
    Scalar s12 = scalarUnaryOperator.apply(RationalScalar.of(5, 4));
    assertEquals(s12, RationalScalar.of(-21, 640));
  }
}
