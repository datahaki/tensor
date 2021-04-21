// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.Objective;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class KleeMintyCubeTest extends TestCase {
  private static void _callKlee(int n) {
    KleeMintyCube kleeMintyCube = new KleeMintyCube(n);
    Tensor x = LinearProgramming.of(kleeMintyCube.linearProgram);
    assertEquals(x, kleeMintyCube.x);
  }

  // numeric test
  private static void _callKleeN(int n) {
    KleeMintyCube kleeMintyCube = new KleeMintyCube(n);
    LinearProgram linearProgram = LinearProgram.of( //
        Objective.MAX, //
        N.DOUBLE.of(kleeMintyCube.linearProgram.c), //
        ConstraintType.LESS_EQUALS, //
        N.DOUBLE.of(kleeMintyCube.linearProgram.A), //
        N.DOUBLE.of(kleeMintyCube.linearProgram.b), //
        RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    assertEquals(x, kleeMintyCube.x);
  }

  public void testKleeMinty() {
    for (int n = 1; n <= 5; ++n) {
      _callKlee(n);
      _callKleeN(n);
    }
  }
}
