// code by jph
package ch.alpine.tensor.mat.ev;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/* package */ enum TestHelper {
  ;
  public static void checkEquation(Tensor matrix, Eigensystem eigensystem) {
    checkEquation(matrix, eigensystem, Tolerance.CHOP);
  }

  public static void checkEquation(Tensor matrix, Eigensystem eigensystem, Chop chop) {
    assertDoesNotThrow(() -> Serialization.copy(eigensystem));
    Tensor v = Transpose.of(eigensystem.vectors());
    Tensor d = eigensystem.diagonalMatrix();
    Tensor lhs = Dot.of(matrix, v);
    Tensor rhs = Dot.of(v, d);
    chop.requireClose(lhs, rhs);
    assertTrue(eigensystem.toString().startsWith("Eigensystem["));
  }
}
