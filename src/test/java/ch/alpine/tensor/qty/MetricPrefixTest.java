// code by jph
package ch.alpine.tensor.qty;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class MetricPrefixTest extends TestCase {
  public void testGiga() {
    assertEquals(MetricPrefix.GIGA.prefix(), "G");
    assertEquals(MetricPrefix.GIGA.english(), "Giga");
    assertEquals(MetricPrefix.GIGA.factor(), RealScalar.of(1_000_000_000));
  }

  public void testHecto() {
    assertEquals(MetricPrefix.HECTO.prefix(), "h");
    assertEquals(MetricPrefix.HECTO.english(), "Hecto");
    assertEquals(MetricPrefix.HECTO.factor(), RationalScalar.of(100, 1));
  }

  public void testMicro() {
    assertEquals(MetricPrefix.MICRO.prefix(), "u");
    assertEquals(MetricPrefix.MICRO.english(), "Micro");
    assertEquals(MetricPrefix.MICRO.factor(), RationalScalar.of(1, 1_000_000));
  }

  public void testUnique() {
    int n = MetricPrefix.values().length;
    assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::english).distinct().count(), n);
    assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::factor).distinct().count(), n);
    assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::prefix).distinct().count(), n);
  }

  public void testExact() {
    Stream.of(MetricPrefix.values()).map(MetricPrefix::factor).forEach(ExactScalarQ::require);
  }

  public void testPackage() {
    assertFalse(Modifier.isPublic(MetricPrefix.class.getModifiers()));
  }
}
