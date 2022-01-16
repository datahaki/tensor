// code by jph
package ch.alpine.tensor.lie.ad;

import junit.framework.TestCase;

public class NilpotentAlgebraQTest extends TestCase {
  public void testSimple() {
    assertTrue(NilpotentAlgebraQ.of(TestHelper.he1()));
    assertFalse(NilpotentAlgebraQ.of(TestHelper.so3()));
    assertFalse(NilpotentAlgebraQ.of(TestHelper.sl2()));
    assertFalse(NilpotentAlgebraQ.of(TestHelper.se2()));
  }
}
