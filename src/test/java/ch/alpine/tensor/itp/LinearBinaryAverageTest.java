// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.jet.DateObject;

class LinearBinaryAverageTest {
  @Test
  void testSimple() {
    Tensor tensor = LinearBinaryAverage.INSTANCE.split(UnitVector.of(3, 1), UnitVector.of(3, 2), RationalScalar.of(1, 3));
    assertEquals(ExactTensorQ.require(tensor), Tensors.fromString("{0, 2/3, 1/3}"));
  }

  @Test
  void testDTS() {
    DateObject dt1 = DateObject.of(LocalDateTime.of(2020, 12, 20, 4, 30));
    DateObject dt2 = DateObject.of(LocalDateTime.of(2020, 12, 21, 4, 30));
    Tensor split = LinearBinaryAverage.INSTANCE.split(dt1, dt2, RationalScalar.of(1, 3));
    assertInstanceOf(DateObject.class, split);
  }
}
