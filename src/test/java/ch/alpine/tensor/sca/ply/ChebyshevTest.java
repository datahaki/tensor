// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensors;

class ChebyshevTest {
  @Test
  void testFirst() {
    assertEquals(Chebyshev.T.of(0).coeffs(), Tensors.vector(1));
    assertEquals(Chebyshev.T.of(1).coeffs(), Tensors.vector(0, 1));
    assertEquals(Chebyshev.T.of(2).coeffs(), Tensors.vector(-1, 0, 2));
    assertEquals(Chebyshev.T.of(3).coeffs(), Tensors.vector(0, -3, 0, 4));
    assertEquals(Chebyshev.T.of(4).coeffs(), Tensors.vector(1, 0, -8, 0, 8));
    assertEquals(Chebyshev.T.of(6).coeffs(), Tensors.vector(-1, 0, 18, 0, -48, 0, 32));
  }

  @Test
  void testUFirst() {
    assertEquals(Chebyshev.U.of(0).coeffs(), Tensors.vector(1));
    assertEquals(Chebyshev.U.of(1).coeffs(), Tensors.vector(0, 2));
    assertEquals(Chebyshev.U.of(2).coeffs(), Tensors.vector(-1, 0, 4));
    assertEquals(Chebyshev.U.of(3).coeffs(), Tensors.vector(0, -4, 0, 8));
    assertEquals(Chebyshev.U.of(4).coeffs(), Tensors.vector(1, 0, -12, 0, 16));
    assertEquals(Chebyshev.U.of(6).coeffs(), Tensors.vector(-1, 0, 24, 0, -80, 0, 64));
  }

  @Test
  void testProduct() {
    int n = 4;
    int m = 3;
    Polynomial p1 = Chebyshev.T.of(n).times(Chebyshev.T.of(m));
    Polynomial p2 = Chebyshev.T.of(n + m).plus(Chebyshev.T.of(Math.abs(m - n))).times(RationalScalar.HALF);
    assertEquals(p1, p2);
  }

  @ParameterizedTest
  @EnumSource
  void testFail(Chebyshev chebyshev) {
    assertThrows(Exception.class, () -> chebyshev.of(-1));
  }
}
