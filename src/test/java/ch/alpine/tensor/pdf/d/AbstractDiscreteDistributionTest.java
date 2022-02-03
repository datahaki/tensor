// code by jph
package ch.alpine.tensor.pdf.d;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class AbstractDiscreteDistributionTest extends TestCase {
  public void testVisibility() {
    assertTrue(Modifier.isPublic(AbstractDiscreteDistribution.class.getModifiers()));
  }
}
