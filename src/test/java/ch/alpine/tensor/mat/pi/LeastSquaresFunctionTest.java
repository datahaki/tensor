// code by jph
package ch.alpine.tensor.mat.pi;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class LeastSquaresFunctionTest {
  @Test
  void test() {
    LeastSquaresFunction.of(RandomVariate.of(NormalDistribution.standard(), 4, 3));
  }
}
