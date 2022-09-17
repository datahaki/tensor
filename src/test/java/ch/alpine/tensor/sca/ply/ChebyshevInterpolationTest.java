// code by jph
package ch.alpine.tensor.sca.ply;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.exp.Exp;

class ChebyshevInterpolationTest {
  @Test
  void test() {
    ScalarUnaryOperator suo = Exp.FUNCTION;
    ScalarUnaryOperator apx1 = ChebyshevInterpolation.of(suo, ChebyshevNodes._1, 8);
    ScalarUnaryOperator apx2 = ChebyshevInterpolation.alt(suo, ChebyshevNodes._1, 8);
    Tensor domain = Subdivide.of(-0.99, 0.99, 10);
    Tensor p1 = domain.map(apx1);
    Tensor p2 = domain.map(apx2);
    System.out.println(p1.subtract(p2));
  }
}
