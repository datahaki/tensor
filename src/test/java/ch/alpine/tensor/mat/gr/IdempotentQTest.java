// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class IdempotentQTest extends TestCase {
  public void testSimple() {
    Tensor matrix = Tensors.of(UnitVector.of(2, 1), UnitVector.of(2, 1));
    assertTrue(IdempotentQ.of(matrix));
    assertFalse(IdempotentQ.of(HilbertMatrix.of(2, 3)));
  }

  public void testNullFail() {
    AssertFail.of(() -> IdempotentQ.of(null));
  }
}
