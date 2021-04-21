// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.CostType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class KleeMintyCubeTest extends TestCase {
  public void callKlee(int n) {
    KleeMintyCube kmc = new KleeMintyCube(n);
    Tensor x = LinearProgramming.of(kmc.linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, kmc.x);
  }

  // numeric test
  public void callKleeN(int n) {
    KleeMintyCube kmc = new KleeMintyCube(n);
    LinearProgram linearProgram = LinearProgram.of(CostType.MAX, N.DOUBLE.of(kmc.linearProgram.c), //
        ConstraintType.LESS_EQUALS, //
        N.DOUBLE.of(kmc.linearProgram.A), //
        N.DOUBLE.of(kmc.linearProgram.b), //
        RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, kmc.x);
  }

  public void testKleeMinty() {
    for (int n = 1; n <= 5; ++n) {
      callKlee(n);
      callKleeN(n);
    }
  }
}
