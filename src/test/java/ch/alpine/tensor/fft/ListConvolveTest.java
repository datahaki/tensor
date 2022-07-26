// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ArrayPad;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.TensorMap;
import ch.alpine.tensor.alg.TensorRank;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;

class ListConvolveTest {
  @Test
  void testVector1() {
    Tensor kernel = Tensors.vector(0, -1, 3);
    Tensor tensor = ArrayPad.of(Tensors.vector(1, 6, 0, 0, -1), //
        Arrays.asList(kernel.length() - 1), Arrays.asList(kernel.length() - 1));
    Tensor result = ListConvolve.of(kernel, tensor);
    Tensor actual = Tensors.vector(0, -1, -3, 18, 0, 1, -3);
    assertEquals(result, actual);
  }

  @Test
  void testMatrix() {
    Tensor kernel = Tensors.fromString("{{2, 1, 3}, {0, 1, -1}}");
    Tensor tensor = Tensors.fromString("{{0, 0, 1, 0, -2, 1, 2}, {2, 0, 1, 0, -2, 1, 2}, {3, 2, 3, 3, 45, 3, 2}}");
    Tensor result = ListConvolve.of(kernel, tensor);
    Tensor actual = Tensors.fromString("{{8, 2, -2, -2, 2}, {15, 16, 101, 58, 145}}");
    assertEquals(result, actual);
  }

  @Test
  void testLastLayer() {
    Tensor kernel = Tensors.vector(1, -1);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 } });
    Tensor result = TensorMap.of(tensor -> ListConvolve.of(kernel, tensor), matrix, TensorRank.of(matrix) - 1);
    assertEquals(Dimensions.of(result), Arrays.asList(2, 4));
  }

  @Test
  void testOperator() {
    Tensor kernel = Tensors.vector(1, -1);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 } });
    Tensor result = TensorMap.of(ListConvolve.with(kernel), matrix, TensorRank.of(matrix) - 1);
    assertEquals(Dimensions.of(result), Arrays.asList(2, 4));
    assertEquals(result, Tensors.matrixInt(new int[][] { { -1, 2, -3, 1 }, { 1, -2, 4, 0 } }));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Tensor kernel = Tensors.of(Tensors.vector(1, -1));
    UnaryOperator<Tensor> uo = ListConvolve.with(kernel);
    UnaryOperator<Tensor> cp = Serialization.copy(uo);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 } });
    Tensor result1 = uo.apply(matrix);
    Tensor result2 = cp.apply(matrix);
    assertEquals(result1, Tensors.matrixInt(new int[][] { { -1, 2, -3, 1 }, { 1, -2, 4, 0 } }));
    assertEquals(result1, result2);
  }

  @Test
  void testSameSame() {
    Tensor kernel = HilbertMatrix.of(3);
    Tensor matrix = ListConvolve.of(kernel, kernel);
    // confirmed with Mathematica ListConvolve[HilbertMatrix[3], HilbertMatrix[3]]
    assertEquals(matrix, Tensors.fromString("{{37/30}}"));
  }

  @Test
  void testOutsideMathematica() {
    Tensor kernel = Tensors.vector(1, -1);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 }, //
        { 0, 1, -1, 3, 3 } });
    Tensor tensor = ListConvolve.of(kernel, matrix);
    assertEquals(tensor, Tensors.fromString("{{-2, 0, -4, 3, 2}, {0, 0, 0, 0, 0}}"));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testRankFail() {
    Tensor kernel = HilbertMatrix.of(2);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 }, //
        { 0, 1, -1, 3, 3 } });
    ListConvolve.of(kernel, matrix);
    assertThrows(Throw.class, () -> ListConvolve.of(kernel, matrix.get(0)));
  }

  @Test
  void testConvolveNullFail() {
    assertThrows(NullPointerException.class, () -> ListConvolve.with(null));
  }
}
