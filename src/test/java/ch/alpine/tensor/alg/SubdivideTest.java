// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class SubdivideTest {
  static Tensor compare(Tensor startInclusive, Tensor endInclusive, int n) {
    Integers.requirePositive(n);
    Tensor difference = endInclusive.subtract(startInclusive);
    return Tensor.of(IntStream.rangeClosed(0, n) //
        .mapToObj(count -> startInclusive.add(difference.multiply(RationalScalar.of(count, n)))));
  }

  @Test
  void testSubdivide() {
    Tensor tensor = Subdivide.of(RealScalar.of(10), RealScalar.of(15), 5);
    Tensor result = Tensors.vector(10, 11, 12, 13, 14, 15);
    assertEquals(tensor, result);
    assertEquals(tensor.toString(), result.toString());
  }

  @Test
  void testSubdivideRev() {
    Tensor tensor = Subdivide.of(RealScalar.of(-1), RealScalar.of(-4), 3);
    Tensor result = Tensors.vector(-1, -2, -3, -4);
    assertEquals(tensor, result);
    assertEquals(tensor.toString(), result.toString());
  }

  @Test
  void testSubdivideTensor() {
    Tensor tensor = Subdivide.of(Tensors.vector(10, 5), Tensors.vector(5, 15), 5);
    Tensor result = Tensors.fromString("{{10, 5}, {9, 7}, {8, 9}, {7, 11}, {6, 13}, {5, 15}}");
    assertEquals(tensor, result);
  }

  @Test
  void testSubdivideTensor2() {
    Tensor tensor = Subdivide.of(Tensors.vector(10, 5), Tensors.vector(5, 15), 4);
    Tensor result = Tensors.fromString("{{10, 5}, {35/4, 15/2}, {15/2, 10}, {25/4, 25/2}, {5, 15}}");
    assertEquals(tensor, result);
  }

  @Test
  void testRange() {
    assertEquals(Range.of(1, 11), Subdivide.of(1, 10, 9));
    assertEquals(Reverse.of(Range.of(1, 11)), Subdivide.of(10, 1, 9));
  }

  @Test
  void testQuantity() {
    Tensor tensor = Subdivide.of(Quantity.of(-20, "deg"), Quantity.of(20, "deg"), 4);
    assertEquals(tensor, QuantityTensor.of(Tensors.vector(-20, -10, 0, 10, 20), "deg"));
  }

  @Test
  void testIncrClipInterval() {
    Clip clip = Clips.interval(Quantity.of(+20, "m"), Quantity.of(+40, "m"));
    Tensor tensor = Subdivide.increasing(clip, 4);
    assertEquals(tensor, Tensors.fromString("{20[m], 25[m], 30[m], 35[m], 40[m]}"));
  }

  @Test
  void testIncrClipPoint() {
    Clip clip = Clips.interval(Quantity.of(+20, "m"), Quantity.of(+20, "m"));
    Tensor tensor = Subdivide.increasing(clip, 4);
    assertEquals(tensor, Tensors.fromString("{20[m], 20[m], 20[m], 20[m], 20[m]}"));
  }

  @Test
  void testDecrClipInterval() {
    Clip clip = Clips.interval(Quantity.of(+20, "m"), Quantity.of(+40, "m"));
    Tensor tensor = Subdivide.decreasing(clip, 4);
    assertEquals(tensor, Tensors.fromString("{40[m], 35[m], 30[m], 25[m], 20[m]}"));
  }

  @Test
  void testDecrClipPoint() {
    Clip clip = Clips.interval(Quantity.of(+20, "m"), Quantity.of(+20, "m"));
    Tensor tensor = Subdivide.decreasing(clip, 4);
    assertEquals(tensor, Tensors.fromString("{20[m], 20[m], 20[m], 20[m], 20[m]}"));
  }

  @Test
  void testLength() {
    int n = 5;
    Tensor tensor = Subdivide.of(2, 3, n);
    assertEquals(tensor.length(), n + 1);
  }

  @Test
  void testNumeric() {
    Scalar beg = RealScalar.of(-0.1);
    Scalar end = RealScalar.of(0.3);
    Tensor tensor = Subdivide.of(beg, end, 5);
    Tensor result = compare(beg, end, 5);
    assertEquals(beg, tensor.get(0));
    assertEquals(end, Last.of(tensor));
    assertEquals(RealScalar.of(0.06), tensor.Get(2));
    Chop._16.requireClose(tensor, result);
  }

  @Test
  void testZeroFail() {
    assertThrows(IllegalArgumentException.class, () -> Subdivide.of(RealScalar.of(-2), RealScalar.of(2), 0));
  }

  @Test
  void testNegativeFail() {
    assertThrows(IllegalArgumentException.class, () -> Subdivide.of(RealScalar.of(-2), RealScalar.of(2), -10));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> Subdivide.of(RealScalar.of(2), null, 1));
    assertThrows(NullPointerException.class, () -> Subdivide.of(null, RealScalar.of(2), 1));
  }

  @Test
  void testGaussScalarFail() {
    assertThrows(Throw.class, () -> Subdivide.of(GaussScalar.of(2, 7), GaussScalar.of(1, 7), 2));
  }
}
