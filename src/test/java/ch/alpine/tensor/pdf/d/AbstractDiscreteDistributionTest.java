// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class AbstractDiscreteDistributionTest {
  @Test
  void testVisibility() {
    assertTrue(Modifier.isPublic(AbstractDiscreteDistribution.class.getModifiers()));
  }
}
