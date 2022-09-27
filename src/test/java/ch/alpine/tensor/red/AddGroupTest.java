// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.BinaryPower;
import ch.alpine.tensor.num.GaussScalar;

class AddGroupTest {
  @Test
  void test() {
    BinaryPower<Scalar> binaryPower = new BinaryPower<>(AdditionGroup.INSTANCE);
    for (int i = 0; i < 10; ++i) {
      Scalar raise = binaryPower.raise(GaussScalar.of(1, 19), BigInteger.valueOf(i));
      assertEquals(raise, GaussScalar.of(i, 19));
    }
    for (int i = 0; i < 10; ++i) {
      Scalar raise = binaryPower.raise(GaussScalar.of(1, 19), BigInteger.valueOf(-i));
      assertEquals(raise, GaussScalar.of(-i, 19));
    }
  }
}
