// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NdClusterInsideTest extends TestCase {
  public void testSimple() {
    NdMap<Void> ndMap = NdTreeMap.of(NdBox.of(Tensors.vector(0), Tensors.vector(1)));
    ndMap.add(Tensors.vector(0), null);
    ndMap.add(Tensors.vector(0.9), null);
    assertFalse(NdClusterInside.anyMatch(ndMap, NdCenterBase.of1Norm(Tensors.vector(0.2)), RealScalar.of(0.1)));
    assertTrue(NdClusterInside.anyMatch(ndMap, NdCenterBase.of1Norm(Tensors.vector(0.2)), RealScalar.of(0.3)));
    assertFalse(NdClusterInside.anyMatch(ndMap, NdCenterBase.of1Norm(Tensors.vector(0.5)), RealScalar.of(0.3)));
    assertTrue(NdClusterInside.anyMatch(ndMap, NdCenterBase.of1Norm(Tensors.vector(0.75)), RealScalar.of(0.3)));
  }

  public void testNullFail() {
    AssertFail.of(() -> new NdClusterInside<>(null, RealScalar.ONE));
  }

  public static class CallCount {
    int count = 0;

    public boolean query() {
      ++count;
      return true;
    }
  }

  public void testEffective() {
    boolean found = false;
    CallCount callCount = new CallCount();
    found = found || callCount.query();
    found = found || callCount.query();
    found = found || callCount.query();
    found = found || callCount.query();
    assertEquals(callCount.count, 1);
  }

  public void testEffective2() {
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
