// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MachineNumberQTest extends TestCase {
  public void testRealFinite() {
    assertTrue(MachineNumberQ.of(RealScalar.of(0.)));
    assertFalse(MachineNumberQ.of(RealScalar.ZERO));
  }

  public void testComplex() {
    assertTrue(MachineNumberQ.of(ComplexScalar.of(0., 0.3)));
    assertFalse(MachineNumberQ.of(ComplexScalar.of(0., 2)));
  }

  public void testComplexCorner() {
    assertFalse(MachineNumberQ.of(ComplexScalar.of(Double.POSITIVE_INFINITY, 0.3)));
    assertFalse(MachineNumberQ.of(ComplexScalar.of(0., Double.NaN)));
  }

  public void testGauss() {
    assertFalse(MachineNumberQ.of(GaussScalar.of(3, 7)));
    assertFalse(MachineNumberQ.of(GaussScalar.of(0, 7)));
  }

  public void testCorner() {
    assertFalse(MachineNumberQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(MachineNumberQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(MachineNumberQ.of(RealScalar.of(Double.NaN)));
  }

  public void testQuantity() {
    assertFalse(MachineNumberQ.of(Quantity.of(3, "m")));
    assertFalse(MachineNumberQ.of(Quantity.of(3.1415, "m")));
  }

  public void testAny() {
    assertTrue(MachineNumberQ.any(Tensors.vector(1, 1, 1.)));
    assertFalse(MachineNumberQ.any(Tensors.vector(1, 2, 3)));
  }

  public void testNullFail() {
    AssertFail.of(() -> MachineNumberQ.of(null));
  }
}
