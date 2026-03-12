// code by jph
package ch.alpine.tensor.mat.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Nest;
import test.wrap.SerializableQ;

class LinearSubspaceTest {
  @Test
  void testScalar() {
    LinearSubspace linearSubspace = LinearSubspace.of(_ -> RealScalar.ZERO, List.of());
    assertEquals(linearSubspace.apply(Tensors.of(Pi.VALUE)), Pi.VALUE);
    Tensor zeros = Array.zeros();
    assertEquals(zeros, RealScalar.ZERO);
  }

  @Test
  void testVector() {
    LinearSubspace linearSubspace = LinearSubspace.of(v -> Tensors.vector(1, 0, 0).dot(v), 3);
    assertEquals(linearSubspace.basis(), Tensors.fromString("{{0, 1, 0}, {0, 0, 1}}"));
    Tensor w = linearSubspace.projection(Tensors.vector(3, 4, 5));
    assertEquals(w, Tensors.vector(0, 4, 5));
    assertTrue(linearSubspace.toString().startsWith("LinearSubspace["));
    SerializableQ.require(linearSubspace);
  }

  @RepeatedTest(6)
  void testArray(RepetitionInfo repetitionInfo) {
    int d = repetitionInfo.getCurrentRepetition() - 1;
    // ---
    int[] dims = IntStream.range(0, d).limit(d).map(_ -> 1).toArray();
    LinearSubspace linearSubspace = LinearSubspace.of(_ -> RealScalar.ZERO, dims);
    linearSubspace.toString();
    Tensor tensor = Nest.of(v -> Tensors.of(v), (Tensor) Pi.VALUE, d);
    assertEquals(linearSubspace.apply(Tensors.of(Pi.VALUE)), tensor);
    assertEquals(linearSubspace.projection(tensor), tensor);
    Tensor zeros = Array.zeros(dims);
    assertEquals(zeros, tensor.maps(Scalar::zero));
    assertEquals(zeros, ConstantArray.of(RealScalar.ZERO, dims));
  }

  @RepeatedTest(6)
  void testArrayEmpty(RepetitionInfo repetitionInfo) {
    int d = repetitionInfo.getCurrentRepetition() - 1;
    // ---
    int[] dims = IntStream.range(0, d).limit(d).map(_ -> 1).toArray();
    LinearSubspace linearSubspace = LinearSubspace.of(_ -> RealScalar.ONE, dims);
    linearSubspace.toString();
    Tensor tensor = Nest.of(v -> Tensors.of(v), (Tensor) Pi.VALUE.zero(), d);
    assertEquals(linearSubspace.apply(Tensors.empty()), tensor);
    assertEquals(linearSubspace.projection(tensor.maps(_ -> RealScalar.ONE)), tensor);
    Tensor zeros = Array.zeros(dims);
    assertEquals(zeros, tensor.maps(Scalar::zero));
    assertEquals(zeros, ConstantArray.of(RealScalar.ZERO, dims));
  }

  @Test
  void testBlub() {
    Tensor weight = Tensors.empty();
    Tensor tensor = Tensors.fromString("{{{}}}");
    assertThrows(Exception.class, () -> weight.dot(tensor));
  }
}
