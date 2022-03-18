// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;

public class ArgMinTest {
  @Test
  public void testDocumentation() {
    assertEquals(ArgMin.of(Tensors.vector(3, 4, 1, 2, 3)), 2);
    assertEquals(ArgMin.of(Tensors.vector(1, 4, 1, 2, 3)), 0);
  }

  @Test
  public void testMin() {
    assertEquals(1, ArgMin.of(Tensors.vectorDouble(3., 0.6, 8, 0.6, 100)));
    assertEquals(2, ArgMin.of(Tensors.vectorDouble(3, 3., 0.6, 8, 0.6, 8)));
  }

  @Test
  public void testMinComparatorIncr() {
    assertEquals(1, ArgMin.of(Tensors.vectorDouble(3., 0.6, 8, 0.6, 100)));
    assertEquals(2, ArgMin.of(Tensors.vectorDouble(3, 3., 0.6, 8, 0.6, 8)));
  }

  @Test
  public void testMinComparatorDecr() {
    assertEquals(4, ArgMin.of(Tensors.vectorDouble(3., 0.6, 8, 0.6, 100), Collections.reverseOrder()));
    assertEquals(3, ArgMin.of(Tensors.vectorDouble(3, 3., 0.6, 8, 0.6, 8), Collections.reverseOrder()));
  }

  @Test
  public void testComparatorNullFail() {
    assertThrows(NullPointerException.class, () -> ArgMin.of(Tensors.empty(), null));
  }

  @Test
  public void testScalar() {
    assertThrows(TensorRuntimeException.class, () -> ArgMin.of(RealScalar.ONE));
  }

  @Test
  public void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> ArgMin.of(HilbertMatrix.of(6)));
  }
}
