// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.exp.Log10;

class MetricPrefixTest {
  @ParameterizedTest
  @EnumSource
  void testAll(MetricPrefix metricPrefix) {
    String string = metricPrefix.english("Phantasy");
    assertTrue(string.endsWith("hantasy"));
    ExactScalarQ.require(metricPrefix.factor());
  }

  @Test
  void testGiga() {
    assertEquals(MetricPrefix.GIGA.prefix("Hz"), "GHz");
    assertEquals(MetricPrefix.GIGA.english("Hertz"), "Gigahertz");
    assertEquals(MetricPrefix.GIGA.factor(), RealScalar.of(1_000_000_000));
  }

  @Test
  void testHecto() {
    assertEquals(MetricPrefix.HECTO.prefix("m"), "hm");
    assertEquals(MetricPrefix.HECTO.english("Meter"), "Hectometer");
    assertEquals(MetricPrefix.HECTO.factor(), Rational.of(100, 1));
  }

  @Test
  void testMicro() {
    assertEquals(MetricPrefix.MICRO.prefix("s"), "us");
    assertEquals(MetricPrefix.MICRO.english("Seconds"), "Microseconds");
    assertEquals(MetricPrefix.MICRO.factor(), Rational.of(1, 1_000_000));
  }

  @Test
  void testNull() {
    assertEquals(MetricPrefix.NULL.prefix("A"), "A");
    assertEquals(MetricPrefix.NULL.english("Amperes"), "Amperes");
    assertEquals(MetricPrefix.NULL.factor(), RealScalar.ONE);
  }

  @Test
  void testExp() {
    Map<Scalar, MetricPrefix> map = new HashMap<>();
    for (MetricPrefix metricPrefix : MetricPrefix.values())
      if (metricPrefix.exponent() % 3 == 0)
        map.put(RealScalar.of(metricPrefix.exponent()), metricPrefix);
    Scalar scalar = RealScalar.of(-3.123456789e-13);
    Scalar abs = Abs.FUNCTION.apply(scalar);
    Scalar log = Log10.FUNCTION.apply(abs);
    Scalar floor = Floor.toMultipleOf(RealScalar.of(3)).apply(log);
    if (map.containsKey(floor)) {
      MetricPrefix metricPrefix = map.get(floor);
      assertEquals(metricPrefix, MetricPrefix.FEMTO);
    }
  }

  @Test
  void testUnique() {
    int n = MetricPrefix.values().length;
    // assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::english).distinct().count(), n);
    assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::factor).distinct().count(), n);
    // assertEquals(Stream.of(MetricPrefix.values()).map(MetricPrefix::prefix).distinct().count(), n);
  }

  @Test
  void testExact() {
    Stream.of(MetricPrefix.values()).map(MetricPrefix::factor).forEach(ExactScalarQ::require);
  }

  @Test
  void testPackage() {
    assertFalse(Modifier.isPublic(MetricPrefix.class.getModifiers()));
  }
}
