// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import junit.framework.TestCase;

public class ScalarAddTest extends TestCase {
  private static void _check(Scalar a, Scalar b) {
    {
      Scalar ab = a.add(b);
      Scalar ba = b.add(a);
      assertEquals(ab, ba);
      assertEquals(ab.toString(), ba.toString());
      assertEquals(ab.getClass(), ba.getClass());
    }
    {
      Scalar ab = a.subtract(b);
      Scalar ba = b.subtract(a).negate();
      assertEquals(ab, ba);
      if (Scalars.nonZero(ab))
        assertEquals(ab.toString(), ba.toString());
      assertEquals(ab.getClass(), ba.getClass());
    }
  }

  public void testSimple() {
    Unit unit = Unit.of("m^2*s^-3");
    for (int i = 0; i < TestHelper.SCALARS.size(); ++i)
      for (int j = i; j < TestHelper.SCALARS.size(); ++j) {
        Scalar a = TestHelper.SCALARS.get(i);
        Scalar b = TestHelper.SCALARS.get(j);
        _check(a, b);
        _check(Quantity.of(a, unit), Quantity.of(b, unit));
      }
  }
}
