// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;

class ResamplingMethodsTest {
  @ParameterizedTest
  @EnumSource
  void testBasic(ResamplingMethods resamplingMethods) {
    ResamplingMethod resamplingMethod = resamplingMethods.get();
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.ONE, Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.ONE, Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 1);
    resamplingMethod.insert(navigableMap, RealScalar.of(4), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(4), Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 2);
    resamplingMethod.insert(navigableMap, RealScalar.of(5), Tensors.vector(1, 2));
  }

  @ParameterizedTest
  @EnumSource
  void testFails(ResamplingMethods resamplingMethods) {
    ResamplingMethod resamplingMethod = resamplingMethods.get();
    assertTrue(0 < resamplingMethod.toString().length());
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    assertSame(resamplingMethod.pack(navigableMap), navigableMap);
    assertThrows(Exception.class, () -> resamplingMethod.insert(navigableMap, null, RealScalar.ONE));
    assertEquals(navigableMap.size(), 0);
    resamplingMethod.insert(navigableMap, Pi.VALUE, RealScalar.ONE);
    assertEquals(resamplingMethod.evaluate(navigableMap, Pi.VALUE), RealScalar.ONE);
  }

  @Test
  void testSparse1() {
    ResamplingMethod resamplingMethod = ResamplingMethod.HOLD_VALUE_FROM_LEFT_SPARSE;
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(1), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(4), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(2), Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 2);
  }

  @Test
  void testSparse2() {
    ResamplingMethod resamplingMethod = ResamplingMethod.HOLD_VALUE_FROM_LEFT_SPARSE;
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(1), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(4), Tensors.vector(2, 3));
    resamplingMethod.insert(navigableMap, RealScalar.of(2), Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 2);
  }

  @Test
  void testSparse3() {
    ResamplingMethod resamplingMethod = ResamplingMethod.HOLD_VALUE_FROM_LEFT_SPARSE;
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(1), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(4), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(2), Tensors.vector(2, 3));
    assertEquals(navigableMap.size(), 3);
  }

  @Test
  void testSparse4() {
    ResamplingMethod resamplingMethod = ResamplingMethod.HOLD_VALUE_FROM_LEFT_SPARSE;
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(3), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(2), Tensors.vector(1, 2));
    resamplingMethod.insert(navigableMap, RealScalar.of(1), Tensors.vector(1, 2));
    assertEquals(navigableMap.size(), 3);
  }

  @Test
  void testLinearSparse() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 3}, {7, 5}, {8, 5}, {9, 5}, {10, 2}, {11, 5}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION_SPARSE);
    assertEquals(ts1.path(), Tensors.fromString("{{1, 3}, {5, 3}, {7, 5}, {9, 5}, {10, 2}, {11, 5}}"));
    TimeSeries ts2 = TimeSeries.empty(ResamplingMethod.LINEAR_INTERPOLATION_SPARSE);
    p1.forEach(kv -> ts2.insert(kv.Get(0), kv.Get(1)));
    assertEquals(ts1.path(), ts2.path());
  }

  @Test
  void testLinearSparse1() {
    Tensor p1 = Tensors.fromString("{{1, 2}, {4, 3}, {5, 3}, {7, 5}, {8, 5}, {9, 5}, {10, 2}, {11, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION_SPARSE);
    assertEquals(ts1.path(), Tensors.fromString("{{1, 2}, {4, 3}, {5, 3}, {7, 5}, {9, 5}, {10, 2}, {11, 2}}"));
  }

  @Test
  void testLinearSparse2() {
    Tensor p1 = Tensors.fromString("{{1, 2}, {4, 3}, {5, 3}, {7, 5}, {8, 5}, {9, 2}, {10, 2}, {11, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION_SPARSE);
    assertEquals(ts1.path(), Tensors.fromString("{{1, 2}, {4, 3}, {5, 3}, {7, 5}, {8, 5}, {9, 2}, {11, 2}}"));
  }
}
