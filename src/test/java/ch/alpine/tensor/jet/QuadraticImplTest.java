// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.red.Entrywise;

class QuadraticImplTest {
  @Test
  void test() {
    Entrywise with = Entrywise.with((a, b) -> QuadraticImpl.of(a, b, RealScalar.of(7)));
    int n = 5;
    Tensor A = with.apply( //
        RandomVariate.of(DiscreteUniformDistribution.of(-15, 15), n, n), //
        RandomVariate.of(DiscreteUniformDistribution.of(-15, 15), n, n));
    Tensor B = with.apply( //
        RandomVariate.of(DiscreteUniformDistribution.of(-15, 15), n, n), //
        RandomVariate.of(DiscreteUniformDistribution.of(-15, 15), n, n));
    A.add(B);
    Tensor dot = Inverse.of(A).dot(A);
    assertEquals(dot, IdentityMatrix.of(n));
  }
}
