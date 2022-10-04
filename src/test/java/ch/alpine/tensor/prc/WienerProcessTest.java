// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class WienerProcessTest {
  @Test
  void test() {
    RandomProcess randomProcess = WienerProcess.of(0, 1);
    assertTrue(randomProcess.toString().startsWith("WienerProcess["));
    RandomFunction randomFunction = RandomFunction.of(randomProcess);
    RandomVariate.of(UniformDistribution.of(0, 10), 100).stream() //
        .map(Scalar.class::cast) //
        .forEach(randomFunction::eval);
  }
}
