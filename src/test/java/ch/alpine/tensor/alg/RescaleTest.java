// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class RescaleTest {
  @Test
  void testEmpty() {
    assertEquals(Rescale.of(Tensors.empty()), Tensors.empty());
  }

  @Test
  void testEqual() {
    assertEquals(Rescale.of(Tensors.vector(2, 2, 2, 2)), Array.zeros(4));
  }

  @Test
  void testVector() {
    Tensor res = Rescale.of(Tensors.vector(-0.7, 0.5, 1.2, 5.6, 1.8));
    Tensor sol = Tensors.vector(0., 0.190476, 0.301587, 1., 0.396825);
    assertEquals(res.subtract(sol).maps(Chop._05), Array.zeros(5));
  }

  @Test
  void testMatrix() {
    assertEquals(Rescale.of(Tensors.fromString("{{2, 2, 2}, {2, 2}}")), Tensors.fromString("{{0, 0, 0}, {0, 0}}"));
    assertEquals(Rescale.of(Tensors.fromString("{{1, 2, 3}}")), Tensors.fromString("{{0, 1/2, 1}}"));
    assertEquals(Rescale.of(Tensors.fromString("{{1, 2, 3}, {}}")), Tensors.fromString("{{0, 1/2, 1}, {}}"));
    assertEquals(Rescale.of(Tensors.fromString("{{-1}, {2, 3}}")), Tensors.fromString("{{0}, {3/4, 1}}"));
    assertEquals(Rescale.of(Tensors.fromString("{{10, 20, 30}}")), Tensors.fromString("{{0, 1/2, 1}}"));
  }

  @Test
  void testInfty() {
    Tensor vec = Tensors.vector(-0.7, 0.5, 1.2, Double.POSITIVE_INFINITY, 1.8);
    Tensor res = Rescale.of(vec);
    assertTrue(2 < Tally.of(res).size());
  }

  @Test
  void testAllInfty() {
    Tensor tensor = Tensors.vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    Tensor scaled = Rescale.of(tensor);
    assertEquals(tensor, scaled);
  }

  @Test
  void testAllInfty2() {
    Tensor tensor = Tensors.vector(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    Tensor scaled = Rescale.of(tensor);
    assertEquals(tensor, scaled);
  }

  @Test
  void testMixed1() {
    Tensor tensor = Tensors.vector(Double.NaN, Double.POSITIVE_INFINITY, 0, 1, 3, Double.NaN);
    Tensor scaled = Rescale.of(tensor);
    assertEquals(scaled.toString(), "{NaN, Infinity, 0, 1/3, 1, NaN}");
  }

  @Test
  void testMixed2() {
    Tensor tensor = Tensors.vector(Double.NaN, 0, 0, 0, Double.NaN);
    Tensor scaled = Rescale.of(tensor);
    assertEquals(tensor.toString(), scaled.toString());
  }

  @Test
  void testQuantity() {
    Tensor vector = QuantityTensor.of(Tensors.vector(1, 2, -3, 4, -1, 2, 1, 0, 3, 2, 1, 2), Unit.of("s"));
    Tensor rescal = Rescale.of(vector);
    Tensor expect = Tensors.fromString("{4/7, 5/7, 0, 1, 2/7, 5/7, 4/7, 3/7, 6/7, 5/7, 4/7, 5/7}");
    assertEquals(rescal, expect);
    assertEquals(rescal.toString(), expect.toString());
  }

  @Test
  void testQuantityEx() {
    Tensor vector = Tensors.fromString("{3[s], Infinity[s], 6[s], 2[s]}");
    Tensor result = Tensors.fromString("{1/4, Infinity, 1, 0}");
    assertEquals(Rescale.of(vector), result);
  }

  @Test
  void testQuantityExStats() {
    Tensor vector = Tensors.fromString("{3[s], Infinity[s], 6[s], 2[s]}");
    Tensor result = Tensors.fromString("{1/4, Infinity, 1, 0}");
    Rescale rescale = new Rescale(vector);
    Clip scalarSummaryStatistics = rescale.clip();
    // assertEquals(scalarSummaryStatistics.count(), 3);
    assertEquals(scalarSummaryStatistics.min(), Quantity.of(2, "s"));
    assertEquals(scalarSummaryStatistics.max(), Quantity.of(6, "s"));
    assertEquals(rescale.result(), result);
  }

  @Test
  void testQuantitySpecial() {
    Tensor vector = QuantityTensor.of(Tensors.vector(3, Double.POSITIVE_INFINITY, 3), Unit.of("s"));
    Tensor rescal = Rescale.of(vector);
    assertEquals(rescal.Get(0).toString(), "0");
    assertEquals(rescal.Get(2).toString(), "0");
  }

  @Test
  void testDateTime() {
    Scalar a1 = DateTime.of(2020, 3, 4, 5, 2);
    Scalar a2 = DateTime.of(2021, 10, 14, 2, 32);
    Scalar a3 = DateTime.of(2020, 8, 9, 22, 17);
    Tensor tensor = Rescale.of(Tensors.of(a1, a2, a3));
    assertEquals(tensor, Tensors.fromString("{0, 1, 15237/56534}"));
    ExactTensorQ.require(tensor);
    tensor.stream().map(Scalar.class::cast).allMatch(Clips.unit()::isInside);
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> Rescale.of(RealScalar.ONE));
  }

  @Test
  void testQuantityMixedFail() {
    Tensor vector = Tensors.of(Quantity.of(1, "s"), Quantity.of(2, "m"));
    assertThrows(Throw.class, () -> Rescale.of(vector));
  }

  @Test
  void testMixedFail() {
    Tensor vector = Tensors.fromString("{3[s], Infinity, 6[s], 2[s]}");
    assertThrows(Exception.class, () -> new Rescale(vector));
  }

  @Test
  void testQuantityZeroFail() {
    Tensor vector = Tensors.of(Quantity.of(0, ""), Quantity.of(0, "m"));
    assertThrows(Throw.class, () -> Rescale.of(vector));
  }

  @Test
  void testComplexFail() {
    Tensor vector = Tensors.fromString("{2+I, 1+2*I}");
    assertThrows(ClassCastException.class, () -> Rescale.of(vector));
  }
}
