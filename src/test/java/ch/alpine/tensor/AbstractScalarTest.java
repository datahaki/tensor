// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.num.Pi;

class AbstractScalarTest {
  @Test
  public void testMap() {
    Tensor c = Tensors.fromString("{{1}, {4}, {4}}");
    Tensor a = Tensors.vector(1, 2, 3);
    Tensor b = Tensors.vector(4).unmodifiable();
    a = a.map(s -> b);
    a.set(RealScalar.ONE, 0, 0); // requires copy() in AbstractScalar::map
    assertEquals(a, c);
  }

  @Test
  public void testSet() {
    Tensor a = Tensors.vector(1, 2, 3);
    Tensor b = Tensors.vector(4).unmodifiable();
    a.set(s -> b, Tensor.ALL);
    a.set(RealScalar.ONE, 0, 0);
    Tensor c = Tensors.fromString("{{1}, {4}, {4}}");
    assertEquals(a, c);
  }

  @Test
  public void testSetAll() {
    Tensor matrix = HilbertMatrix.of(5);
    matrix.set(Tensor::negate, Tensor.ALL, 1);
    matrix.set(Tensor::negate, 3, Tensor.ALL);
    matrix.set(Tensor::negate, 1);
    matrix.set(Tensor::negate, Tensor.ALL, 3);
    SymmetricMatrixQ.require(matrix);
  }

  @Test
  public void testGet1Fail() {
    assertEquals(Pi.VALUE.get(), Pi.VALUE);
    assertThrows(TensorRuntimeException.class, () -> Pi.VALUE.get(Arrays.asList(0)));
    assertThrows(TensorRuntimeException.class, () -> Pi.VALUE.get(Arrays.asList(0, 0)));
    assertThrows(TensorRuntimeException.class, () -> Pi.VALUE.get(Arrays.asList(-1)));
    assertThrows(TensorRuntimeException.class, () -> Pi.VALUE.get(Arrays.asList(-1, 0)));
    assertThrows(TensorRuntimeException.class, () -> RealScalar.ONE.Get(1));
    assertThrows(TensorRuntimeException.class, () -> RealScalar.ONE.get(new int[] { 1 }));
  }

  @Test
  public void testGet2Fail() {
    assertThrows(TensorRuntimeException.class, () -> RationalScalar.HALF.Get(1, 4));
    assertThrows(TensorRuntimeException.class, () -> Pi.TWO.get(new int[] { 1, 2 }));
  }

  @Test
  public void testSetFail() {
    assertThrows(TensorRuntimeException.class, () -> RealScalar.ONE.set(RealScalar.ZERO));
    assertThrows(TensorRuntimeException.class, () -> RealScalar.ONE.set(s -> RealScalar.ZERO));
  }

  @Test
  public void testSetListFail() {
    assertThrows(TensorRuntimeException.class, () -> Pi.VALUE.set(RealScalar.ZERO, Integers.asList(new int[] {})));
    assertThrows(TensorRuntimeException.class, () -> Pi.VALUE.set(RealScalar.ZERO, Integers.asList(new int[] { 2 })));
    assertThrows(TensorRuntimeException.class, () -> Pi.VALUE.set(RealScalar.ZERO::add, Integers.asList(new int[] {})));
    assertThrows(TensorRuntimeException.class, () -> Pi.VALUE.set(RealScalar.ZERO::add, Integers.asList(new int[] { 2 })));
  }

  @Test
  public void testAppendFail() {
    assertThrows(TensorRuntimeException.class, () -> RealScalar.ONE.append(RealScalar.ZERO));
  }

  @Test
  public void testExtractFail() {
    assertThrows(TensorRuntimeException.class, () -> RealScalar.ONE.extract(1, 2));
  }

  @Test
  public void testBlockEmpty() {
    assertEquals(Pi.VALUE.block(Arrays.asList(), Arrays.asList()), Pi.VALUE);
  }

  @Test
  public void testBlockFail() {
    assertThrows(TensorRuntimeException.class, () -> RealScalar.ONE.block(Arrays.asList(1), Arrays.asList(1)));
  }
}
