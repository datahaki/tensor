// code by jph
package ch.alpine.tensor.red;

import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.num.Pi;

class InnerTest {
  @Test
  void testSimple() {
    BiFunction<Scalar, Tensor, Scalar> ex = (s, ar) -> Pi.VALUE;
    Inner.with(ex);
  }
  // public void testSimple2() {
  // BiFunction<Scalar, Scalar, Scalar> ex = (s, ar) -> Pi.VALUE;
  // Inner.with(ex);
  // }
  // public void testSimple3() {
  // BiFunction<Scalar, SparseArray, Scalar> ex = (s, ar) -> Pi.VALUE;
  // Inner.with(ex);
  // }
}
