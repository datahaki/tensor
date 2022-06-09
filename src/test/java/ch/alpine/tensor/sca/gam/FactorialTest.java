// code by jph
package ch.alpine.tensor.sca.gam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;

class FactorialTest {
  @Test
  public void testRealScalar() {
    assertEquals(Factorial.of(RealScalar.of(0)), RealScalar.of(1));
    assertEquals(Factorial.of(RealScalar.of(1)), RealScalar.of(1));
    assertEquals(Factorial.of(RealScalar.of(2)), RealScalar.of(2));
    assertEquals(Factorial.of(RealScalar.of(3)), RealScalar.of(6));
    assertEquals(Factorial.of(RealScalar.of(4)), RealScalar.of(24));
    assertEquals(Factorial.of(RealScalar.of(10)), RealScalar.of(3628800));
  }

  @Test
  public void testOf1() {
    Scalar result = Factorial.of(RealScalar.of(3));
    assertEquals(result, RealScalar.of(6));
  }

  @Test
  public void testOf2() {
    Tensor result = Factorial.of(Tensors.vector(0, 1, 2, 3, 4));
    assertEquals(result, Tensors.vector(1, 1, 2, 6, 24));
  }

  @Test
  public void testOfInteger() {
    assertEquals(Factorial.of(0), RealScalar.ONE);
    assertEquals(Factorial.of(1), RealScalar.ONE);
    assertEquals(Factorial.of(2), RealScalar.of(2));
  }

  @Test
  public void testLarge() {
    Factorial.of(RealScalar.of(1000));
  }

  @Test
  public void testSimple() {
    Scalar result = Tensors.vector(2, 3, 4, 3).stream() //
        .map(Scalar.class::cast) //
        .map(Factorial.FUNCTION) //
        .reduce(Scalar::multiply).orElseThrow();
    assertEquals(result, RealScalar.of(1728));
  }

  @Test
  public void testNegativeOneFail() {
    assertThrows(IllegalArgumentException.class, () -> Factorial.of(RealScalar.of(-1)));
  }

  @Test
  public void testNumericFail() {
    assertThrows(TensorRuntimeException.class, () -> Factorial.of(RealScalar.of(1.2)));
  }
}
