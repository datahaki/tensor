// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class BSplineBasisTest {
  @RepeatedTest(4)
  void test(RepetitionInfo repetitionInfo) {
    int deg = repetitionInfo.getCurrentRepetition() - 1;
    ScalarUnaryOperator suo = BSplineBasis.of(deg);
    assertEquals(suo.toString(), "BSplineBasis[" + deg + "]");
    BSplineBasisOld old = BSplineBasisOld.of[deg];
    Tensor domain = RandomVariate.of(UniformDistribution.of(-4, 4), 10);
    Tensor s1 = domain.map(suo);
    Tensor s2 = domain.map(s -> RealScalar.of(old.at(s.number().doubleValue())));
    Tolerance.CHOP.requireClose(s1, s2);
  }
}
