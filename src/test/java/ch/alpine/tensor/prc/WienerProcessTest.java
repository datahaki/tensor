// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Clips;

class WienerProcessTest {
  @Test
  void test() {
    RandomProcess randomProcess = WienerProcess.of(0, 1);
    assertEquals(randomProcess.toString(), "WienerProcess[0, 1]");
    RandomFunction randomFunction = RandomFunction.of(randomProcess);
    RandomVariate.of(UniformDistribution.of(0, 10), 100).stream() //
        .map(Scalar.class::cast) //
        .forEach(randomFunction::evaluate);
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
}
