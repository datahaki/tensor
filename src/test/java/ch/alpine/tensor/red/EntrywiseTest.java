// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class EntrywiseTest {
  @Test
  void testMax() {
    Entrywise entrywise = Entrywise.with(Max::of);
    Tensor result = entrywise.of(Tensors.of( //
        Tensors.vector(3, 2, 3), Tensors.vector(-2, 1, 4), Tensors.vector(-3, 4, 0)));
    assertEquals(result, Tensors.vector(3, 4, 4));
  }

  @Test
  void testHelpOf() {
    assertEquals(Entrywise.with(Max::of).of(Tensors.of(Tensors.vector(1, 2, 3), Tensors.vector(5, 0, 4))), Tensors.vector(5, 2, 4));
    assertEquals(Entrywise.with(Min::of).of(Tensors.of(Tensors.vector(1, 2, 3), Tensors.vector(5, 0, 4))), Tensors.vector(1, 0, 3));
  }

  @Test
  void testStreamReduce() {
    Tensor box = Tensors.fromString("{{0, 7}, {0, 8}, {1, 5}, {2, 7}}");
    Tensor max = box.stream().reduce(Entrywise.max()).get();
    Tensor min = box.stream().reduce(Entrywise.min()).get();
    assertEquals(max, Tensors.vector(2, 8));
    assertEquals(min, Tensors.vector(0, 5));
  }

  @Test
  void testMaxSimple() {
    Entrywise entrywise = Entrywise.max();
    Tensor result = entrywise.apply( //
        Tensors.vector(3, 2, 3), Tensors.vector(-2, 1, 4));
    assertEquals(result, Tensors.vector(3, 2, 4));
  }

  @Test
  void testMinSimple() throws ClassNotFoundException, IOException {
    Entrywise entrywise = Serialization.copy(Entrywise.min());
    Tensor result = entrywise.apply( //
        Tensors.vector(3, 2, 3), Tensors.vector(-2, 1, 4));
    assertEquals(result, Tensors.vector(-2, 1, 3));
  }

  @Test
  void testMaxScalar() throws ClassNotFoundException, IOException {
    Entrywise entrywise = Serialization.copy(Entrywise.max());
    Tensor result = entrywise.apply( //
        RealScalar.of(3), RealScalar.of(5));
    assertEquals(result, RealScalar.of(5));
  }

  @Test
  void testMinScalar() {
    Entrywise entrywise = Entrywise.min();
    Tensor result = entrywise.apply( //
        RealScalar.of(3), RealScalar.of(5));
    assertEquals(result, RealScalar.of(3));
  }

  @Test
  void testSingle() {
    Tensor single = Tensors.vector(3, 2, 3);
    Entrywise entrywise = Entrywise.with(Max::of);
    Tensor result = entrywise.of(single);
    assertEquals(result, RealScalar.of(3));
  }

  @Test
  void testAdd() {
    Distribution distribution = UniformDistribution.unit();
    Tensor a = RandomVariate.of(distribution, 7, 9);
    Tensor b = RandomVariate.of(distribution, 7, 9);
    Tensor c = RandomVariate.of(distribution, 7, 9);
    Tensor res = Entrywise.with(Scalar::add).of(Tensors.of(a, b, c));
    assertEquals(res, a.add(b).add(c));
  }

  @Test
  void testMultiply() {
    Distribution distribution = UniformDistribution.unit();
    Tensor a = RandomVariate.of(distribution, 7, 9);
    Tensor b = RandomVariate.of(distribution, 7, 9);
    Tensor c = RandomVariate.of(distribution, 7, 9);
    Tensor res = Entrywise.with(Scalar::multiply).of(Tensors.of(a, b, c));
    assertEquals(res, Times.of(Times.of(a, b), c));
  }

  @Test
  void testEmpty() {
    Entrywise entrywise = Entrywise.with(Max::of);
    assertThrows(NoSuchElementException.class, () -> entrywise.of(Tensors.empty()));
  }

  @Test
  void testFail() {
    Entrywise entrywise = Entrywise.max();
    assertThrows(IllegalArgumentException.class, () -> entrywise.apply(Tensors.vector(3, 2, 3), Tensors.vector(-2, 1)));
  }

  @Test
  void testScalarTensorFail() {
    Entrywise entrywise = Entrywise.max();
    assertThrows(Throw.class, () -> entrywise.apply(Tensors.vector(3, 2, 3), RealScalar.ONE));
    assertThrows(ClassCastException.class, () -> entrywise.apply(RealScalar.ONE, Tensors.vector(3, 2, 3)));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> Entrywise.with(null));
  }
}
