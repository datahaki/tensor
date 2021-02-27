// code by jph
package ch.ethz.idsc.tensor.fft;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.function.UnaryOperator;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ListCorrelateTest extends TestCase {
  public void testVector1() {
    Tensor kernel = Tensors.vector(2, 1, 3);
    Tensor tensor = Tensors.vector(0, 0, 1, 0, 0, 0);
    Tensor result = ListCorrelate.of(kernel, tensor);
    Tensor actual = Tensors.vector(3, 1, 2, 0);
    assertEquals(result, actual);
  }

  public void testVector2() {
    Tensor kernel = Tensors.vector(2, 1, 3);
    Tensor tensor = Tensors.vector(0, 0, 1, 0, -2, 1, 2);
    Tensor result = ListCorrelate.of(kernel, tensor);
    Tensor actual = Tensors.vector(3, 1, -4, 1, 3);
    assertEquals(result, actual);
    TensorUnaryOperator tuo = ListCorrelate.with(kernel);
    assertTrue(tuo.toString().startsWith("ListCorrelate["));
  }

  public void testMatrix() {
    Tensor kernel = Tensors.fromString("{{2, 1, 3}, {0, 1, -1}}");
    Tensor tensor = Tensors.fromString("{{0, 0, 1, 0, -2, 1, 2}, {2, 0, 1, 0, -2, 1, 2}, {3, 2, 3, 3, 45, 3, 2}}");
    Tensor result = ListCorrelate.of(kernel, tensor);
    Tensor actual = Tensors.fromString("{{2, 2, -2, -2, 2}, {6, 1, -46, 43, 4}}");
    assertEquals(result, actual);
  }

  public void testRank3() {
    Tensor kernel = Tensors.fromString("{{{2, 1, 3}, {0, 1, -1}}}");
    Tensor tensor = Tensors.fromString("{{{0, 0, 1, 0, -2, 1, 2}, {2, 0, 1, 0, -2, 1, 2}, {3, 2, 3, 3, 45, 3, 2}}}");
    Tensor result = ListCorrelate.of(kernel, tensor);
    Tensor actual = Tensors.fromString("{{{2, 2, -2, -2, 2}, {6, 1, -46, 43, 4}}}");
    assertEquals(result, actual);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Tensor kernel = Tensors.of(Tensors.vector(1, -1));
    UnaryOperator<Tensor> uo = ListCorrelate.with(kernel);
    UnaryOperator<Tensor> cp = Serialization.copy(uo);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 } });
    Tensor result1 = uo.apply(matrix);
    Tensor result2 = cp.apply(matrix);
    assertEquals(result1, Tensors.matrixInt(new int[][] { { 1, -2, 3, -1 }, { -1, 2, -4, 0 } }));
    assertEquals(result1, result2);
  }

  public void testSameSame() {
    Tensor kernel = HilbertMatrix.of(3);
    Tensor matrix = ListCorrelate.of(kernel, kernel);
    // confirmed with Mathematica ListCorrelate[HilbertMatrix[3], HilbertMatrix[3]]
    assertEquals(matrix, Tensors.fromString("{{1199/600}}"));
  }

  public void testNarrow1() {
    Tensor kernel = Tensors.vector(2, 1, 3);
    Tensor tensor = Tensors.vector(4, 5);
    AssertFail.of(() -> ListCorrelate.of(kernel, tensor));
  }

  public void testNarrow2() {
    Tensor kernel = Tensors.fromString("{{1, 2, 3}}");
    Tensor tensor = Tensors.fromString("{{1, 2}}");
    AssertFail.of(() -> ListCorrelate.of(kernel, tensor));
  }

  public void testNarrow3() {
    Tensor kernel = Tensors.fromString("{{1, 2, 3}, {2, 3, 4}}");
    Tensor tensor = Tensors.fromString("{{1, 2, 3}}");
    AssertFail.of(() -> ListCorrelate.of(kernel, tensor));
  }

  public void testScalarFail() {
    Tensor kernel = RealScalar.ZERO;
    Tensor tensor = RealScalar.ONE;
    AssertFail.of(() -> ListCorrelate.of(kernel, tensor));
  }

  public void testRankFail() {
    Tensor kernel = Tensors.vector(1, -1);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 }, //
        { 0, 1, -1, 3, 3 } });
    Tensor result = ListCorrelate.of(kernel, matrix);
    ExactTensorQ.require(result);
    assertEquals(result, Tensors.fromString("{{2, 0, 4, -3, -2}, {0, 0, 0, 0, 0}}"));
  }

  public void testNullFail() {
    AssertFail.of(() -> ListCorrelate.with(null));
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(ListCorrelate.class.getModifiers()));
  }
}
