// code by jph
package ch.alpine.tensor.lie.ad;

import junit.framework.TestCase;

public class NilpotentAlgebraQTest extends TestCase {
  public void testSimple() {
    assertTrue(NilpotentAlgebraQ.of(LieAlgebras.he1()));
    assertFalse(NilpotentAlgebraQ.of(LieAlgebras.so3()));
  }
}
