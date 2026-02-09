// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class QuotientRemainderTest {
  @ParameterizedTest
  @ValueSource(ints = { -13, -5, -3, 1, 2, 4, 5, 7, 13, 14, 100 })
  void testSimple(int num) {
    int den = 5;
    QuotientRemainder qr = QuotientRemainder.of(num, den);
    Scalar check = qr.quotient().multiply(RealScalar.of(den)).add(qr.remainder());
    assertEquals(check, RealScalar.of(num));
    Tensor apply = NumberDecompose.of(Tensors.vector(den, 1)).apply(RealScalar.of(num));
    assertEquals(apply.Get(0), qr.quotient());
    assertEquals(apply.Get(1), qr.remainder());
  }
}
