// code by jph
package ch.alpine.tensor;

import java.util.List;

import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import junit.framework.TestCase;

public class ScalarMultiplyTest extends TestCase {
  private static void _check(Scalar a, Scalar b) {
    {
      Scalar ab = a.multiply(b);
      Scalar ba = b.multiply(a);
      assertEquals(ab, ba);
      assertEquals(ab.toString(), ba.toString());
      assertEquals(ab.getClass(), ba.getClass());
    }
  }

  public void testSimple() {
    Unit ua = Unit.of("m^2*s^-3");
    Unit ub = Unit.of("kg*CHF^-1");
    List<Scalar> list = TestHelper.SCALARS;
    for (int i = 0; i < list.size(); ++i)
      for (int j = i; j < list.size(); ++j) {
        Scalar a = list.get(i);
        Scalar b = list.get(j);
        _check(a, b);
        _check(Quantity.of(a, ua), Quantity.of(b, ua));
        _check(Quantity.of(a, ua), Quantity.of(b, ub));
        _check(Quantity.of(a, ub), Quantity.of(b, ua));
        _check(Quantity.of(a, ub), Quantity.of(b, ub));
      }
  }
}
