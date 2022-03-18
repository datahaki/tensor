// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

public class MachineNumberQTest {
  @Test
  public void testRealFinite() {
    assertTrue(MachineNumberQ.of(RealScalar.of(0.)));
    assertFalse(MachineNumberQ.of(RealScalar.ZERO));
  }

  @Test
  public void testComplex() {
    assertTrue(MachineNumberQ.of(ComplexScalar.of(0., 0.3)));
    assertFalse(MachineNumberQ.of(ComplexScalar.of(0., 2)));
  }

  @Test
  public void testComplexCorner() {
    assertFalse(MachineNumberQ.of(ComplexScalar.of(Double.POSITIVE_INFINITY, 0.3)));
    assertFalse(MachineNumberQ.of(ComplexScalar.of(0., Double.NaN)));
  }

  @Test
  public void testGauss() {
    assertFalse(MachineNumberQ.of(GaussScalar.of(3, 7)));
    assertFalse(MachineNumberQ.of(GaussScalar.of(0, 7)));
  }

  @Test
  public void testCorner() {
    assertFalse(MachineNumberQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(MachineNumberQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(MachineNumberQ.of(RealScalar.of(Double.NaN)));
  }

  @Test
  public void testQuantity() {
    assertFalse(MachineNumberQ.of(Quantity.of(3, "m")));
    assertFalse(MachineNumberQ.of(Quantity.of(3.1415, "m")));
  }

  @Test
  public void testAny() {
    assertTrue(MachineNumberQ.any(Tensors.vector(1, 1, 1.)));
    assertFalse(MachineNumberQ.any(Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> MachineNumberQ.of(null));
  }
}
