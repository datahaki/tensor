// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.ExactScalarQ;

class SiUnitSystemTest {
  @Test
  void testInstances() {
    assertEquals(UnitSystem.SI(), SiUnitSystem.INSTANCE.unitSystem);
  }

  @Test
  void testBase() {
    Set<String> base = UnitSystems.base(UnitSystem.SI());
    assertTrue(7 <= base.size());
    assertTrue(base.containsAll(Arrays.asList("A", "cd", "s", "K", "mol", "kg", "m")));
  }

  @Test
  void testExtension() {
    Map<String, Scalar> map = new HashMap<>(UnitSystem.SI().map());
    map.put("CHF", Quantity.of(3, "m"));
    UnitSystem unitSystem = SimpleUnitSystem.from(map);
    KnownUnitQ knownUnitQ = KnownUnitQ.in(unitSystem);
    assertTrue(knownUnitQ.test(Unit.of("CHF^2*K")));
  }

  private static int _check(Collection<String> ignore, String prefix, Scalar conv) {
    int checked = 0;
    for (Entry<String, Scalar> entry : UnitSystem.SI().map().entrySet()) {
      String atom = entry.getKey();
      if (atom.matches(prefix + ".+") && !ignore.contains(atom)) {
        Scalar conversion = entry.getValue();
        String target = atom.substring(1);
        if (KnownUnitQ.SI().test(Unit.of(target)))
          try {
            Scalar scalar = UnitConvert.SI().to(target).apply(Quantity.of(1, atom));
            Scalar expect = Quantity.of(conv, target);
            if (ExactScalarQ.of(conversion)) {
              assertEquals(ExactScalarQ.require(scalar), expect);
              ++checked;
            }
          } catch (Exception exception) {
            System.out.println(atom);
            fail();
          }
      }
    }
    return checked;
  }

  @Test
  void testMilli() {
    int checked = _check(List.of("min"), "m", Rational.of(1, 1000));
    assertTrue(10 <= checked);
  }

  @Test
  void testMicro() {
    int checked = _check(List.of(), "u", Rational.of(1, 1000000));
    assertTrue(4 <= checked);
  }

  @Test
  void testNano() {
    int checked = _check(List.of("nmi"), "n", Rational.of(1, 1000000000));
    assertTrue(3 <= checked);
  }

  @Test
  void testPico() {
    int checked = _check(Arrays.asList("pt", "ppt", "ppm"), "p", Rational.of(1, 1000000000000L));
    assertTrue(1 <= checked);
  }

  @Test
  void testKilo() {
    int checked = _check(Arrays.asList("kat", "kp"), "k", RealScalar.of(1000));
    assertTrue(8 <= checked);
  }

  @Test
  void testMega() {
    int checked = _check(List.of(), "M", RealScalar.of(1000000));
    assertTrue(6 <= checked);
  }

  @Test
  void testGiga() {
    int checked = _check(List.of("Ga"), "G", RealScalar.of(1000000000));
    assertTrue(3 <= checked);
  }

  @Test
  void testTera() {
    int checked = _check(List.of(), "T", RealScalar.of(1000000000000L));
    assertTrue(1 <= checked);
  }

  @Test
  void testToString() {
    assertTrue(SiUnitSystem.INSTANCE.unitSystem.toString().startsWith("UnitSystem[{"));
  }
}
