// code by jph
package ch.alpine.tensor.red;

import java.io.IOException;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.nrm.Normalize;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TotalTest extends TestCase {
  public void testTotal() {
    Tensor a = Tensors.vectorLong(7, 2);
    Tensor b = Tensors.vectorLong(3, 4);
    Tensor c = Tensors.vectorLong(2, 2);
    Tensor d = Tensors.of(a, b, c);
    assertEquals(Total.of(d), Tensors.vectorLong(12, 8));
    Tensor e = Tensors.vectorLong(0, 2, 6);
    assertEquals(Total.of(e), DoubleScalar.of(2 + 6));
    assertEquals(Total.of(Tensors.empty()), DoubleScalar.of(0));
    assertEquals(DoubleScalar.of(0), Total.of(Tensors.empty()));
  }

  public void testAddEmpty() {
    Tensor a = Tensors.of(Tensors.empty());
    Tensor b = Total.of(a);
    assertEquals(b, Tensors.empty());
  }

  public void testExample() {
    Tensor tensor = Total.of(Tensors.fromString("{{1, 2}, {3, 4}, {5, 6}}"));
    assertEquals(tensor, Tensors.vector(9, 12));
  }

  public void testOfVectorSimple() {
    Scalar scalar = Total.ofVector(Tensors.vector(1, 2, 3));
    assertEquals(scalar, RealScalar.of(6));
  }

  public void testOfVectorEmpty() {
    Scalar scalar = Total.ofVector(Tensors.empty());
    assertEquals(scalar, RealScalar.ZERO);
  }

  public void testOfVectorNormalize() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = Normalize.with(Total::ofVector);
    TensorUnaryOperator copy = Serialization.copy(tensorUnaryOperator);
    Tensor vector = copy.apply(Tensors.vector(1, 2, 3));
    assertEquals(vector, Tensors.vector(1, 2, 3).divide(RealScalar.of(6)));
  }

  public void testOfVectorFail() {
    AssertFail.of(() -> Total.ofVector(RealScalar.ONE));
    AssertFail.of(() -> Total.ofVector(HilbertMatrix.of(3)));
  }

  public void testTotalScalarFail() {
    AssertFail.of(() -> Total.of(RealScalar.ONE));
  }
}
