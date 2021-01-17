// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Quaternion;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class AbsInterfaceTest extends TestCase {
  public void testAbsAndSquared() {
    Tensor tensor = Tensors.of( //
        Quaternion.of(1, 2, 3, 4), //
        RationalScalar.HALF, RationalScalar.of(2, 7), RealScalar.TWO, //
        ComplexScalar.I, ComplexScalar.of(2, 3), ComplexScalar.of(2.0, 3.3), //
        RealScalar.of(-3), Pi.VALUE, Pi.HALF.negate(), //
        Quantity.of(2.3, "m*s^-3"), Quantity.of(ComplexScalar.of(2, 3), "m^2*s^-1"));
    for (Tensor _q : tensor) {
      Scalar q = (Scalar) _q;
      Scalar abs = Abs.FUNCTION.apply(q);
      Tolerance.CHOP.requireClose(AbsSquared.of(q), abs.multiply(abs));
    }
  }
}
