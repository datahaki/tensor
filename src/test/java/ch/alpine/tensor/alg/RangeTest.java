// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.Clips;

class RangeTest {
  @Test
  void testRange() {
    Tensor t = Range.of(Integer.MAX_VALUE, Integer.MAX_VALUE + 4L);
    Tensor r = Tensors.fromString("{2147483647, 2147483648, 2147483649, 2147483650}");
    assertEquals(t, r);
    assertEquals(t.toString(), r.toString());
  }

  @Test
  void testRange2() {
    Tensor t = Range.of(2, 7);
    Tensor r = Tensors.vector(2, 3, 4, 5, 6);
    assertEquals(t, r);
    assertEquals(t.toString(), r.toString());
  }

  @Test
  void testGauss() {
    Tensor tensor = Range.of(GaussScalar.of(1, 17), 4);
    assertEquals(tensor.length(), 4);
  }

  @Test
  void testRangeEmpty() {
    assertEquals(Range.of(6, 6), Tensors.empty());
    assertEquals(Range.of(6, 5), Tensors.empty());
  }

  @Test
  void testBigInteger() {
    assertEquals(Range.of(new BigInteger("123"), new BigInteger("126")), Range.of(123, 126));
  }

  @Test
  void testBigIntegerEmpty() {
    assertEquals(Range.of(new BigInteger("123"), new BigInteger("123")), Tensors.empty());
    assertEquals(Range.of(new BigInteger("123"), new BigInteger("122")), Tensors.empty());
    assertEquals(Range.of(new BigInteger("123"), new BigInteger("121")), Tensors.empty());
  }

  @Test
  void testBigIntegerNullFail() {
    assertThrows(NullPointerException.class, () -> Range.of(new BigInteger("123"), null));
    assertThrows(NullPointerException.class, () -> Range.of(null, new BigInteger("123")));
  }

  @Test
  void testClip() {
    assertEquals(Range.closed(Clips.unit()), Tensors.vector(0, 1));
    assertEquals(Range.closed(Clips.interval(3, 3)), Tensors.vector(3));
  }

  @Test
  void testClipFail() {
    assertThrows(Throw.class, () -> Range.closed(Clips.interval(3, 3.4)));
    assertThrows(Throw.class, () -> Range.closed(Clips.interval(3.4, 4)));
  }
}
