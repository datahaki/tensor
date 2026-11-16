// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.ext.Serialization;

class AdjacentReduceTest {
  public static class Some implements TensorBinaryOperator {
    @Override
    public Tensor apply(Tensor prev, Tensor next) {
      return prev;
    }
  }

  @Test
  void testSimple() {
    Tensor tensor = Range.of(0, 5);
    int length = tensor.length();
    AdjacentReduce adjacentReduce = new AdjacentReduce(new Some());
    Tensor result = Serialization.copy(adjacentReduce).apply(tensor);
    assertEquals(result, Range.of(0, 4));
    assertEquals(result.length(), length - 1);
  }
}
