// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ArgMin;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Abs;

class RootsBoundsTest {
  @RepeatedTest(10)
  void testQuadratic() {
    Distribution distribution = UniformDistribution.of(-10, +10);
    Tensor coeffs = RandomVariate.of(distribution, 3);
    Tensor roots = Roots.of(coeffs);
    Scalar max = roots.map(Abs.FUNCTION).stream().map(Scalar.class::cast).reduce(Max::of).orElseThrow();
    for (RootsBounds rootsBounds : RootsBounds.values()) {
      Scalar bound = rootsBounds.of(coeffs);
      boolean check = Scalars.lessEquals(max, bound);
      if (!check) {
        System.err.println(rootsBounds + " " + max + " " + bound + "  " + coeffs);
      }
      assertTrue(check);
    }
  }

  @RepeatedTest(10)
  void testCubic() {
    Distribution distribution = UniformDistribution.of(-10, +10);
    Tensor coeffs = RandomVariate.of(distribution, 4);
    Tensor roots = Roots.of(coeffs);
    Scalar max = roots.map(Abs.FUNCTION).stream().map(Scalar.class::cast).reduce(Max::of).orElseThrow();
    for (RootsBounds rootsBounds : RootsBounds.values()) {
      Scalar bound = rootsBounds.of(coeffs);
      boolean check = Scalars.lessEquals(max, bound);
      if (!check) {
        System.err.println(rootsBounds + " " + max + " " + bound + "  " + coeffs);
      }
      assertTrue(check);
    }
  }

  @RepeatedTest(100)
  @Disabled
  void testWinner() {
    Distribution distribution = UniformDistribution.of(-10, +10);
    Tensor coeffs = RandomVariate.of(distribution, 10);
    int index = ArgMin.of(Tensor.of(Stream.of(RootsBounds.values()).map(s -> s.of(coeffs))));
    System.out.println(RootsBounds.values()[index]);
  }
}
