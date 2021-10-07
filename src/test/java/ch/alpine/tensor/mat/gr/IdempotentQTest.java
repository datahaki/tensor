// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class IdempotentQTest extends TestCase {
  public void testSimple() {
    assertFalse(IdempotentQ.of(HilbertMatrix.of(2, 3)));
  }
}
