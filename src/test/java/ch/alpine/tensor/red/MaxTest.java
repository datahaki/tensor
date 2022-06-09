// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.MapThread;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.GaussScalar;

class MaxTest {
  static Tensor max(List<Tensor> col) {
    return col.stream().reduce(Max::of).get();
  }

  @Test
  public void testColumnwise1() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { 1, 3, 3 }, { 2, 2, 7 } });
    Tensor res = matrix.stream().reduce(Entrywise.max()).get();
    Tensor map = MapThread.of(MaxTest::max, matrix.stream().collect(Collectors.toList()), 1);
    assertEquals(res, Tensors.vector(2, 3, 7));
    assertEquals(res, map);
  }

  @Test
  public void testRowwise() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { 8, 3, 3 }, { 2, 2, 7 } });
    Tensor res = Tensor.of(matrix.stream().map( //
        row -> row.stream().reduce(Max::of).get()));
    assertEquals(res, Tensors.vector(8, 7));
  }

  @Test
  public void testElementWise() {
    Tensor tensor = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    Tensor capped = tensor.map(Max.function(RealScalar.ZERO));
    Tensor blub = Tensors.matrixInt(new int[][] { { 0, 3, 0 }, { 2, 0, 7 } });
    assertEquals(capped, blub);
  }

  @Test
  public void testSet() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    ScalarUnaryOperator _op = Max.function(RealScalar.ZERO);
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(_op);
    matrix.set(scalarUnaryOperator, Tensor.ALL, 0);
    Tensor blub = Tensors.matrixInt(new int[][] { { 0, 3, -3 }, { 2, -2, 7 } });
    assertEquals(matrix, blub);
  }

  @Test
  public void testGenericInteger() {
    UnaryOperator<Integer> function = Max.function(100);
    assertEquals(function.apply(50), Integer.valueOf(100));
    assertEquals(function.apply(200), Integer.valueOf(200));
  }

  @Test
  public void testGenericString() {
    UnaryOperator<String> function = Max.function("math");
    assertEquals(function.apply("library"), "math");
    assertEquals(function.apply("tensor"), "tensor");
  }

  @Test
  public void testMaxNaN() {
    assertThrows(TensorRuntimeException.class, () -> Max.of(DoubleScalar.of(1), DoubleScalar.INDETERMINATE));
    assertThrows(TensorRuntimeException.class, () -> Max.of(DoubleScalar.INDETERMINATE, DoubleScalar.of(1)));
    assertThrows(TensorRuntimeException.class, () -> Max.of(RealScalar.of(1), DoubleScalar.INDETERMINATE));
    assertThrows(TensorRuntimeException.class, () -> Max.of(DoubleScalar.INDETERMINATE, RealScalar.of(1)));
  }

  @Test
  public void testFail() {
    Scalar string = StringScalar.of("string");
    Scalar gauss = GaussScalar.of(1, 3);
    assertThrows(TensorRuntimeException.class, () -> Max.of(string, gauss));
    assertThrows(TensorRuntimeException.class, () -> Max.of(gauss, string));
  }
}
