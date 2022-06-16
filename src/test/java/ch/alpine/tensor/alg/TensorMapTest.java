// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.red.Total;

class TensorMapTest {
  @Test
  void testUnmodifiable() {
    Tensor matrix = Array.zeros(3, 1).unmodifiable();
    try {
      TensorMap.of(s -> {
        s.set(RealScalar.ONE, 0);
        return s;
      }, matrix, 1);
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertEquals(matrix, Array.zeros(3, 1));
  }

  @Test
  void testTotal() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
    Tensor result = TensorMap.of(Total::of, tensor, 1);
    assertEquals(result, Tensors.vector(6, 9));
  }

  @Test
  void testIrregular() {
    Tensor array = Tensors.fromString("{{1, 2, 3}, {8, 9}}");
    Tensor result = TensorMap.of(row -> Total.of(row), array, 1);
    assertEquals(array, Tensors.fromString("{{1, 2, 3}, {8, 9}}"));
    assertEquals(result, Tensors.vector(6, 17));
  }

  @Test
  void testModifiable() {
    Tensor matrix = Array.zeros(3, 1);
    Tensor blub = TensorMap.of(s -> {
      s.set(RealScalar.ONE, 0);
      return s;
    }, matrix, 1);
    assertEquals(matrix, ConstantArray.of(RealScalar.ONE, 3, 1));
    assertEquals(matrix, blub);
    assertFalse(matrix == blub);
  }

  @Test
  void testImageTUO() {
    TensorUnaryOperator tensorUnaryOperator = rgba -> {
      if (Scalars.isZero(rgba.Get(0)))
        return Tensors.vector(255, 248, 198, 255);
      return rgba;
    };
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/img/rgba15x33.png");
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    Tensor result = TensorMap.of(tensorUnaryOperator, tensor, 2);
    assertEquals(Dimensions.of(result), Arrays.asList(33, 15, 4));
  }

  @Test
  void testImageTSF() {
    TensorScalarFunction tensorScalarFunction = rgba -> {
      if (Scalars.isZero(rgba.Get(0)))
        return RealScalar.ONE;
      return RealScalar.ZERO;
    };
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/img/rgba15x33.png");
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    Tensor result = TensorMap.of(tensorScalarFunction, tensor, 2);
    assertEquals(Dimensions.of(result), Arrays.asList(33, 15));
  }

  @Test
  void testScalar() {
    Tensor result = TensorMap.of(RealScalar.ONE::add, RealScalar.ONE, 0);
    assertEquals(result, RealScalar.of(2));
  }

  @Test
  void testNegativeFail() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}");
    assertThrows(TensorRuntimeException.class, () -> TensorMap.of(Total::of, tensor, -1));
  }
}
