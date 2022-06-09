// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;

class FlattenTest {
  @Test
  public void testLevel0a() {
    Tensor m = HilbertMatrix.of(5, 4);
    assertEquals(Flatten.of(m, 0), m);
  }

  @Test
  public void testLevel0b() {
    Tensor m = Tensors.fromString("{{0, 1, {2, {3}}}, {{4}, 5}}");
    assertEquals(Flatten.of(m, 0), m);
  }

  @Test
  public void testLevels() {
    Tensor ad = Array.zeros(3, 3, 3);
    assertEquals(Flatten.of(ad, 0), ad);
    assertEquals(Dimensions.of(Flatten.of(ad, 1)), Arrays.asList(9, 3));
    assertEquals(Dimensions.of(Flatten.of(ad, 2)), Arrays.asList(27));
    assertEquals(Dimensions.of(Flatten.of(ad, 3)), Arrays.asList(27));
    assertEquals(Dimensions.of(Flatten.of(ad)), Arrays.asList(27));
  }

  @Test
  public void testAll() {
    assertEquals(Flatten.of(Tensors.fromString("{{0, 1, {{2}, 3}}, {4, 5}}")), Range.of(0, 6));
  }

  @Test
  public void testScalar() {
    assertEquals(Flatten.of(RealScalar.of(3)), Tensors.vector(3));
    assertEquals(Flatten.of(RealScalar.of(3), 4), Tensors.vector(3));
  }

  @Test
  public void testExcess() {
    Tensor ad = Array.zeros(3, 4, 5);
    Tensor tensor = Flatten.of(ad, 10);
    assertEquals(tensor.length(), Numel.of(ad));
  }

  @Test
  public void testVarargs() {
    Tensor res = Flatten.of(Tensors.vector(1, 2, 3), RealScalar.of(4), Tensors.fromString("{{5}, 6, {{7}, 8}}"));
    assertEquals(res, Range.of(1, 9));
  }

  @Test
  public void testReferences0() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4}}");
    Tensor flatten = Flatten.of(tensor, 0);
    flatten.set(RealScalar.ZERO, 0, 0);
    assertEquals(tensor, Tensors.fromString("{{1, 2}, {3, 4}}"));
  }

  @Test
  public void testReferences1() {
    Tensor tensor = Tensors.fromString("{{{1, 2}, {3, 4}}}");
    Tensor flatten = Flatten.of(tensor, 1);
    flatten.set(RealScalar.ZERO, 0, 0);
    assertEquals(tensor, Tensors.fromString("{{{1, 2}, {3, 4}}}"));
  }
}
