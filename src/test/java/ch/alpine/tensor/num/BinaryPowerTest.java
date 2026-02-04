// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.api.GroupInterface;

class BinaryPowerTest {
  @Test
  void testInteger() {
    GroupInterface<Integer> groupInterface = new GroupInterface<>() {
      @Override // from BinaryPower
      public Integer neutral(Integer integer) {
        return 1;
      }

      @Override // from BinaryPower
      public Integer invert(Integer integer) {
        if (integer.equals(1))
          return integer;
        throw new RuntimeException();
      }

      @Override // from BinaryPower
      public Integer combine(Integer int1, Integer int2) {
        return Math.multiplyExact(int1, int2);
      }
    };
    BinaryPower<Integer> binaryPower = new BinaryPower<>(groupInterface);
    assertEquals(binaryPower.raise(5, 0), (Integer) 1);
    assertEquals(binaryPower.raise(5, 1), (Integer) 5);
    assertEquals(binaryPower.raise(5, 2), (Integer) 25);
    assertEquals(binaryPower.raise(5, 3), (Integer) 125);
    assertEquals(binaryPower.raise(5, 4), (Integer) 625);
    assertEquals(binaryPower.raise(5, 5), (Integer) 3125);
    assertEquals(binaryPower.raise(1, -3), (Integer) 1);
    String string = binaryPower.toString();
    assertTrue(string.startsWith("BinaryPower"));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> new BinaryPower<>(null));
  }
}
