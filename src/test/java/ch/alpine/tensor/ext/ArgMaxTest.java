// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.spa.SparseArray;

public class ArgMaxTest {
  @Test
  public void testDocumentation() {
    assertEquals(ArgMax.of(Tensors.vector(3, 4, 2, 0, 3)), 1);
    assertEquals(ArgMax.of(Tensors.vector(4, 3, 2, 4, 3)), 0);
  }

  @Test
  public void testInteger() {
    assertEquals(ArgMax.of(Arrays.asList(3, 4, 2, 0, 3)), 1);
    assertEquals(ArgMax.of(Arrays.asList(4, 3, 2, 4, 3)), 0);
  }

  @Test
  public void testComparatorNullFail() {
    assertThrows(NullPointerException.class, () -> ArgMax.of(Tensors.empty(), null));
  }

  @Test
  public void testMax() {
    assertEquals(4, ArgMax.of(Tensors.vectorDouble(3., 0.6, 8, 0.6, 100)));
    assertEquals(3, ArgMax.of(Tensors.vectorDouble(3, 3., 0.6, 8, 0.6, 0, 8)));
  }

  @Test
  public void testMaxComparatorIncr() {
    assertEquals(4, ArgMax.of(Tensors.vectorDouble(3., 0.6, 8, 0.6, 100)));
    assertEquals(3, ArgMax.of(Tensors.vectorDouble(3, 3., 0.6, 8, 0.6, 0, 8)));
  }

  @Test
  public void testMaxComparatorDecr() {
    assertEquals(1, ArgMax.of(Tensors.vectorDouble(3., 0.6, 8, 0.6, 100), Collections.reverseOrder()));
    assertEquals(5, ArgMax.of(Tensors.vectorDouble(3, 3., 0.6, 8, 0.6, 0, 8, 0), Collections.reverseOrder()));
  }

  @Test
  public void testArgMax() {
    Tensor tensor = SparseArray.of(Quantity.of(0, "m"), 5);
    tensor.set(Quantity.of(3, "m"), 1);
    assertEquals(ArgMax.of(tensor), 1);
    assertEquals(ArgMin.of(tensor), 0);
  }

  @Test
  public void testInf() {
    Scalar inf = RealScalar.of(Double.POSITIVE_INFINITY);
    Tensor vec = Tensors.of(RealScalar.ZERO, inf, inf);
    int pos = ArgMax.of(vec);
    assertEquals(pos, 1);
  }

  @Test
  public void testScalar() {
    assertThrows(TensorRuntimeException.class, () -> ArgMax.of(RealScalar.ONE));
  }

  @Test
  public void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> ArgMax.of(HilbertMatrix.of(6)));
  }
}
