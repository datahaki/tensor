// code by jph
package ch.ethz.idsc.tensor.num;

import java.math.BigInteger;

import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BinaryPowerTest extends TestCase {
  public void testInteger() {
    GroupInterface<Integer> groupInterface = new GroupInterface<Integer>() {
      @Override // from BinaryPower
      public Integer identity() {
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
    assertEquals(binaryPower.raise(5, BigInteger.valueOf(0)), (Integer) 1);
    assertEquals(binaryPower.raise(5, BigInteger.valueOf(1)), (Integer) 5);
    assertEquals(binaryPower.raise(5, BigInteger.valueOf(2)), (Integer) 25);
    assertEquals(binaryPower.raise(5, BigInteger.valueOf(3)), (Integer) 125);
    assertEquals(binaryPower.raise(5, BigInteger.valueOf(4)), (Integer) 625);
    assertEquals(binaryPower.raise(5, BigInteger.valueOf(5)), (Integer) 3125);
    assertEquals(binaryPower.raise(1, BigInteger.valueOf(-3)), (Integer) 1);
    String string = binaryPower.toString();
    assertTrue(string.startsWith("BinaryPower"));
  }

  public void testNullFail() {
    AssertFail.of(() -> new BinaryPower<>(null));
  }
}
