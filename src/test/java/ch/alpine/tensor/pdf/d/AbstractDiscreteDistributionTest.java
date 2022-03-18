// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

public class AbstractDiscreteDistributionTest {
  @Test
  public void testVisibility() {
    assertTrue(Modifier.isPublic(AbstractDiscreteDistribution.class.getModifiers()));
  }
}
