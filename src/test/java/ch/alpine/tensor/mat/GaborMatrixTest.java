// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.usr.AssertFail;

public class GaborMatrixTest {
  @Test
  public void testMatrix() {
    Tensor matrix = GaborMatrix.of(2, Tensors.vector(0.2, 0.1), RealScalar.of(0.2));
    assertEquals(Dimensions.of(matrix), Arrays.asList(5, 5));
  }

  @Test
  public void testVector() {
    Tensor vector = GaborMatrix.of(3, Tensors.vector(1), RealScalar.of(0));
    assertEquals(Dimensions.of(vector), Arrays.asList(7));
  }

  @Test
  public void testFailVector() {
    AssertFail.of(() -> GaborMatrix.of(3, RealScalar.ONE, RealScalar.of(0)));
  }

  @Test
  public void testFailMatrix() {
    AssertFail.of(() -> GaborMatrix.of(3, HilbertMatrix.of(3), RealScalar.of(0)));
  }
}
