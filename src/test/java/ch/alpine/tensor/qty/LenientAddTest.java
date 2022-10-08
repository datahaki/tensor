// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.sca.N;

class LenientAddTest {
  @Test
  void testDifferent() {
    Scalar p = Quantity.of(3, "m");
    Scalar q = Quantity.of(0, "s");
    assertEquals(LenientAdd.of(p, q), p);
    assertEquals(LenientAdd.of(q, p), p);
    assertThrows(Throw.class, () -> p.add(q));
  }

  @Test
  void testZeros() {
    Scalar p = Quantity.of(0, "m");
    Scalar q = Quantity.of(0, "s");
    assertEquals(LenientAdd.of(p, q), RealScalar.ZERO);
    assertEquals(LenientAdd.of(q, p), RealScalar.ZERO);
    assertThrows(Throw.class, () -> p.add(q));
  }

  @Test
  void testDifferentFail() {
    Scalar p = Quantity.of(3, "m");
    Scalar q = Quantity.of(1, "s");
    assertThrows(Throw.class, () -> LenientAdd.of(p, q));
    assertThrows(Throw.class, () -> p.add(q));
  }

  @Test
  void testPhysicalConstants() {
    Properties properties = ResourceData.properties("/ch/alpine/tensor/qty/physical_constants.properties");
    {
      Scalar scalar = Scalars.fromString(properties.getProperty("na"));
      assertTrue(ExactScalarQ.of(scalar));
      String string = N.DOUBLE.apply(scalar).toString();
      assertTrue(string.startsWith("6.0221"));
      assertTrue(string.contains("E23["));
      QuantityMagnitude.singleton("mol^-1").apply(scalar);
    }
    {
      Scalar scalar = Scalars.fromString(properties.getProperty("boltzmann"));
      assertTrue(ExactScalarQ.of(scalar));
      String string = N.DOUBLE.apply(scalar).toString();
      assertTrue(string.startsWith("1.3806"));
      assertTrue(string.contains("E-23["));
      QuantityMagnitude.singleton("J*K^-1").apply(scalar);
    }
    {
      Scalar scalar = Scalars.fromString(properties.getProperty("nucs"));
      assertTrue(ExactScalarQ.of(scalar));
      String string = N.DOUBLE.apply(scalar).toString();
      assertTrue(string.startsWith("9.1926"));
      assertTrue(string.contains("E9["));
      QuantityMagnitude.singleton("Hz").apply(scalar);
    }
    {
      Scalar scalar = Scalars.fromString(properties.getProperty("e"));
      assertTrue(ExactScalarQ.of(scalar));
      String string = N.DOUBLE.apply(scalar).toString();
      assertTrue(string.startsWith("1.6021"));
      assertTrue(string.contains("E-19["));
      QuantityMagnitude.singleton("C").apply(scalar);
    }
    {
      Scalar scalar = Scalars.fromString(properties.getProperty("kcd"));
      assertTrue(ExactScalarQ.of(scalar));
      assertEquals(scalar, Quantity.of(683, "lm*W^-1"));
    }
    {
      Scalar scalar = Scalars.fromString(properties.getProperty("h"));
      assertTrue(ExactScalarQ.of(scalar));
      String string = N.DOUBLE.apply(scalar).toString();
      assertTrue(string.startsWith("6.6260"));
      assertTrue(string.contains("E-34["));
      QuantityMagnitude.singleton("s*J").apply(scalar);
    }
    {
      Scalar scalar = Scalars.fromString(properties.getProperty("c"));
      assertTrue(ExactScalarQ.of(scalar));
      String string = N.DOUBLE.apply(scalar).toString();
      assertTrue(string.startsWith("2.9979"));
      assertTrue(string.contains("E8["));
      QuantityMagnitude.singleton("m*s^-1").apply(scalar);
    }
  }
}
