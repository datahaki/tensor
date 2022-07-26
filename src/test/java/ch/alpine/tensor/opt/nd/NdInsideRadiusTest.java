// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

class NdInsideRadiusTest {
  @ParameterizedTest
  @EnumSource
  void testSimple(NdCenters ndCenters) {
    NdMap<Void> ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(0), Tensors.vector(1)));
    ndMap.insert(Tensors.vector(0), null);
    ndMap.insert(Tensors.vector(0.9), null);
    assertFalse(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(0.2)), RealScalar.of(0.1)));
    assertTrue(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(0.2)), RealScalar.of(0.3)));
    assertFalse(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(0.5)), RealScalar.of(0.3)));
    assertTrue(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(0.75)), RealScalar.of(0.3)));
  }

  @ParameterizedTest
  @EnumSource
  void testExact(NdCenters ndCenters) {
    NdMap<Void> ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(0), Tensors.vector(4)));
    ndMap.insert(Tensors.vector(0), null);
    ndMap.insert(Tensors.vector(4), null);
    assertTrue(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(0)), RealScalar.of(0)));
    assertTrue(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(4)), RealScalar.of(0)));
    assertTrue(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(1)), RealScalar.of(1)));
    assertTrue(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(2)), RealScalar.of(2)));
    assertTrue(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(3)), RealScalar.of(1)));
    assertFalse(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(1)), RealScalar.of(0.9)));
    assertFalse(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(2)), RealScalar.of(1.9)));
    assertFalse(NdInsideRadius.anyMatch(ndMap, ndCenters.apply(Tensors.vector(3)), RealScalar.of(0.9)));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> new NdInsideRadius<>(null, RealScalar.ONE));
  }

  public static class CallCount {
    int count = 0;

    public boolean query() {
      ++count;
      return true;
    }
  }

  @Test
  void testEffective() {
    boolean found = false;
    CallCount callCount = new CallCount();
    found = found || callCount.query();
    found = found || callCount.query();
    found = found || callCount.query();
    found = found || callCount.query();
    assertEquals(callCount.count, 1);
  }

  @Test
  void testEffective2() {
    boolean found = false;
    CallCount callCount = new CallCount();
    if (!found)
      found = callCount.query();
    if (!found)
      found = callCount.query();
    if (!found)
      found = callCount.query();
    if (!found)
      found = callCount.query();
    assertEquals(callCount.count, 1);
  }
}
