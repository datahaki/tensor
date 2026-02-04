// code by jph
package ch.alpine.tensor.sca.var;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;

class MultiquadricVariogramTest {
  @Test
  void testQuantity() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(MultiquadricVariogram.of(Quantity.of(1, "m")));
    Scalar scalar = scalarUnaryOperator.apply(Quantity.of(0.2, "m"));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("1.019803902718557[m]"));
  }

  @Test
  void testQuantityZero() {
    ScalarUnaryOperator scalarUnaryOperator = MultiquadricVariogram.of(Quantity.of(0, "m"));
    Scalar scalar = scalarUnaryOperator.apply(Quantity.of(0.2, "m"));
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("0.2[m]"));
  }

  @Test
  void testFailNonPositive() {
    assertThrows(Exception.class, () -> MultiquadricVariogram.of(RealScalar.of(-1)));
  }
}
