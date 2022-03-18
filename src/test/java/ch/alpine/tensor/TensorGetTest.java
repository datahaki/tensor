// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

public class TensorGetTest {
  @Test
  public void testGetEmpty() {
    assertEquals(Tensors.empty().get(), Tensors.empty());
    assertEquals(Tensors.empty().get(new int[] {}), Tensors.empty());
    assertEquals(Array.zeros(2, 3).get(), Array.zeros(2, 3));
    assertEquals(Array.zeros(2, 3).get(new int[] {}), Array.zeros(2, 3));
  }

  @Test
  public void testGetScalar() {
    assertTrue(IdentityMatrix.of(10).Get(3, 4) instanceof RealScalar);
  }

  @Test
  public void testGet() {
    Tensor matrix = Tensors.matrixInt( //
        new int[][] { { 3, 4 }, { 1, 2 }, { 9, 8 } });
    assertEquals(matrix.get(0, 0), Tensors.fromString("3"));
    assertEquals(matrix.get(1, 1), Tensors.fromString("2"));
    assertEquals(matrix.get(0), Tensors.fromString("{3, 4}"));
    assertEquals(matrix.get(1), Tensors.fromString("{1, 2}"));
    assertEquals(matrix.get(2), Tensors.fromString("{9, 8}"));
    assertEquals(matrix.get(Tensor.ALL, 0), Tensors.fromString("{3, 1, 9}"));
    assertEquals(matrix.get(Tensor.ALL, 1), Tensors.fromString("{4, 2, 8}"));
  }

  @Test
  public void testGetAllSimple() {
    Tensor a = Array.zeros(3, 4);
    Tensor c = a.get(Tensor.ALL, 2);
    c.set(RealScalar.ONE, 1);
    assertEquals(a, Array.zeros(3, 4));
  }

  @Test
  public void testGetAll() {
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(), 2, 3, 4);
    assertEquals(matrix.get(), matrix);
    assertEquals(matrix.get(new int[] { Tensor.ALL }), matrix);
    assertEquals(matrix.get(Tensor.ALL, Tensor.ALL), matrix);
    assertEquals(matrix.get(Tensor.ALL, Tensor.ALL, Tensor.ALL), matrix);
  }

  @Test
  public void testGetAll1() {
    Tensor a = Array.zeros(3).unmodifiable();
    a.get().set(RealScalar.ONE, 1);
    // a.get(1).set(RealScalar.ONE, 1); // scalar is immutable
    Tensor b = a.get(new int[] { Tensor.ALL });
    b.set(Tensors.vector(1, 2, 3), Tensor.ALL);
    assertEquals(b, Tensors.vector(1, 2, 3));
    assertEquals(a, Array.zeros(3));
  }

  @Test
  public void testGetAll2() {
    Tensor matrix = Array.zeros(3, 4).unmodifiable();
    matrix.get().set(RealScalar.ONE, 1);
    matrix.get(1).set(RealScalar.ONE, 1);
    matrix.get(new int[] { Tensor.ALL }).set(Tensors.vector(1, 2, 3), Tensor.ALL);
    matrix.get(Tensor.ALL, 2).set(RealScalar.ONE, 1);
    matrix.get(2, Tensor.ALL).set(Tensors.vector(1, 2, 3, 4), Tensor.ALL);
    matrix.get(Tensor.ALL, Tensor.ALL).set(Array.of(l -> RealScalar.ONE, 3, 4), Tensor.ALL, Tensor.ALL);
    assertEquals(matrix, Array.zeros(3, 4));
  }

  @Test
  public void testGetAll3() {
    Tensor tensor = Array.zeros(3, 4, 3).unmodifiable();
    tensor.get().set(RealScalar.ONE, 1);
    tensor.get(1).set(RealScalar.ONE, 1);
    tensor.get(new int[] { Tensor.ALL }).set(Tensors.vector(1, 2, 3), Tensor.ALL);
    tensor.get(Tensor.ALL, 2).set(RealScalar.ONE, 1);
    tensor.get(2, Tensor.ALL).set(Tensors.vector(1, 2, 3, 4), Tensor.ALL);
  }

  @Test
  public void testGetAllFail() {
    Tensor matrix = Array.zeros(3, 4, 5);
    assertThrows(IndexOutOfBoundsException.class, () -> matrix.Get(Tensor.ALL));
  }
}
