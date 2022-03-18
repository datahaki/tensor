// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

public class GeometricMeanTest {
  @Test
  public void testGeo1() {
    assertEquals(GeometricMean.of(Tensors.vectorDouble(4, 9)), RealScalar.of(6));
    Tensor a = GeometricMean.of(Tensors.vectorDouble(8, 27, 525));
    // 48.4029
    Tolerance.CHOP.requireClose(a, RealScalar.of(48.4028593807363));
  }

  @Test
  public void testGeo2() {
    Tensor a = Tensors.matrixDouble(new double[][] { { 5, 10 }, { 2, 1 }, { 4, 3 }, { 12, 15 } });
    Tensor b = GeometricMean.of(a);
    // {4.68069, 4.60578}
    Tensor r = Tensors.vector(4.680694638641432, 4.605779351596907);
    Tolerance.CHOP.requireClose(b, r);
  }

  @Test
  public void testFailScalar() {
    assertThrows(TensorRuntimeException.class, () -> GeometricMean.of(RealScalar.ONE));
  }

  @Test
  public void testFailEmpty() {
    assertThrows(ArithmeticException.class, () -> GeometricMean.of(Tensors.empty()));
  }

  @Test
  public void testFailMatrix() {
    Tensor mean = GeometricMean.of(HilbertMatrix.of(4));
    Tensor expected = Tensors.vector(0.4518010018049224, 0.3021375397356768, 0.2295748846661433, 0.18575057999133598);
    Chop._14.requireClose(mean, expected);
  }
}
