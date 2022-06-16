// code by jph
package ch.alpine.tensor.lie.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Chop;

class CirclePointTest {
  @Test
  void testNonPublic() {
    assertFalse(Modifier.isPublic(CirclePoint.class.getModifiers()));
  }

  @Test
  void testExact() {
    for (int count = 0; count < 12; ++count) {
      Scalar scalar = RationalScalar.of(count, 12);
      Optional<Tensor> optional = CirclePoint.INSTANCE.turns(scalar);
      assertTrue(optional.isPresent());
      Tensor vector = optional.get();
      assertTrue(0 < vector.stream().map(Scalar.class::cast).filter(ExactScalarQ::of).count());
      Chop._14.requireClose(vector, AngleVector.of(scalar.multiply(Pi.TWO)));
    }
  }

  @Test
  void testModify() {
    Optional<Tensor> o1 = CirclePoint.INSTANCE.turns(RealScalar.ZERO);
    assertEquals(o1.get(), UnitVector.of(2, 0));
    o1.get().set(RealScalar.of(3), 0);
    Optional<Tensor> o2 = CirclePoint.INSTANCE.turns(RealScalar.ZERO);
    assertEquals(o2.get(), UnitVector.of(2, 0));
  }
}
