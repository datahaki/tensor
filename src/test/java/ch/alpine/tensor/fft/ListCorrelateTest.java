// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;

class ListCorrelateTest {
  @Test
  public void testVector1() {
    Tensor kernel = Tensors.vector(2, 1, 3);
    Tensor tensor = Tensors.vector(0, 0, 1, 0, 0, 0);
    Tensor result = ListCorrelate.of(kernel, tensor);
    Tensor actual = Tensors.vector(3, 1, 2, 0);
    assertEquals(result, actual);
  }

  @Test
  public void testVector2() {
    Tensor kernel = Tensors.vector(2, 1, 3);
    Tensor tensor = Tensors.vector(0, 0, 1, 0, -2, 1, 2);
    Tensor result = ListCorrelate.of(kernel, tensor);
    Tensor actual = Tensors.vector(3, 1, -4, 1, 3);
    assertEquals(result, actual);
    TensorUnaryOperator tuo = ListCorrelate.with(kernel);
    assertTrue(tuo.toString().startsWith("ListCorrelate["));
  }

  @Test
  public void testMatrix() {
    Tensor kernel = Tensors.fromString("{{2, 1, 3}, {0, 1, -1}}");
    Tensor tensor = Tensors.fromString("{{0, 0, 1, 0, -2, 1, 2}, {2, 0, 1, 0, -2, 1, 2}, {3, 2, 3, 3, 45, 3, 2}}");
    Tensor result = ListCorrelate.of(kernel, tensor);
    Tensor actual = Tensors.fromString("{{2, 2, -2, -2, 2}, {6, 1, -46, 43, 4}}");
    assertEquals(result, actual);
  }

  @Test
  public void testRank3() {
    Tensor kernel = Tensors.fromString("{{{2, 1, 3}, {0, 1, -1}}}");
    Tensor tensor = Tensors.fromString("{{{0, 0, 1, 0, -2, 1, 2}, {2, 0, 1, 0, -2, 1, 2}, {3, 2, 3, 3, 45, 3, 2}}}");
    Tensor result = ListCorrelate.of(kernel, tensor);
    Tensor actual = Tensors.fromString("{{{2, 2, -2, -2, 2}, {6, 1, -46, 43, 4}}}");
    assertEquals(result, actual);
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    Tensor kernel = Tensors.of(Tensors.vector(1, -1));
    UnaryOperator<Tensor> uo = ListCorrelate.with(kernel);
    UnaryOperator<Tensor> cp = Serialization.copy(uo);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 } });
    Tensor result1 = uo.apply(matrix);
    Tensor result2 = cp.apply(matrix);
    assertEquals(result1, Tensors.matrixInt(new int[][] { { 1, -2, 3, -1 }, { -1, 2, -4, 0 } }));
    assertEquals(result1, result2);
  }

  @Test
  public void testSameSame() {
    Tensor kernel = HilbertMatrix.of(3);
    Tensor matrix = ListCorrelate.of(kernel, kernel);
    // confirmed with Mathematica ListCorrelate[HilbertMatrix[3], HilbertMatrix[3]]
    assertEquals(matrix, Tensors.fromString("{{1199/600}}"));
  }

  @Test
  public void testNarrow1() {
    Tensor kernel = Tensors.vector(2, 1, 3);
    Tensor tensor = Tensors.vector(4, 5);
    assertThrows(IllegalArgumentException.class, () -> ListCorrelate.of(kernel, tensor));
  }

  @Test
  public void testNarrow2() {
    Tensor kernel = Tensors.fromString("{{1, 2, 3}}");
    Tensor tensor = Tensors.fromString("{{1, 2}}");
    assertThrows(IllegalArgumentException.class, () -> ListCorrelate.of(kernel, tensor));
  }

  @Test
  public void testNarrow3() {
    Tensor kernel = Tensors.fromString("{{1, 2, 3}, {2, 3, 4}}");
    Tensor tensor = Tensors.fromString("{{1, 2, 3}}");
    assertThrows(IllegalArgumentException.class, () -> ListCorrelate.of(kernel, tensor));
  }

  @Test
  public void testScalarFail() {
    Tensor kernel = RealScalar.ZERO;
    Tensor tensor = RealScalar.ONE;
    assertThrows(TensorRuntimeException.class, () -> ListCorrelate.of(kernel, tensor));
  }

  @Test
  public void testRankFail() {
    Tensor kernel = Tensors.vector(1, -1);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 }, //
        { 0, 1, -1, 3, 3 } });
    Tensor result = ListCorrelate.of(kernel, matrix);
    ExactTensorQ.require(result);
    assertEquals(result, Tensors.fromString("{{2, 0, 4, -3, -2}, {0, 0, 0, 0, 0}}"));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> ListCorrelate.with(null));
  }

  @Test
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(ListCorrelate.class.getModifiers()));
  }
}
