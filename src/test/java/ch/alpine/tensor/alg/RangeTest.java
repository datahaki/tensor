// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;

public class RangeTest {
  @Test
  public void testRange() {
    Tensor t = Range.of(Integer.MAX_VALUE, Integer.MAX_VALUE + 4L);
    Tensor r = Tensors.fromString("{2147483647, 2147483648, 2147483649, 2147483650}");
    assertEquals(t, r);
    assertEquals(t.toString(), r.toString());
  }

  @Test
  public void testRange2() {
    Tensor t = Range.of(2, 7);
    Tensor r = Tensors.vector(2, 3, 4, 5, 6);
    assertEquals(t, r);
    assertEquals(t.toString(), r.toString());
  }

  @Test
  public void testRangeEmpty() {
    assertEquals(Range.of(6, 6), Tensors.empty());
    assertEquals(Range.of(6, 5), Tensors.empty());
  }

  @Test
  public void testBigInteger() {
    assertEquals(Range.of(new BigInteger("123"), new BigInteger("126")), Range.of(123, 126));
  }

  @Test
  public void testBigIntegerEmpty() {
    assertEquals(Range.of(new BigInteger("123"), new BigInteger("123")), Tensors.empty());
    assertEquals(Range.of(new BigInteger("123"), new BigInteger("122")), Tensors.empty());
    assertEquals(Range.of(new BigInteger("123"), new BigInteger("121")), Tensors.empty());
  }

  @Test
  public void testBigIntegerNullFail() {
    AssertFail.of(() -> Range.of(new BigInteger("123"), null));
    AssertFail.of(() -> Range.of(null, new BigInteger("123")));
  }
}
