// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class SiUnitSystemTest extends TestCase {
  public void testInstances() {
    assertEquals(UnitSystem.SI(), SiUnitSystem.INSTANCE.unitSystem);
  }

  public void testExtension() {
    Map<String, Scalar> map = new HashMap<>(UnitSystem.SI().map());
    map.put("CHF", Quantity.of(3, "m"));
    UnitSystem unitSystem = SimpleUnitSystem.from(map);
    KnownUnitQ knownUnitQ = KnownUnitQ.in(unitSystem);
    assertTrue(knownUnitQ.of(Unit.of("CHF^2*K")));
  }

  private static int _check(Collection<String> ignore, String prefix, Scalar conv) {
    int checked = 0;
    for (Entry<String, Scalar> entry : UnitSystem.SI().map().entrySet()) {
      String atom = entry.getKey();
      if (atom.matches(prefix + ".+") && !ignore.contains(atom)) {
        Scalar conversion = entry.getValue();
        String target = atom.substring(1);
        if (UnitSystem.SI().units().contains(target))
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

  public void testMilli() {
    int checked = _check(Arrays.asList("min"), "m", RationalScalar.of(1, 1000));
    assertTrue(7 <= checked);
  }

  public void testMicro() {
    int checked = _check(Arrays.asList(), "u", RationalScalar.of(1, 1000000));
    assertTrue(2 <= checked);
  }

  public void testNano() {
    int checked = _check(Arrays.asList("nmi"), "n", RationalScalar.of(1, 1000000000));
    assertTrue(2 <= checked);
  }

  public void testPico() {
    int checked = _check(Arrays.asList("pt"), "p", RationalScalar.of(1, 1000000000000L));
    assertTrue(1 <= checked);
  }

  public void testKilo() {
    int checked = _check(Arrays.asList("kat", "kp"), "k", RealScalar.of(1000));
    assertTrue(5 <= checked);
  }

  public void testMega() {
    int checked = _check(Arrays.asList(), "M", RealScalar.of(1000000));
    assertTrue(3 <= checked);
  }

  public void testGiga() {
    int checked = _check(Arrays.asList("Ga"), "G", RealScalar.of(1000000000));
    assertTrue(0 <= checked);
  }

  public void testToString() {
    assertTrue(SiUnitSystem.INSTANCE.unitSystem.toString().startsWith("SimpleUnitSystem["));
  }
}
