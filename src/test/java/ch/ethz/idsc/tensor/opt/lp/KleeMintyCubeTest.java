// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class KleeMintyCubeTest extends TestCase {
  public void callKlee(int n) {
    KleeMintyCube kmc = new KleeMintyCube(n);
    Tensor x = LinearProgramming.maxLessEquals(kmc.c, kmc.m, kmc.b);
    // System.out.println("---");
    // kmc.show();
    assertEquals(x, kmc.x);
  }

  // numeric test
  public void callKleeN(int n) {
    KleeMintyCube kmc = new KleeMintyCube(n);
    Tensor x = LinearProgramming.maxLessEquals(N.DOUBLE.of(kmc.c), N.DOUBLE.of(kmc.m), N.DOUBLE.of(kmc.b));
    assertEquals(x, kmc.x);
  }

  public void testKleeMinty() {
    for (int n = 1; n <= 7; ++n) {
      callKlee(n);
      callKleeN(n);
    }
  }
}
