// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.DateTime;

class InterpolationTest {
  @Test
  void test() {
    Tensor row1 = Tensors.of(DateTime.of(1980, Month.APRIL, 1, 12, 30), Pi.VALUE, RationalScalar.HALF);
    Tensor row2 = Tensors.of(DateTime.of(2020, Month.JANUARY, 19, 18, 3), Pi.TWO, RationalScalar.of(3, 2));
    Tensor row3 = Tensors.of(DateTime.of(1990, Month.DECEMBER, 24, 0, 0), RealScalar.ZERO, RationalScalar.of(7, 2));
    Tensor data = Tensors.of(row1, row2, row3);
    Interpolation interpolation = LinearInterpolation.of(data);
    {
      Tensor tensor = interpolation.at(RealScalar.ZERO);
      assertEquals(tensor, row1);
    }
    {
      Tensor tensor = interpolation.at(RationalScalar.HALF);
      VectorQ.requireLength(tensor, 3);
    }
    {
      Tensor tensor = interpolation.at(RealScalar.ONE);
      assertEquals(tensor, row2);
    }
    {
      Tensor tensor = interpolation.at(RealScalar.TWO);
      assertEquals(tensor, row3);
    }
  }
}
