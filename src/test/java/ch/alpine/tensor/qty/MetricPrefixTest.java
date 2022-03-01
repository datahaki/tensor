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
    assertEquals(MetricPrefix.GIGA.prefix("Hz"), "GHz");
    assertEquals(MetricPrefix.GIGA.english("Hertz"), "Gigahertz");
    assertEquals(MetricPrefix.GIGA.factor(), RealScalar.of(1_000_000_000));
  }

  public void testHecto() {
    assertEquals(MetricPrefix.HECTO.prefix("m"), "hm");
    assertEquals(MetricPrefix.HECTO.english("Meter"), "Hectometer");
    assertEquals(MetricPrefix.HECTO.factor(), RationalScalar.of(100, 1));
  }

  public void testMicro() {
    assertEquals(MetricPrefix.MICRO.prefix("s"), "us");
    assertEquals(MetricPrefix.MICRO.english("Seconds"), "Microseconds");
    assertEquals(MetricPrefix.MICRO.factor(), RationalScalar.of(1, 1_000_000));
  }

  public void testNull() {
    assertEquals(MetricPrefix.NULL.prefix("A"), "A");
    assertEquals(MetricPrefix.NULL.english("Amperes"), "Amperes");
    assertEquals(MetricPrefix.NULL.factor(), RealScalar.ONE);
  }

  public void testUnique() {
    int n = MetricPrefix.values().length;
    // assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::english).distinct().count(), n);
    assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::factor).distinct().count(), n);
    // assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::prefix).distinct().count(), n);
  }

  public void testExact() {
    Stream.of(MetricPrefix.values()).map(MetricPrefix::factor).forEach(ExactScalarQ::require);
  }

  public void testPackage() {
    assertFalse(Modifier.isPublic(MetricPrefix.class.getModifiers()));
  }
}
