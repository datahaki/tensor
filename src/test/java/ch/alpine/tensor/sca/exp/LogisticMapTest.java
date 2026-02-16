// code by jph
package ch.alpine.tensor.sca.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

class LogisticMapTest {
  @Test
  void test39() {
    Supplier<Scalar> logisticMap = LogisticMap.of(3.9, 0.3);
    Tensor vector = Tensor.of(Stream.generate(logisticMap).limit(10));
    assertEquals(vector.stream().distinct().count(), 10);
  }

  @Test
  void testFinite() {
    Supplier<Scalar> logisticMap = LogisticMap.of(3.9, 0.3);
    Stream.generate(logisticMap).limit(10000).count();
  }
}
