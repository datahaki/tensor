// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensors;

class ChebyshevTTest {
  @Test
  void testFirst() {
    assertEquals(ChebyshevT.of(0).coeffs(), Tensors.vector(1));
    assertEquals(ChebyshevT.of(1).coeffs(), Tensors.vector(0, 1));
    assertEquals(ChebyshevT.of(2).coeffs(), Tensors.vector(-1, 0, 2));
    assertEquals(ChebyshevT.of(3).coeffs(), Tensors.vector(0, -3, 0, 4));
    assertEquals(ChebyshevT.of(4).coeffs(), Tensors.vector(1, 0, -8, 0, 8));
    assertEquals(ChebyshevT.of(6).coeffs(), Tensors.vector(-1, 0, 18, 0, -48, 0, 32));
  }

  @Test
  void testProduct() {
    int n = 4;
    int m = 3;
    Polynomial p1 = ChebyshevT.of(n).times(ChebyshevT.of(m));
    Polynomial p2 = ChebyshevT.of(n + m).plus(ChebyshevT.of(Math.abs(m - n))).times(RationalScalar.HALF);
    assertEquals(p1, p2);
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> ChebyshevT.of(-1));
  }
}
