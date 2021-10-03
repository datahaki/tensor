// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Abs;
import junit.framework.TestCase;

public class TensorStreamTest extends TestCase {
  public void testStream() {
    Tensor row = IdentityMatrix.of(5).stream().skip(2).findFirst().get();
    assertEquals(row, UnitVector.of(5, 2));
  }

  public void testReduction() {
    Tensor a = Tensors.vectorDouble(2., 1.123, 0.3123);
    assertTrue(a.flatten(-1) //
        .map(Scalar.class::cast) //
        .map(Scalar::number) //
        .map(Number::doubleValue) //
        .allMatch(d -> d > 0));
  }

  public void testNorm3() {
    Tensor a = Tensors.vectorLong(2, -3, 4, -1);
    double ods = a.flatten(0) //
        .map(Scalar.class::cast) //
        .map(Abs.FUNCTION) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue) //
        .max() //
        .getAsDouble();
    assertEquals(ods, 4.0);
  }

  public void testNorm4() {
    Tensor a = Tensors.vectorLong(2, -3, 4, -1);
    double ods = a.flatten(0) //
        .map(s -> (Scalar) s) //
        .map(Abs.FUNCTION) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue) //
        .sum();
    assertEquals(ods, 10.0);
  }
}
