// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;

public class NormalizeTotalTest {
  @Test
  public void testSimple() {
    Tensor tensor = NormalizeTotal.FUNCTION.apply(Tensors.vector(2, -3, 4, 5));
    assertEquals(tensor, Tensors.fromString("{1/4, -3/8, 1/2, 5/8}"));
  }

  @Test
  public void testUnitVector() {
    Tensor tensor = NormalizeTotal.FUNCTION.apply(Tensors.vector(2, 0.0, 4, 5).map(Scalar::reciprocal));
    assertEquals(tensor, UnitVector.of(4, 1));
  }

  @Test
  public void testEmpty() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(Tensors.empty()));
  }

  @Test
  public void testZeroFail() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(Tensors.vector(2, -2, 1, -1)));
  }

  @Test
  public void testZeroNumericFail() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(Tensors.vectorDouble(2, -2, 1, -1)));
  }

  @Test
  public void testFailScalar() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(Pi.TWO));
  }

  @Test
  public void testFailMatrix() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(HilbertMatrix.of(3)));
  }
}
