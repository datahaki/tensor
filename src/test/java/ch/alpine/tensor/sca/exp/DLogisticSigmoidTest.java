// code by jph
package ch.alpine.tensor.sca.exp;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.jet.JetScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class DLogisticSigmoidTest {
  @Test
  void test() {
    Scalar t = RandomVariate.of(NormalDistribution.standard());
    JetScalar jetScalar = JetScalar.of(t, 2);
    Scalar f0 = LogisticSigmoid.FUNCTION.apply(jetScalar);
    Scalar d0 = DLogisticSigmoid.FUNCTION.apply(t);
    JetScalar js = (JetScalar) f0;
    Tolerance.CHOP.requireClose(js.vector().Get(1), d0);
  }

  @Test
  void testNested() {
    Scalar t = RandomVariate.of(NormalDistribution.standard());
    JetScalar jetScalar = JetScalar.of(t, 2);
    Scalar f0 = LogisticSigmoid.FUNCTION.apply(jetScalar);
    Scalar d0 = DLogisticSigmoid.NESTED.apply(f0);
    Scalar d1 = DLogisticSigmoid.FUNCTION.apply(t);
    JetScalar df = (JetScalar) d0;
    Tolerance.CHOP.requireClose(df.vector().Get(0), d1);
  }
}
