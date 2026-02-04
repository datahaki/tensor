// code by jph
package ch.alpine.tensor.sca.var;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

class VariogramFunctionsTest {
  @Test
  void testSimple() {
    for (VariogramFunctions variograms : VariogramFunctions.values()) {
      ScalarUnaryOperator suo = variograms.of(RealScalar.ONE);
      suo.toString();
    }
  }
}
