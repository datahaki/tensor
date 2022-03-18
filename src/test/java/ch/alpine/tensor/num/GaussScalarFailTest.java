// code by jph
package ch.alpine.tensor.num;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.usr.AssertFail;

public class GaussScalarFailTest {
  @Test
  public void testPrimeNegative() {
    AssertFail.of(() -> GaussScalar.of(2, -7));
  }

  @Test
  public void testPrime() {
    AssertFail.of(() -> GaussScalar.of(2, 20001));
    AssertFail.of(() -> GaussScalar.of(2, 100101));
  }

  @Test
  public void testIllegalGauss() {
    Scalar a = GaussScalar.of(4, 7);
    Scalar b = GaussScalar.of(4, 11);
    AssertFail.of(() -> a.add(b));
  }

  @Test
  public void testIllegal() {
    Scalar a = GaussScalar.of(4, 7);
    Scalar b = DoubleScalar.of(4.33);
    AssertFail.of(() -> a.add(b));
    AssertFail.of(() -> a.multiply(b));
  }

  @Test
  public void testMultiplyFail() {
    AssertFail.of(() -> GaussScalar.of(2, 7).multiply(RealScalar.of(0.3)));
  }

  @Test
  public void testPowerFail() {
    Scalar scalar = GaussScalar.of(2, 7);
    AssertFail.of(() -> Power.of(scalar, 2.3));
    AssertFail.of(() -> Power.of(scalar, RationalScalar.of(2, 3)));
  }

  @Test
  public void testCompareFail1() {
    AssertFail.of(() -> Scalars.compare(GaussScalar.of(2, 7), GaussScalar.of(9, 11)));
  }

  @Test
  public void testCompareTypeFail() {
    AssertFail.of(() -> Scalars.compare(GaussScalar.of(2, 7), RealScalar.of(0.3)));
    AssertFail.of(() -> Scalars.compare(RealScalar.of(0.3), GaussScalar.of(2, 7)));
  }

  @Test
  public void testComparableFail() {
    AssertFail.of(() -> Scalars.compare(DoubleScalar.of(3.14), GaussScalar.of(1, 7)));
  }
}
