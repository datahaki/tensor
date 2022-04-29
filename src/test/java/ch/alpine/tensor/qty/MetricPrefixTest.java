// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.chq.ExactScalarQ;

public class MetricPrefixTest {
  @Test
  public void testGiga() {
    assertEquals(MetricPrefix.GIGA.prefix("Hz"), "GHz");
    assertEquals(MetricPrefix.GIGA.english("Hertz"), "Gigahertz");
    assertEquals(MetricPrefix.GIGA.factor(), RealScalar.of(1_000_000_000));
  }

  @Test
  public void testHecto() {
    assertEquals(MetricPrefix.HECTO.prefix("m"), "hm");
    assertEquals(MetricPrefix.HECTO.english("Meter"), "Hectometer");
    assertEquals(MetricPrefix.HECTO.factor(), RationalScalar.of(100, 1));
  }

  @Test
  public void testMicro() {
    assertEquals(MetricPrefix.MICRO.prefix("s"), "us");
    assertEquals(MetricPrefix.MICRO.english("Seconds"), "Microseconds");
    assertEquals(MetricPrefix.MICRO.factor(), RationalScalar.of(1, 1_000_000));
  }

  @Test
  public void testNull() {
    assertEquals(MetricPrefix.NULL.prefix("A"), "A");
    assertEquals(MetricPrefix.NULL.english("Amperes"), "Amperes");
    assertEquals(MetricPrefix.NULL.factor(), RealScalar.ONE);
  }

  @Test
  public void testUnique() {
    int n = MetricPrefix.values().length;
    // assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::english).distinct().count(), n);
    assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::factor).distinct().count(), n);
    // assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::prefix).distinct().count(), n);
  }

  @Test
  public void testExact() {
    Stream.of(MetricPrefix.values()).map(MetricPrefix::factor).forEach(ExactScalarQ::require);
  }

  @Test
  public void testPackage() {
    assertFalse(Modifier.isPublic(MetricPrefix.class.getModifiers()));
  }
}
