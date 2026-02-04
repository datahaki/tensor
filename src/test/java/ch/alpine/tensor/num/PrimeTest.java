// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class PrimeTest {
  @Test
  void testSimple() {
    Tensor tensor = Tensor.of(IntStream.range(1, 21).parallel().mapToObj(Prime::of));
    Tensor expect = Tensors.vector(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71);
    assertEquals(tensor, expect);
  }

  @Test
  void test1003() {
    Scalar scalar = Prime.of(1003);
    assertTrue(PrimeQ.of(scalar));
    assertEquals(scalar, RealScalar.of(7937));
  }

  @Test
  void test1234() {
    Scalar scalar = Prime.of(1234);
    assertTrue(PrimeQ.of(scalar));
    assertEquals(scalar, RealScalar.of(10061));
  }

  @Test
  void testRepeated() {
    for (int c = 0; c < 10_000; ++c)
      Prime.of(1003);
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> Prime.of(0));
    assertThrows(Exception.class, () -> Prime.of(-1));
  }
}
