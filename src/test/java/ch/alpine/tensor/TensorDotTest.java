// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.chq.ExactTensorQ;

class TensorDotTest {
  @Test
  void testDotEmpty() {
    Tensor a = Tensors.empty().dot(Tensors.empty());
    assertInstanceOf(Scalar.class, a);
    assertEquals(a, RealScalar.ZERO);
    assertEquals(a, DoubleScalar.of(0));
    assertEquals(RealScalar.ZERO, a);
    assertEquals(DoubleScalar.of(0), a);
  }

  @Test
  void testDot2() {
    Tensor tensor = Tensors.of(Tensors.empty());
    Tensor sca = tensor.dot(Tensors.empty());
    ExactTensorQ.require(sca);
    assertEquals(sca, Tensors.vectorDouble(0));
  }

  @Test
  void testDot3() {
    Tensor tensor = Tensors.of(Tensors.empty(), Tensors.empty());
    Tensor sca = tensor.dot(Tensors.empty());
    assertEquals(sca, Tensors.vectorLong(0, 0));
  }

  @Test
  void testDot4() {
    Tensor c = Tensors.vectorLong(1, 2, 6);
    Tensor d = Tensors.vectorLong(3, 4, 5);
    assertInstanceOf(RationalScalar.class, c.dot(d));
    assertEquals(c.dot(d), RationalScalar.of(3 + 8 + 30, 1));
  }

  @Test
  void testDot5() {
    Tensor c = Tensors.vectorDouble(1, 2, 6.);
    Tensor d = Tensors.vectorLong(3, 4, 5);
    assertInstanceOf(DoubleScalar.class, c.dot(d));
    assertEquals(c.dot(d), RationalScalar.of(3 + 8 + 30, 1));
  }

  @Test
  void testDot6() {
    Tensor a = Tensors.vectorLong(7, 2);
    Tensor b = Tensors.vectorLong(3, 4);
    Tensor c = Tensors.vectorLong(2, 2);
    Tensor d = Tensors.of(a, b, c);
    Tensor e = Tensors.vectorLong(-1, 1);
    Tensor f = d.dot(e);
    Tensor g = Tensors.vectorLong(-7 + 2, -3 + 4, -2 + 2);
    assertEquals(f, g);
  }

  @Test
  void testDotIrregularExample() {
    Tensor a = Tensors.vector(1, 2);
    Tensor b = Tensors.fromString("{{3, {4}}, {5, {6}}}");
    assertEquals(a.dot(b), Tensors.fromString("{13, {16}}"));
  }

  @Test
  void testDotIrregularRight() {
    Tensor a = Tensors.vector(1, 2, 3);
    Tensor b = Tensors.fromString("{{1, {2}}, {2, {3}}, {4, {5}}}");
    assertEquals(a.dot(b), Tensors.fromString("{17, {23}}"));
  }

  @Test
  void testDotIrregularLeft() {
    Tensor a = Tensors.fromString("{{1, 2, 3}, {{2, 3, 4}, {5, 6, 7}}}");
    Tensor b = Tensors.vector(4, 5, 6);
    Tensor c = a.dot(b);
    assertEquals(c, Tensors.fromString("{32, {47, 92}}"));
    ExactTensorQ.require(c);
  }
}
