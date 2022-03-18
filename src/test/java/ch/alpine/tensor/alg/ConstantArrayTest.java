// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;

public class ConstantArrayTest {
  @Test
  public void testRepmat() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor repmat = ConstantArray.of(vector, 2, 3, 4);
    assertTrue(Tensors.isUnmodifiable(repmat));
    assertEquals(Dimensions.of(repmat), Arrays.asList(2, 3, 4, 3));
    Tensor tensor = repmat.copy();
    assertEquals(repmat, tensor);
    assertEquals(repmat.hashCode(), tensor.hashCode());
    assertFalse(Tensors.isUnmodifiable(tensor));
    tensor.set(RealScalar.ONE::add, Tensor.ALL, Tensor.ALL, Tensor.ALL, Tensor.ALL);
    assertEquals(ConstantArray.of(Tensors.vector(2, 3, 4), 2, 3, 4), tensor);
  }

  @Test
  public void testSingle() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor repmat = ConstantArray.of(vector);
    assertTrue(Tensors.isUnmodifiable(repmat));
    assertEquals(Dimensions.of(repmat), Arrays.asList(3));
    assertEquals(repmat, Range.of(1, 4));
  }

  @Test
  public void testScalar() {
    Tensor repmat = ConstantArray.of(RealScalar.ZERO);
    assertTrue(repmat instanceof Scalar);
  }

  @Test
  public void testScalar1() {
    Tensor repmat = ConstantArray.of(Pi.VALUE, Arrays.asList());
    assertTrue(repmat instanceof Scalar);
  }

  @Test
  public void testZeros() {
    Tensor repmat = ConstantArray.of(RealScalar.ZERO, 2, 4, 1);
    Tensor zeros = Array.zeros(2, 4, 1);
    assertEquals(repmat, zeros);
  }

  @Test
  public void testNCopies() {
    Tensor tensor = ConstantArray.of(Tensors.vector(1, 2, 3), 6);
    assertTrue(Tensors.isUnmodifiable(tensor));
    assertEquals(tensor.length(), 6);
    assertEquals(tensor.get(1), Tensors.vector(1, 2, 3));
    assertEquals(tensor.Get(1, 2), RealScalar.of(3));
    assertThrows(UnsupportedOperationException.class, () -> tensor.set(s -> s, 0, 0));
    assertThrows(UnsupportedOperationException.class, () -> tensor.append(RealScalar.ONE));
  }

  @Test
  public void testNCopiesScalar() {
    Tensor tensor = ConstantArray.of(RealScalar.of(3), 6);
    assertTrue(Tensors.isUnmodifiable(tensor));
    assertEquals(tensor.length(), 6);
    assertEquals(tensor.get(1), RealScalar.of(3));
    assertEquals(tensor.extract(2, 5), Tensors.vector(3, 3, 3));
    assertThrows(UnsupportedOperationException.class, () -> tensor.set(s -> s, 0));
    assertThrows(UnsupportedOperationException.class, () -> tensor.append(RealScalar.ONE));
  }

  @Test
  public void testFailNull() {
    assertThrows(NullPointerException.class, () -> ConstantArray.of(null, 6, 3));
  }

  @Test
  public void testFailNegative() {
    Tensor repmat = ConstantArray.of(RealScalar.ONE, 6, 0, 3);
    assertEquals(Dimensions.of(repmat), Arrays.asList(6, 0));
    assertEquals(repmat, Tensors.fromString("{{}, {}, {}, {}, {}, {}}"));
    assertThrows(IllegalArgumentException.class, () -> ConstantArray.of(RealScalar.ONE, 6, -1, 3));
  }
}
