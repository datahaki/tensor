// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import junit.framework.TestCase;

public class Givens2Test extends TestCase {
  public void testProduct() {
    Scalar theta1 = RealScalar.of(0.2);
    Scalar theta2 = RealScalar.of(0.3);
    int n = 4;
    int p = 1;
    int q = 3;
    Givens2 givens2 = new Givens2(n, theta1, theta2, p, q);
    Tensor matrix = givens2.matrix();
    Tolerance.CHOP.requireClose(Inverse.of(matrix), ConjugateTranspose.of(matrix));
    Tolerance.CHOP.requireClose( //
        Dot.of( //
            new Givens1(n, theta2, p, q).matrix(), //
            new Givens1(n, theta1, p, q).matrix()), //
        matrix);
    Tolerance.CHOP.requireClose(Inverse.of(matrix), givens2.inverse());
  }
}
