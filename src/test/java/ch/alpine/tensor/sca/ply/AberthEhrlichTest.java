// code by jph
package ch.alpine.tensor.sca.ply;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;

class AberthEhrlichTest {
  @Test
  void testDegree1() {
    Polynomial polynomial = Polynomial.of(Tensors.vector(4, -5, 1));
    Tensor result = AberthEhrlich.of(polynomial);
    Tolerance.CHOP.requireAllZero(result.map(polynomial));
  }

  @Test
  void testSimple() {
    Polynomial polynomial = Polynomial.of(Tensors.vector(4, -5, 0, 1));
    Tensor result = AberthEhrlich.of(polynomial);
    Tolerance.CHOP.requireAllZero(result.map(polynomial));
  }
}
