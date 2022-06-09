// code by jph
package ch.alpine.tensor.lie;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Cosh;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinh;

class TrigonometrySeriesTest {
  @Test
  public void testTrigonometryExact() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    Quaternion sin = Quaternion.of( //
        25.987532783271178, 12.134775109731375, -8.089850073154249, 8.089850073154249); // mathematica
    Tolerance.CHOP.requireClose(Sin.FUNCTION.apply(q1), sin);
    Quaternion cos = Quaternion.of( //
        16.686402906489768, -18.898792492845683, 12.599194995230455, -12.599194995230455); // mathematica
    Tolerance.CHOP.requireClose(Cos.FUNCTION.apply(q1), cos);
    Quaternion sinh = Quaternion.of( //
        -0.6531361502064957, -0.9333911168998251, 0.6222607445998833, -0.6222607445998833);
    Tolerance.CHOP.requireClose(Sinh.FUNCTION.apply(q1), sinh);
    Quaternion cosh = Quaternion.of( //
        -0.8575908114563202, -0.710865219851931, 0.47391014656795394, -0.47391014656795394);
    Tolerance.CHOP.requireClose(Cosh.FUNCTION.apply(q1), cosh);
  }

  @Test
  public void testTrigonometryNumeric() {
    Scalar q1 = Quaternion.of(1, 3., -2, 2);
    Quaternion sin = Quaternion.of( //
        25.987532783271178, 12.134775109731375, -8.089850073154249, 8.089850073154249); // mathematica
    Tolerance.CHOP.requireClose(Sin.FUNCTION.apply(q1), sin);
    Quaternion cos = Quaternion.of( //
        16.686402906489768, -18.898792492845683, 12.599194995230455, -12.599194995230455); // mathematica
    Tolerance.CHOP.requireClose(Cos.FUNCTION.apply(q1), cos);
    Quaternion sinh = Quaternion.of( //
        -0.6531361502064957, -0.9333911168998251, 0.6222607445998833, -0.6222607445998833);
    Tolerance.CHOP.requireClose(Sinh.FUNCTION.apply(q1), sinh);
    Quaternion cosh = Quaternion.of( //
        -0.8575908114563202, -0.710865219851931, 0.47391014656795394, -0.47391014656795394);
    Tolerance.CHOP.requireClose(Cosh.FUNCTION.apply(q1), cosh);
  }
}
