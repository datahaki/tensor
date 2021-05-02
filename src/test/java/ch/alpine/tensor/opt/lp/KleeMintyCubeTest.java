// code by jph
package ch.alpine.tensor.opt.lp;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.alpine.tensor.opt.lp.LinearProgram.Objective;
import ch.alpine.tensor.opt.lp.LinearProgram.Variables;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class KleeMintyCubeTest extends TestCase {
  private static void _callKlee(int n) {
    KleeMintyCube kleeMintyCube = KleeMintyCube.of(n);
    assertTrue(kleeMintyCube.linearProgram.isCanonicDual());
    Tensor x = LinearProgramming.of(kleeMintyCube.linearProgram);
    assertEquals(x, kleeMintyCube.x);
  }

  // numeric test
  private static void _callKleeN(int n) {
    KleeMintyCube kleeMintyCube = KleeMintyCube.of(n);
    LinearProgram linearProgram = LinearProgram.of( //
        Objective.MAX, //
        N.DOUBLE.of(kleeMintyCube.linearProgram.c), //
        ConstraintType.LESS_EQUALS, //
        N.DOUBLE.of(kleeMintyCube.linearProgram.A), //
        N.DOUBLE.of(kleeMintyCube.linearProgram.b), //
        Variables.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    assertEquals(x, kleeMintyCube.x);
  }

  public void testKleeMinty() {
    for (int n = 1; n <= 5; ++n) {
      _callKlee(n);
      _callKleeN(n);
    }
  }

  public void testSmallCorners() {
    KleeMintyCube kleeMintyCube = KleeMintyCube.of(3);
    Tensor sol = SimplexCorners.of(kleeMintyCube.linearProgram);
    assertEquals(sol, Tensors.fromString("{{0, 0, 125}}"));
  }

  public void testFail() {
    AssertFail.of(() -> KleeMintyCube.of(0));
    AssertFail.of(() -> KleeMintyCube.of(-1));
  }
}
