// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.num.Pi;

class AbstractScalarTest {
  @Test
  void testMap() {
    Tensor c = Tensors.fromString("{{1}, {4}, {4}}");
    Tensor a = Tensors.vector(1, 2, 3);
    Tensor b = Tensors.vector(4).unmodifiable();
    a = a.maps(_ -> b);
    a.set(RealScalar.ONE, 0, 0); // requires copy() in AbstractScalar::map
    assertEquals(a, c);
  }

  @Test
  void testSet() {
    Tensor a = Tensors.vector(1, 2, 3);
    Tensor b = Tensors.vector(4).unmodifiable();
    a.set(_ -> b, Tensor.ALL);
    a.set(RealScalar.ONE, 0, 0);
    Tensor c = Tensors.fromString("{{1}, {4}, {4}}");
    assertEquals(a, c);
  }

  @Test
  void testSetAll() {
    Tensor matrix = HilbertMatrix.of(5);
    matrix.set(Tensor::negate, Tensor.ALL, 1);
    matrix.set(Tensor::negate, 3, Tensor.ALL);
    matrix.set(Tensor::negate, 1);
    matrix.set(Tensor::negate, Tensor.ALL, 3);
    SymmetricMatrixQ.INSTANCE.requireMember(matrix);
  }

  @Test
  void testGet1Fail() {
    assertEquals(Pi.VALUE.get(), Pi.VALUE);
    assertThrows(Throw.class, () -> Pi.VALUE.get(List.of(0)));
    assertThrows(Throw.class, () -> Pi.VALUE.get(Arrays.asList(0, 0)));
    assertThrows(Throw.class, () -> Pi.VALUE.get(List.of(-1)));
    assertThrows(Throw.class, () -> Pi.VALUE.get(Arrays.asList(-1, 0)));
    assertThrows(Throw.class, () -> RealScalar.ONE.Get(1));
    assertThrows(Throw.class, () -> RealScalar.ONE.get(new int[] { 1 }));
  }

  @Test
  void testGet2Fail() {
    assertThrows(Throw.class, () -> RationalScalar.HALF.Get(1, 4));
    assertThrows(Throw.class, () -> Pi.TWO.get(new int[] { 1, 2 }));
  }

  @Test
  void testSetFail() {
    assertThrows(Throw.class, () -> RealScalar.ONE.set(RealScalar.ZERO));
    assertThrows(Throw.class, () -> RealScalar.ONE.set(_ -> RealScalar.ZERO));
  }

  @Test
  void testSetListFail() {
    assertThrows(Throw.class, () -> Pi.VALUE.set(RealScalar.ZERO, Integers.asList(new int[] {})));
    assertThrows(Throw.class, () -> Pi.VALUE.set(RealScalar.ZERO, Integers.asList(new int[] { 2 })));
    assertThrows(Throw.class, () -> Pi.VALUE.set(RealScalar.ZERO::add, Integers.asList(new int[] {})));
    assertThrows(Throw.class, () -> Pi.VALUE.set(RealScalar.ZERO::add, Integers.asList(new int[] { 2 })));
  }

  @Test
  void testFlatten() {
    assertEquals(Flatten.scalars(Pi.VALUE).count(), 1L);
    assertEquals(Flatten.stream(Pi.VALUE, -1).count(), 1L);
    assertEquals(Flatten.stream(Pi.VALUE, 0).count(), 1L);
  }

  @Test
  void testAppendFail() {
    assertThrows(Throw.class, () -> RealScalar.ONE.append(RealScalar.ZERO));
  }

  @Test
  void testExtractFail() {
    assertThrows(Throw.class, () -> RealScalar.ONE.extract(1, 2));
  }

  @Test
  void testBlockEmpty() {
    assertEquals(Pi.VALUE.block(List.of(), List.of()), Pi.VALUE);
  }

  @Test
  void testBlockFail() {
    assertThrows(Throw.class, () -> RealScalar.ONE.block(List.of(1), List.of(1)));
  }
}
