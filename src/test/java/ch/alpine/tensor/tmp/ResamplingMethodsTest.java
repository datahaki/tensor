// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class ResamplingMethodsTest {
  @ParameterizedTest
  @EnumSource
  void testBasic(ResamplingMethods resamplingMethods) {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethods.insert(navigableMap, RealScalar.ONE, Tensors.vector(1, 2));
    resamplingMethods.insert(navigableMap, RealScalar.ONE, Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 1);
    resamplingMethods.insert(navigableMap, RealScalar.of(4), Tensors.vector(1, 2));
    resamplingMethods.insert(navigableMap, RealScalar.of(4), Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 2);
    resamplingMethods.insert(navigableMap, RealScalar.of(5), Tensors.vector(1, 2));
  }

  @ParameterizedTest
  @EnumSource
  void testFails(ResamplingMethods resamplingMethods) {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    assertThrows(Exception.class, () -> resamplingMethods.insert(navigableMap, null, RealScalar.ONE));
    assertEquals(navigableMap.size(), 0);
  }

  @Test
  void testSparse1() {
    ResamplingMethod resamplingMethod = ResamplingMethods.HOLD_LO_SPARSE;
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(1), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(4), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(2), Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 2);
  }

  @Test
  void testSparse2() {
    ResamplingMethod resamplingMethod = ResamplingMethods.HOLD_LO_SPARSE;
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(1), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(4), Tensors.vector(2, 3));
    resamplingMethod.insert(navigableMap, RealScalar.of(2), Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 2);
  }

  @Test
  void testSparse3() {
    ResamplingMethod resamplingMethod = ResamplingMethods.HOLD_LO_SPARSE;
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(1), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(4), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(2), Tensors.vector(2, 3));
    assertEquals(navigableMap.size(), 3);
  }

  @Test
  void testSparse4() {
    ResamplingMethod resamplingMethod = ResamplingMethods.HOLD_LO_SPARSE;
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(3), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(2), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(1), Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 3);
  }
}
