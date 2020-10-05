// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class AccumulateTest extends TestCase {
  public void testEmpty() {
    assertEquals(Accumulate.of(Tensors.empty()), Tensors.empty());
  }

  public void testVector() {
    Tensor vector = Tensors.vector(2, 3, 1, 0);
    assertEquals(Accumulate.of(vector), Tensors.vector(2, 5, 6, 6));
  }

  public void testProd() {
    Tensor vector = Tensors.vector(2, 3, -1, 0);
    assertEquals(Accumulate.prod(vector), Tensors.vector(2, 6, -6, 0));
  }

  public void testMatrixAdd() {
    Tensor matrix = Tensors.matrix(new Number[][] { { 1, 2 }, { 5, 5 }, { -3, -9 } });
    Tensor actual = Accumulate.of(matrix);
    Tensor expected = Tensors.matrix(new Number[][] { { 1, 2 }, { 6, 7 }, { 3, -2 } });
    assertEquals(expected, actual);
  }

  public void testMatrixProd() {
    Tensor matrix = Tensors.matrix(new Number[][] { { 1, 2 }, { 5, 5 }, { -3, -9 } });
    assertEquals(Accumulate.prod(matrix), Tensors.fromString("{{1, 2}, {5, 10}, {-15, -90}}"));
  }

  public void testProdAd() {
    Tensor tensor = Accumulate.prod(Array.zeros(3, 3, 3));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 3, 3));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Accumulate.of(RealScalar.ONE));
  }

  public void testScalarProdFail() {
    AssertFail.of(() -> Accumulate.prod(RealScalar.ONE));
  }

  public void testNullFail() {
    AssertFail.of(() -> Accumulate.of(null));
  }

  public void testNullProdFail() {
    AssertFail.of(() -> Accumulate.prod(null));
  }
}
