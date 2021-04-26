// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.Expectation;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class GaussianTest extends TestCase {
  public void testPlusGaussian() {
    Scalar a = Gaussian.of(10, 1);
    Scalar b = Gaussian.of(-2, 2);
    Scalar c = a.add(b);
    assertEquals(c, Gaussian.of(8, 3));
  }

  public void testPlusReal() {
    Scalar a = Gaussian.of(10, 1);
    Scalar b = RealScalar.of(3);
    Scalar c = a.add(b);
    assertEquals(c, Gaussian.of(13, 1));
  }

  public void testMultiply() {
    Scalar a = Gaussian.of(5, 2);
    Scalar b = RealScalar.of(-3);
    Scalar c = a.multiply(b);
    assertEquals(c, Gaussian.of(-15, 2 * 9));
  }

  public void testMean() {
    Tensor vector = Tensors.of(Gaussian.of(2, 3), Gaussian.of(3, 1), Gaussian.of(-3, 1));
    Scalar mean = (Scalar) Mean.of(vector);
    assertTrue(mean instanceof Gaussian);
    Scalar actual = Gaussian.of(Scalars.fromString("2/3"), Scalars.fromString("5/9"));
    assertEquals(mean, actual);
  }

  public void testNonExact() {
    assertTrue(ExactScalarQ.of(Gaussian.of(1, 2)));
    assertFalse(ExactScalarQ.of(Gaussian.of(1, 0.2)));
    assertFalse(ExactScalarQ.of(Gaussian.of(0.3, 2)));
    assertFalse(ExactScalarQ.of(Gaussian.of(0.3, 0.5)));
  }

  public void testGaussianWithQuantity() {
    Scalar gq1 = Gaussian.of( //
        Quantity.of(3, "m"), //
        Quantity.of(2, "m^2"));
    Scalar gq2 = Gaussian.of( //
        Quantity.of(-3, "m"), //
        Quantity.of(1, "m^2"));
    Scalar gq3 = gq1.add(gq2);
    Scalar ga3 = Gaussian.of( //
        Quantity.of(0, "m"), //
        Quantity.of(3, "m^2"));
    assertEquals(gq3, ga3);
    Scalar qs = Quantity.of(7, "s");
    Scalar gq4 = gq1.multiply(qs);
    Scalar ga4 = Gaussian.of( //
        Quantity.of(21, "m*s"), //
        Quantity.of(98, "m^2*s^2"));
    assertEquals(gq4, ga4);
  }

  public void testDistribution() {
    Gaussian gaussian = (Gaussian) Gaussian.of(-200, 10);
    Distribution distribution = gaussian.distribution();
    Scalar mean = (Scalar) Mean.of(RandomVariate.of(distribution, 20));
    Chop.below(3).requireClose(mean, RealScalar.of(-200));
  }

  public void testDistWithQuantity() {
    Gaussian gq1 = (Gaussian) Gaussian.of( //
        Quantity.of(3, "m"), //
        Quantity.of(2, "m^2"));
    ExactScalarQ.require(gq1);
    Distribution distribution = gq1.distribution(); // operates on Quantity
    Scalar rand = RandomVariate.of(distribution); // produces quantity with [m]
    assertTrue(rand instanceof Quantity);
    assertEquals(Expectation.mean(distribution), Quantity.of(3, "m"));
    assertEquals(gq1.one(), RealScalar.ONE);
    assertEquals(gq1.one().multiply(gq1), gq1);
    Tolerance.CHOP.requireClose( // exact would be nice
        Expectation.variance(distribution), Quantity.of(2, "m^2"));
  }

  public void testFail() {
    AssertFail.of(() -> Gaussian.of(2, -3));
  }
}
