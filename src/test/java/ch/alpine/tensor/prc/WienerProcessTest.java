// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.tmp.MinimumTimeIncrement;

class WienerProcessTest {
  @Test
  void testStandard() {
    RandomProcess randomProcess = WienerProcess.standard();
    assertEquals(randomProcess.toString(), "WienerProcess[0, 1]");
    RandomFunction randomFunction = RandomFunction.of(randomProcess);
    RandomVariate.of(UniformDistribution.of(0, 10), 100).stream() //
        .map(Scalar.class::cast) //
        .forEach(randomFunction::evaluate);
    Scalar scalar = MinimumTimeIncrement.of(randomFunction.timeSeries());
    assertTrue(Scalars.lessEquals(scalar, RealScalar.of(10.0 / 100)));
  }

  @Test
  void testStandard2() {
    RandomFunction randomFunction = RandomFunction.of(WienerProcess.of(0, 0));
    randomFunction.evaluate(RealScalar.ZERO);
    randomFunction.evaluate(RealScalar.ONE);
    assertThrows(Exception.class, () -> randomFunction.evaluate(RealScalar.of(-1)));
  }

  @Test
  void testQuantity() {
    Scalar mu = Quantity.of(0, "m*s^-1");
    Scalar sigma = Quantity.of(2, "m*s^-1/2");
    RandomProcess randomProcess = WienerProcess.of(mu, sigma);
    RandomFunction randomFunction = RandomFunction.of(randomProcess);
    {
      Scalar x1 = Quantity.of(4, "s");
      Scalar val = randomFunction.evaluate(x1);
      assertEquals(QuantityUnit.of(val), Unit.of("m"));
      assertEquals(val, randomFunction.evaluate(x1));
    }
    {
      Scalar xm = Quantity.of(2.4, "s");
      Scalar val = randomFunction.evaluate(xm);
      assertEquals(QuantityUnit.of(val), Unit.of("m"));
      assertEquals(val, randomFunction.evaluate(xm));
    }
    {
      Scalar x0 = Quantity.of(0.0, "s");
      Scalar val = randomFunction.evaluate(x0);
      assertEquals(val, Quantity.of(0, "m"));
      assertEquals(QuantityUnit.of(val), Unit.of("m"));
      assertEquals(val, randomFunction.evaluate(x0));
    }
    RandomVariate.of(UniformDistribution.of(Clips.positive(Quantity.of(10, "s"))), 100).stream() //
        .map(Scalar.class::cast) //
        .forEach(randomFunction::evaluate);
  }

  @Test
  void testQuantity2() {
    Scalar mu = Quantity.of(1, "m*s^-1");
    Scalar sigma = Quantity.of(2, "m*s^-1/2");
    RandomProcess randomProcess = WienerProcess.of(mu, sigma);
    RandomFunction randomFunction = RandomFunction.of(randomProcess);
    {
      Scalar x1 = Quantity.of(4, "s");
      Scalar val = randomFunction.evaluate(x1);
      assertEquals(QuantityUnit.of(val), Unit.of("m"));
      assertEquals(val, randomFunction.evaluate(x1));
    }
    {
      Scalar xm = Quantity.of(2.4, "s");
      Scalar val = randomFunction.evaluate(xm);
      assertEquals(QuantityUnit.of(val), Unit.of("m"));
      assertEquals(val, randomFunction.evaluate(xm));
    }
    {
      Scalar x0 = Quantity.of(0.0, "s");
      Scalar val = randomFunction.evaluate(x0);
      assertEquals(val, Quantity.of(0, "m"));
      assertEquals(QuantityUnit.of(val), Unit.of("m"));
      assertEquals(val, randomFunction.evaluate(x0));
    }
    RandomVariate.of(UniformDistribution.of(Clips.positive(Quantity.of(10, "s"))), 100).stream() //
        .map(Scalar.class::cast) //
        .forEach(randomFunction::evaluate);
  }

  @Test
  void testDateTime() {
    Scalar mu = Quantity.of(1, "m*s^-1");
    Scalar sigma = Quantity.of(2, "m*s^-1/2");
    DateTime t_zero = DateTime.now();
    RandomProcess randomProcess = WienerProcess.of(mu, sigma, t_zero, Quantity.of(3, "m"));
    RandomFunction randomFunction = RandomFunction.of(randomProcess);
    {
      Scalar x1 = t_zero.add(Quantity.of(4, "s"));
      Scalar val = randomFunction.evaluate(x1);
      assertEquals(QuantityUnit.of(val), Unit.of("m"));
      assertEquals(val, randomFunction.evaluate(x1));
    }
    {
      Scalar val = randomFunction.evaluate(t_zero);
      assertEquals(val, Quantity.of(3, "m"));
      assertEquals(val, randomFunction.evaluate(t_zero));
    }
    {
      Scalar x1 = t_zero.subtract(Quantity.of(4, "s"));
      assertThrows(Exception.class, () -> randomFunction.evaluate(x1));
    }
  }

  @Test
  void testFlat() {
    RandomProcess randomProcess = WienerProcess.of(0, 0);
    assertEquals(RandomFunction.of(randomProcess).evaluate(Pi.VALUE), RealScalar.ZERO);
  }

  @Test
  void testFlatQuantity() {
    Scalar mu = Quantity.of(0, "m*s^-1");
    Scalar sigma = Quantity.of(0, "m*s^-1/2");
    RandomProcess randomProcess = WienerProcess.of(mu, sigma);
    RandomFunction randomFunction = RandomFunction.of(randomProcess);
    assertEquals(randomFunction.evaluate(Quantity.of(4, "s")), Quantity.of(0, "m"));
  }
}
