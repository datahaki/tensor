// code by jph
package ch.ethz.idsc.tensor.alg;

import java.io.IOException;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PadRightTest extends TestCase {
  public void testVectorLo() {
    TensorUnaryOperator tuo = PadRight.zeros(10);
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor result = tuo.apply(vector);
    assertEquals(result.extract(0, 6), vector);
    assertEquals(result.extract(6, 10), Array.zeros(4));
  }

  public void testVectorHi() {
    TensorUnaryOperator tuo = PadRight.zeros(4);
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor result = tuo.apply(vector);
    assertEquals(result, vector.extract(0, 4));
  }

  public void testMatrixRegular() {
    TensorUnaryOperator tuo = PadRight.zeros(2, 4);
    Tensor vector = Tensors.fromString("{{1, 2, 3}}");
    Tensor result = tuo.apply(vector);
    assertEquals(result, Tensors.fromString("{{1, 2, 3, 0}, {0, 0, 0, 0}}"));
  }

  public void testMatrixIrregular1() {
    TensorUnaryOperator tuo = PadRight.zeros(3, 4);
    Tensor vector = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
    Tensor result = tuo.apply(vector);
    assertEquals(result, Tensors.fromString("{{1, 2, 3, 0}, {4, 5, 0, 0}, {0, 0, 0, 0}}"));
  }

  public void testMatrixIrregular2() {
    TensorUnaryOperator tuo = PadRight.zeros(1, 2);
    Tensor vector = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
    Tensor result = tuo.apply(vector);
    assertEquals(result, Tensors.fromString("{{1, 2}}"));
  }

  public void testMatrixIrregular3() {
    TensorUnaryOperator tuo = PadRight.zeros(2, 2);
    Tensor vector = Tensors.fromString("{{1}, {2}, {4, 5}}");
    Tensor result = tuo.apply(vector);
    assertEquals(result, Tensors.fromString("{{1, 0}, {2, 0}}"));
  }

  public void testSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(PadRight.zeros());
  }

  public void testQuantity() {
    Scalar element = Quantity.of(2, "Apples");
    TensorUnaryOperator tuo = PadRight.with(element, 3);
    Tensor tensor = tuo.apply(Tensors.fromString("{1[A], 2[V]}"));
    assertEquals(tensor.toString(), "{1[A], 2[V], 2[Apples]}");
  }

  public void testFail() {
    TensorUnaryOperator tuo = PadRight.zeros(2, 2, 6);
    AssertFail.of(() -> tuo.apply(Tensors.fromString("{{1}, {2}, {4, 5}}")));
  }

  public void testFail2() {
    TensorUnaryOperator tuo = PadRight.zeros(-2);
    AssertFail.of(() -> tuo.apply(Tensors.vector(1, 2, 3)));
  }
}
