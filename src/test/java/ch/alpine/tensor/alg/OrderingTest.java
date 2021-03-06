// code by jph
package ch.alpine.tensor.alg;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.BinomialDistribution;
import ch.alpine.tensor.pdf.CauchyDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.LogNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class OrderingTest extends TestCase {
  public void testVector() throws ClassNotFoundException, IOException {
    Tensor vector = Tensors.vector(4, 2, 3, 0, 1);
    int[] array = Serialization.copy(Ordering.INCREASING).of(vector);
    Tensor ascending = Tensor.of( //
        IntStream.range(0, array.length).mapToObj(index -> vector.Get(array[index])));
    assertEquals(ascending, Sort.of(vector));
  }

  public void testRandomExact() {
    Distribution d = BinomialDistribution.of(12, RationalScalar.of(1, 3));
    Tensor vector = RandomVariate.of(d, 100);
    int[] array = Ordering.INCREASING.of(vector);
    Tensor ascending = Tensor.of( //
        IntStream.range(0, array.length).mapToObj(index -> vector.Get(array[index])));
    assertEquals(ascending, Sort.of(vector));
  }

  public void testRandomNumeric() {
    Distribution d = LogNormalDistribution.standard();
    Tensor vector = RandomVariate.of(d, 100);
    int[] array = Ordering.INCREASING.of(vector);
    Tensor ascending = Tensor.of( //
        IntStream.range(0, array.length).mapToObj(index -> vector.Get(array[index])));
    assertEquals(ascending, Sort.of(vector));
  }

  public void testRandomNumericDecreasing() {
    Distribution d = CauchyDistribution.standard();
    Tensor vector = RandomVariate.of(d, 100);
    int[] array = Ordering.DECREASING.of(vector);
    Tensor decreasing = Tensor.of( //
        IntStream.range(0, array.length).mapToObj(index -> vector.Get(array[index])));
    assertEquals(Reverse.of(decreasing), Sort.of(vector));
  }

  public void testEnum() {
    assertEquals(Ordering.valueOf("INCREASING"), Ordering.INCREASING);
    assertEquals(Ordering.valueOf("DECREASING"), Ordering.DECREASING);
  }

  public void testSerializable() throws Exception {
    Ordering a = Ordering.DECREASING;
    Ordering b = Serialization.copy(a);
    assertEquals(a, b);
  }

  public void testSingleton() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}}");
    int[] array = Ordering.DECREASING.of(tensor);
    assertEquals(array.length, 1);
    assertEquals(array[0], 0);
  }

  public void testIntegerToTensor() {
    Integer[] a = { 2, 3, 4 };
    assertEquals(Tensors.vector(a), Tensors.vector(1, 2, 3).map(RealScalar.ONE::add));
  }

  public void testMatrix() {
    assertTrue(Arrays.equals(Ordering.INCREASING.of(HilbertMatrix.of(4)), new int[] { 3, 2, 1, 0 }));
    assertTrue(Arrays.equals(Ordering.DECREASING.of(HilbertMatrix.of(4)), new int[] { 0, 1, 2, 3 }));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Ordering.INCREASING.of(Pi.HALF));
  }
}
