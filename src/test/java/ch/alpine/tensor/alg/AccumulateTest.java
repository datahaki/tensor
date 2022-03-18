// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;

public class AccumulateTest {
  @Test
  public void testEmpty() {
    assertEquals(Accumulate.of(Tensors.empty()), Tensors.empty());
  }

  @Test
  public void testVector() {
    Tensor vector = Tensors.vector(2, 3, 1, 0);
    assertEquals(Accumulate.of(vector), Tensors.vector(2, 5, 6, 6));
  }

  @Test
  public void testProd() {
    Tensor vector = Tensors.vector(2, 3, -1, 0);
    assertEquals(Accumulate.prod(vector), Tensors.vector(2, 6, -6, 0));
  }

  @Test
  public void testMatrixAdd() {
    Tensor matrix = Tensors.matrix(new Number[][] { { 1, 2 }, { 5, 5 }, { -3, -9 } });
    Tensor actual = Accumulate.of(matrix);
    Tensor expected = Tensors.matrix(new Number[][] { { 1, 2 }, { 6, 7 }, { 3, -2 } });
    assertEquals(expected, actual);
  }

  @Test
  public void testMatrixProd() {
    Tensor matrix = Tensors.matrix(new Number[][] { { 1, 2 }, { 5, 5 }, { -3, -9 } });
    assertEquals(Accumulate.prod(matrix), Tensors.fromString("{{1, 2}, {5, 10}, {-15, -90}}"));
  }

  @Test
  public void testProdAd() {
    Tensor tensor = Accumulate.prod(Array.zeros(3, 3, 3));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 3, 3));
  }

  @Test
  public void testScalarFail() {
    AssertFail.of(() -> Accumulate.of(RealScalar.ONE));
  }

  @Test
  public void testScalarProdFail() {
    AssertFail.of(() -> Accumulate.prod(RealScalar.ONE));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> Accumulate.of(null));
  }

  @Test
  public void testNullProdFail() {
    AssertFail.of(() -> Accumulate.prod(null));
  }
}
