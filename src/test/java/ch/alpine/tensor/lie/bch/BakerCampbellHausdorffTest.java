// code by jph
package ch.alpine.tensor.lie.bch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.CliffordAlgebra;
import ch.alpine.tensor.lie.ExAd;
import ch.alpine.tensor.lie.KillingForm;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.spa.SparseArray;

class BakerCampbellHausdorffTest {
  static void _check(Tensor ad, int degree) {
    BakerCampbellHausdorff bakerCampbellHausdorff = //
        new BakerCampbellHausdorff(ad, degree, Chop._14);
    BchSeries appx = (BchSeries) BakerCampbellHausdorff.of(ad, degree);
    int n = ad.length();
    for (int c0 = 0; c0 < n; ++c0)
      for (int c1 = 0; c1 < n; ++c1)
      // Distribution distribution2 = DiscreteUniformDistribution.of(-10, 10);
      // RandomVariate
      {
        Tensor x =
            // RandomVariate.of(distribution2, n);
            UnitVector.of(n, c0);
        Tensor y =
            // RandomVariate.of(distribution2, n);
            UnitVector.of(n, c1);
        {
          Tensor res1 = bakerCampbellHausdorff.apply(x, y);
          Tensor res2 = appx.apply(x, y);
          assertEquals(res1, res2);
          ExactTensorQ.require(res1);
        }
        {
          Tensor res1 = bakerCampbellHausdorff.series(x, y);
          Tensor res2 = appx.series(x, y);
          assertEquals(res1, res2);
          ExactTensorQ.require(res1);
        }
      }
    {
      Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
      Tensor x = RandomVariate.of(distribution, n);
      Tensor y = RandomVariate.of(distribution, n);
      {
        Tensor res1 = bakerCampbellHausdorff.apply(x, y);
        Tensor res2 = appx.apply(x, y);
        assertEquals(res1, res2);
        ExactTensorQ.require(res1);
        Tensor res3 = bakerCampbellHausdorff.apply(y.negate(), x.negate()).negate();
        assertEquals(res1, res3);
      }
    }
  }

  @ParameterizedTest
  @ValueSource(ints = { 6, 8 })
  void testHe1(int d) {
    _check(ExAd.HE2.ad(), d);
  }

  @ParameterizedTest
  @ValueSource(ints = { 6, 8 })
  void testSl2(int d) {
    _check(ExAd.SL2.ad(), d);
  }

  @Test
  void testSl2SophusAd() {
    Tensor ad = Tensors.fromString( //
        "{{{0, 0, 0}, {0, 0, 1}, {0, -1, 0}}, {{0, 0, 1}, {0, 0, 0}, {-1, 0, 0}}, {{0, -1, 0}, {1, 0, 0}, {0, 0, 0}}}");
    assertEquals(Det.of(KillingForm.of(ad)), RealScalar.of(-8));
  }

  @ParameterizedTest
  @ValueSource(ints = { 6, 8 })
  void testSl2Sophus(int d) {
    Tensor ad = Tensors.fromString( //
        "{{{0, 0, 0}, {0, 0, 1}, {0, -1, 0}}, {{0, 0, 1}, {0, 0, 0}, {-1, 0, 0}}, {{0, -1, 0}, {1, 0, 0}, {0, 0, 0}}}");
    _check(ad, d);
  }

  @ParameterizedTest
  @ValueSource(ints = { 6, 8, 10 })
  void testSe2(int d) {
    _check(ExAd.SE2.ad(), d);
  }

  @ParameterizedTest
  @ValueSource(ints = { 6, 8 })
  void testSo3(int d) {
    _check(ExAd.SO3.ad(), d);
  }

  @Test
  void testOptimized2() {
    Tensor ad = ExAd.SL2.ad();
    assertInstanceOf(BchSeries6.class, BakerCampbellHausdorff.of(ad, 6, Chop._02));
    assertInstanceOf(BchSeries8.class, BakerCampbellHausdorff.of(ad, 8, Chop._02));
    assertInstanceOf(BchSeriesA.class, BakerCampbellHausdorff.of(ad, 10, Chop._02));
  }

  @Test
  void testSparse() throws ClassNotFoundException, IOException {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.of(1, 2);
    Tensor cp = cliffordAlgebra.cp();
    assertInstanceOf(SparseArray.class, cp);
    TensorBinaryOperator binaryOperator = Serialization.copy(BakerCampbellHausdorff.of(cp, 3));
    int n = cp.length();
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    Random random = new Random(1234);
    Tensor x = RandomVariate.of(distribution, random, n).divide(RealScalar.of(20));
    Tensor y = RandomVariate.of(distribution, random, n).divide(RealScalar.of(20));
    Tensor apply = binaryOperator.apply(x, y);
    ExactTensorQ.require(apply);
  }

  @Test
  void testJacobiFail() throws ClassNotFoundException, IOException {
    Tensor ad = ExAd.SL2.ad().copy();
    Serialization.copy(BakerCampbellHausdorff.of(ad, 2));
    ad.set(Scalar::zero, Tensor.ALL, 1, 2);
    assertThrows(Exception.class, () -> BakerCampbellHausdorff.of(ad, 2));
  }

  @Test
  void testDegreeFail() {
    Tensor ad = Array.sparse(2, 2, 2);
    BakerCampbellHausdorff.of(ad, 1);
    assertThrows(Exception.class, () -> BakerCampbellHausdorff.of(ad, 1, null));
    assertThrows(Exception.class, () -> BakerCampbellHausdorff.of(ad, 0));
  }

  @Test
  void testChopNullFail() {
    Tensor ad = ExAd.SL2.ad();
    assertThrows(Exception.class, () -> BakerCampbellHausdorff.of(ad, 6, null));
  }
}
