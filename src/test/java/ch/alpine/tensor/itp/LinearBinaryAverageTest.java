// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.DateTime;

class LinearBinaryAverageTest {
  @Test
  void testSimple() {
    Tensor tensor = LinearBinaryAverage.INSTANCE.split(UnitVector.of(3, 1), UnitVector.of(3, 2), Rational.of(1, 3));
    assertEquals(ExactTensorQ.require(tensor), Tensors.fromString("{0, 2/3, 1/3}"));
  }

  @Test
  void testDTS() {
    DateTime dt1 = DateTime.of(2020, 12, 20, 4, 30);
    DateTime dt2 = DateTime.of(2020, 12, 21, 4, 30);
    Tensor split = LinearBinaryAverage.INSTANCE.split(dt1, dt2, Rational.of(1, 3));
    assertInstanceOf(DateTime.class, split);
    assertEquals(LinearBinaryAverage.INSTANCE.split(dt1, dt2, Rational.of(1, 1)), dt2);
  }

  @Test
  void testNaNInf() {
    Tolerance.CHOP.requireClose(Pi.VALUE, LinearBinaryAverage.INSTANCE.split( //
        DoubleScalar.INDETERMINATE, Pi.VALUE, RealScalar.of(1.0)));
    Tolerance.CHOP.requireClose(Pi.VALUE, LinearBinaryAverage.INSTANCE.split( //
        DoubleScalar.POSITIVE_INFINITY, Pi.VALUE, RealScalar.of(1.0)));
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> LinearBinaryAverage.INSTANCE.split( //
        Tensors.vector(1, 2, 3), //
        Tensors.vector(1, 2), RealScalar.ONE));
  }
}
