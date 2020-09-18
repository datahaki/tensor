// code by jph
package ch.ethz.idsc.tensor.num;

import java.math.BigInteger;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PermutationBinaryPowerTest extends TestCase {
  private static void _check(Cycles arg, BigInteger exponent, Cycles expexted) {
    assertEquals(PermutationBinaryPower.of(arg, exponent), expexted);
    assertEquals(arg.power(exponent), expexted);
    assertEquals(arg.power(RealScalar.of(exponent)), expexted);
  }

  public void testSimple() {
    _check(Cycles.of(Tensors.fromString("{{4, 2, 5}, {6, 3, 1, 7}}")), BigInteger.valueOf(6), //
        Cycles.of(Tensors.fromString("{{1, 6}, {3, 7}}")));
    _check(Cycles.of(Tensors.fromString("{{4, 2, 5}, {6, 3, 1, 7}}")), BigInteger.valueOf(-2), //
        Cycles.of(Tensors.fromString("{{1, 6}, {2, 5, 4}, {3, 7}}")));
    _check(Cycles.of(Tensors.fromString("{{4, 2, 5}, {6, 3, 1, 7}}")), BigInteger.valueOf(12), //
        Cycles.identity());
  }

  public void testForloop() {
    Tensor factor = Tensors.fromString("{{5, 9}, {7, 14, 13}, {18, 4, 10, 19, 6}, {20, 1}, {}}");
    Cycles cycles = Cycles.of(factor);
    Cycles cumprd = Cycles.identity();
    for (int exp = 0; exp < 20; ++exp) {
      assertEquals(cumprd, cycles.power(BigInteger.valueOf(exp)));
      assertEquals(cumprd.inverse(), cycles.power(BigInteger.valueOf(exp).negate()));
      cumprd = cumprd.product(cycles);
    }
  }
}
