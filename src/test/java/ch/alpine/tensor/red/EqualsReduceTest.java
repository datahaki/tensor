// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;

class EqualsReduceTest {
  @Test
  void testNumeric() {
    assertSame(EqualsReduce.zero(Tensors.fromString("{0,0.0,0.0,0}")), DoubleScalar.of(1).zero());
    assertSame(EqualsReduce.zero(Tensors.fromString("{0,0,0.0,0}")), DoubleScalar.of(1).zero());
    assertSame(EqualsReduce.zero(Tensors.fromString("{0,0,0,0}")), RealScalar.ZERO);
  }

  @Test
  void testFailUnique() {
    assertThrows(Exception.class, () -> Tensors.vector(1, 2, 3).stream() //
        .map(Scalar.class::cast).reduce(EqualsReduce.INSTANCE).orElseThrow());
  }
}
