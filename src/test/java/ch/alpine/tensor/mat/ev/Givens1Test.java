// code by jph
package ch.alpine.tensor.mat.ev;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;

class Givens1Test {
  @Test
  public void testSimple() {
    Givens1 givens = new Givens1(4, RealScalar.of(0.3), 1, 2);
    Tensor matrix = givens.matrix();
    // System.out.println(Pretty.of(matrix.map(Round._2)));
    Tensor invers = Inverse.of(matrix);
    Tolerance.CHOP.requireClose(invers, ConjugateTranspose.of(matrix));
    // Tolerance.CHOP.requireClose(invers, new Givens1(4, RealScalar.of(-0.3), 1, 2).matrix());
  }
}
