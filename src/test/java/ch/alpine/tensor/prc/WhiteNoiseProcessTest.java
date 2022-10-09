// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Serialization;

class WhiteNoiseProcessTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    RandomFunction randomFunction = RandomFunction.of(Serialization.copy(WhiteNoiseProcess.instance()));
    assertEquals(randomFunction.path(), Tensors.empty());
    Scalar t = RealScalar.of(10);
    assertEquals(randomFunction.evaluate(t), randomFunction.evaluate(t));
    assertEquals(Dimensions.of(randomFunction.path()), List.of(11, 2));
    Serialization.copy(randomFunction);
  }
}
