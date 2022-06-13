// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.StringTensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.qty.Quantity;

class PermutationsTest {
  @Test
  void testEmpty() {
    assertEquals(Permutations.of(Tensors.vector()), Tensors.fromString("{{}}")); // 0! == 1
  }

  @Test
  void testThree() {
    Tensor res = Permutations.of(Tensors.vector(1, 2, 3));
    assertEquals(res.get(0), Tensors.vector(1, 2, 3));
    assertEquals(res.get(1), Tensors.vector(1, 3, 2));
    assertEquals(res.length(), 6);
  }

  @Test
  void testThree2() {
    Tensor res = Permutations.of(Tensors.vector(1, 2, 1));
    assertEquals(res.get(0), Tensors.vector(1, 2, 1));
    assertEquals(res.get(1), Tensors.vector(1, 1, 2));
    assertEquals(res.get(2), Tensors.vector(2, 1, 1));
    assertEquals(res.length(), 3);
  }

  @Test
  void testThree1() {
    Tensor res = Permutations.of(Tensors.vector(1, 1, 1));
    assertEquals(res.length(), 1);
  }

  @Test
  void testFour2() {
    Tensor res = Permutations.of(Tensors.vector(2, -1, 2, -1));
    assertEquals(res.get(0), Tensors.vector(2, -1, 2, -1));
    assertEquals(res.get(1), Tensors.vector(2, -1, -1, 2));
    assertEquals(res.get(2), Tensors.vector(2, 2, -1, -1));
    assertEquals(res.get(3), Tensors.vector(-1, 2, 2, -1));
    assertEquals(res.length(), 6);
  }

  @Test
  void testStreamOf() {
    assertEquals(Permutations.of(Tensors.vector(2, -1, 2, -1)), //
        Tensor.of(Permutations.stream(Tensors.vector(2, -1, 2, -1))));
  }

  @Test
  void testStrings() {
    Tensor vector = StringTensor.vector("a", "b", "a");
    Tensor tensor = Permutations.of(vector);
    assertEquals(tensor, Tensors.fromString("{{a, b, a}, {a, a, b}, {b, a, a}}"));
  }

  @Test
  void testRank2() {
    Tensor tensor = Tensors.fromString("{{a, b, a}}");
    Tensor perms = Permutations.of(tensor);
    Tensor expect = Tensors.fromString("{{{a, b, a}}}");
    assertEquals(perms, expect);
  }

  @Test
  void testStream() {
    Tensor vector = StringTensor.vector("a", "b", "a");
    assertEquals(Permutations.stream(vector).count(), 3);
  }

  @Test
  void testMatrix() {
    assertEquals(Dimensions.of(Permutations.of(IdentityMatrix.of(3))), Arrays.asList(6, 3, 3));
    assertEquals(Dimensions.of(Permutations.of(IdentityMatrix.of(3).extract(0, 2))), Arrays.asList(2, 2, 3));
  }

  @Test
  void testNonComparable() {
    Tensor tensor = Tensors.of( //
        ComplexScalar.I, //
        Quaternion.of(3, 2, 3, 4), //
        Quantity.of(2, "s"), Quantity.of(2, "s"), //
        Quantity.of(-1, "m"));
    Tensor permut = Permutations.of(tensor);
    assertEquals(permut.length(), 60);
  }

  @Test
  void testTensorScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Permutations.of(RealScalar.ONE));
  }

  @Test
  void testStreamScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Permutations.stream(RealScalar.ONE));
  }
}
