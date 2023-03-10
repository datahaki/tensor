// code by jph
package ch.alpine.tensor.sca.gam;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Chop;

class GompertzFunctionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException { // example taken from wikipedia
    ScalarUnaryOperator suo = GompertzFunction.of(4378449, 15.42677, 0.384124);
    Scalar result = Serialization.copy(suo).apply(RealScalar.of(10));
    Chop._06.requireClose(result, RealScalar.of(3144102.9160426045));
    assertTrue(suo.toString().startsWith("GompertzFunction["));
  }
}
