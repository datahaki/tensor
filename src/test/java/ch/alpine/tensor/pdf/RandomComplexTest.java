// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

class RandomComplexTest {
  @Test
  void testSimple() {
    Scalar scalar = RandomComplex.of();
    assertInstanceOf(ComplexScalar.class, scalar);
    Clips.unit().requireInside(Re.FUNCTION.apply(scalar));
    Clips.unit().requireInside(Im.FUNCTION.apply(scalar));
  }
}
