// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;

class OrderingTest {
  @Test
  void testVector() throws ClassNotFoundException, IOException {
    Tensor vector = Tensors.vector(4, 2, 3, 0, 1);
    int[] array = Serialization.copy(Ordering.INCREASING).of(vector);
    Tensor ascending = Tensor.of( //
        IntStream.range(0, array.length).mapToObj(index -> vector.Get(array[index])));
    assertEquals(ascending, Sort.of(vector));
  }

  @Test
  void testRandomExact() {
    Distribution d = BinomialDistribution.of(12, RationalScalar.of(1, 3));
    Tensor vector = RandomVariate.of(d, 100);
    int[] array = Ordering.INCREASING.of(vector);
    Tensor ascending = Tensor.of( //
        IntStream.range(0, array.length).mapToObj(index -> vector.Get(array[index])));
    assertEquals(ascending, Sort.of(vector));
  }

  @Test
  void testRandomNumeric() {
    Distribution d = LogNormalDistribution.standard();
    Tensor vector = RandomVariate.of(d, 100);
    int[] array = Ordering.INCREASING.of(vector);
    Tensor ascending = Tensor.of( //
        IntStream.range(0, array.length).mapToObj(index -> vector.Get(array[index])));
    assertEquals(ascending, Sort.of(vector));
  }

  @Test
  void testRandomNumericDecreasing() {
    Distribution d = CauchyDistribution.standard();
    Tensor vector = RandomVariate.of(d, 100);
    int[] array = Ordering.DECREASING.of(vector);
    Tensor decreasing = Tensor.of( //
        IntStream.range(0, array.length).mapToObj(index -> vector.Get(array[index])));
    assertEquals(Reverse.of(decreasing), Sort.of(vector));
  }

  @Test
  void testEnum() {
    assertEquals(Ordering.valueOf("INCREASING"), Ordering.INCREASING);
    assertEquals(Ordering.valueOf("DECREASING"), Ordering.DECREASING);
  }

  @Test
  void testSerializable() throws Exception {
    Ordering a = Ordering.DECREASING;
    Ordering b = Serialization.copy(a);
    assertEquals(a, b);
  }

  @Test
  void testSingleton() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}}");
    int[] array = Ordering.DECREASING.of(tensor);
    assertEquals(array.length, 1);
    assertEquals(array[0], 0);
  }

  @Test
  void testIntegerToTensor() {
    Integer[] a = { 2, 3, 4 };
    assertEquals(Tensors.vector(a), Tensors.vector(1, 2, 3).map(RealScalar.ONE::add));
  }

  @Test
  void testMatrix() {
    assertArrayEquals(Ordering.INCREASING.of(HilbertMatrix.of(4)), new int[] { 3, 2, 1, 0 });
    assertArrayEquals(Ordering.DECREASING.of(HilbertMatrix.of(4)), new int[] { 0, 1, 2, 3 });
  }

  @Test
  void testIndex() {
    Tensor vector = Tensors.vector(2, 5, 4, 1, 0, 3);
    int[] array = Ordering.INCREASING.of(vector);
    assertEquals(Tensors.vectorInt(array), Tensors.vector(4, 3, 0, 5, 2, 1));
    int[] inverse = new int[vector.length()];
    for (int i = 0; i < vector.length(); ++i)
      inverse[vector.Get(i).number().intValue()] = i;
    assertEquals(Tensors.vectorInt(inverse), Tensors.vector(4, 3, 0, 5, 2, 1));
  }

  @Test
  void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Ordering.INCREASING.of(Pi.HALF));
  }
}
