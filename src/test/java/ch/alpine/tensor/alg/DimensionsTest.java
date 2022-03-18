// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;

public class DimensionsTest {
  @Test
  public void testScalar() {
    assertTrue(Dimensions.of(DoubleScalar.of(0.123)).isEmpty());
  }

  @Test
  public void testEmpty() {
    assertEquals(Dimensions.of(Tensors.empty()), Arrays.asList(0));
  }

  @Test
  public void testVectors() {
    Tensor a = Tensors.vectorLong(1, 2, 3);
    assertEquals(Dimensions.of(a), Arrays.asList(3));
    Tensor b = Tensors.vectorLong(1, 2, 2);
    Tensor e = Tensors.of(a, b);
    assertEquals(Dimensions.of(e), Arrays.asList(2, 3));
  }

  @Test
  public void testDimensions4() {
    Tensor a = Tensors.vectorLong(1, 2);
    Tensor b = Tensors.vectorLong(3, 4, 5);
    Tensor c = Tensors.vectorLong(6);
    Tensor d = Tensors.of(a, b, c);
    Tensor e = Tensors.of(a, b);
    Tensor f = Tensors.of(d, e);
    assertEquals(Dimensions.of(f), Arrays.asList(2));
    Tensor g = Tensors.of(d, d, d, d);
    assertEquals(Dimensions.of(g), Arrays.asList(4, 3));
  }

  @Test
  public void testDimensions5() {
    Tensor a = DoubleScalar.of(2.32123);
    Tensor b = Tensors.vectorLong(3, 2);
    Tensor c = DoubleScalar.of(1.23);
    Tensor d = Tensors.of(a, b, c);
    assertEquals(Dimensions.of(d), Arrays.asList(3));
  }

  @Test
  public void testIsEmpty() {
    assertTrue(Tensors.isEmpty(Tensors.empty()));
    assertFalse(Tensors.isEmpty(RealScalar.ONE));
    assertFalse(Tensors.isEmpty(Tensors.vector(3, 4)));
  }

  @Test
  public void testDepth() {
    assertEquals(new Dimensions(RealScalar.ONE).maxDepth(), 0);
    assertEquals(new Dimensions(UnitVector.of(3, 2)).maxDepth(), 1);
    assertEquals(new Dimensions(HilbertMatrix.of(2, 3)).maxDepth(), 2);
    Tensor tensor = Tensors.fromString("{{{2, 3}, {{}}}, {4, 5, 7}, 3}");
    assertEquals(new Dimensions(tensor).maxDepth(), 3);
  }

  @Test
  public void testLengths() throws ClassNotFoundException, IOException {
    Tensor tensor = Tensors.fromString("{{{2, 3}, {{}}}, {4, 5, 7}, 3}");
    Dimensions dimensions = Serialization.copy(new Dimensions(tensor));
    assertEquals(dimensions.lengths(0), new HashSet<>(Arrays.asList(3)));
    assertEquals(dimensions.lengths(1), new HashSet<>(Arrays.asList(Scalar.LENGTH, 2, 3)));
    assertEquals(dimensions.lengths(2), new HashSet<>(Arrays.asList(Scalar.LENGTH, 1, 2)));
    assertEquals(dimensions.lengths(3), new HashSet<>(Arrays.asList(Scalar.LENGTH, 0)));
  }

  @Test
  public void testIntEquality() {
    List<Integer> list = Dimensions.of(HilbertMatrix.of(3));
    assertTrue(list.get(0) == list.get(1));
  }

  @Test
  public void testScalar2() {
    Dimensions dimensions = new Dimensions(Pi.VALUE);
    assertTrue(dimensions.isArray());
    assertEquals(dimensions.list(), Arrays.asList());
  }

  @Test
  public void testLengthsFail() {
    Tensor tensor = Tensors.fromString("{{{2, 3}, {{}}}, {4, 5, 7}, 3}");
    Dimensions dimensions = new Dimensions(tensor);
    AssertFail.of(() -> dimensions.lengths(-1));
    dimensions.lengths(dimensions.maxDepth());
    AssertFail.of(() -> dimensions.lengths(dimensions.maxDepth() + 1));
    AssertFail.of(() -> dimensions.lengths(0).add(1));
  }
}
