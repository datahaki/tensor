// code by jph
package ch.alpine.tensor.lie.ad;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.lie.MatrixBracket;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.KroneckerDelta;
import ch.alpine.tensor.spa.SparseArray;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BakerCampbellHausdorffTest extends TestCase {
  private static void _check(Tensor ad, Tensor basis) {
    JacobiIdentity.require(ad);
    int n = ad.length();
    assertEquals(n, basis.length());
    for (int c0 = 0; c0 < n; ++c0)
      for (int c1 = 0; c1 < n; ++c1) {
        Tensor mr = MatrixBracket.of(basis.get(c0), basis.get(c1));
        Tensor ar = ad.dot(UnitVector.of(n, c0)).dot(UnitVector.of(n, c1));
        assertEquals(ar.dot(basis), mr);
      }
    assertEquals(AdBuilder.of(basis), ad);
  }

  public void testHe1Basis() {
    Tensor b0 = Array.of(l -> KroneckerDelta.of(l, Arrays.asList(0, 1)), 3, 3);
    Tensor b1 = Array.of(l -> KroneckerDelta.of(l, Arrays.asList(1, 2)), 3, 3);
    Tensor b2 = Array.of(l -> KroneckerDelta.of(l, Arrays.asList(0, 2)), 3, 3);
    Tensor basis = Tensors.of(b0, b1, b2);
    _check(LieAlgebras.he1(), basis);
  }

  public void testSo3Basis() {
    Tensor basis = LeviCivitaTensor.of(3).negate();
    _check(LieAlgebras.so3(), basis);
  }

  public void testSe2Basis() {
    Tensor b0 = Tensors.fromString("{{0, 0, 1}, {0, 0, 0}, {0, 0, 0}}");
    Tensor b1 = Tensors.fromString("{{0, 0, 0}, {0, 0, 1}, {0, 0, 0}}");
    Tensor b2 = LeviCivitaTensor.of(3).get(2).negate();
    Tensor basis = Tensors.of(b0, b1, b2);
    _check(LieAlgebras.se2(), basis);
  }

  private static void _check(Tensor ad) {
    BakerCampbellHausdorff bakerCampbellHausdorff = //
        (BakerCampbellHausdorff) BakerCampbellHausdorff.of(ad, BchApprox.DEGREE);
    BchApprox appx = (BchApprox) BchApprox.of(ad);
    int n = ad.length();
    for (int c0 = 0; c0 < n; ++c0)
      for (int c1 = 0; c1 < n; ++c1) {
        Tensor x = UnitVector.of(3, c0);
        Tensor y = UnitVector.of(3, c1);
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
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    for (int count = 0; count < 10; ++count) {
      Tensor x = RandomVariate.of(distribution, 3);
      Tensor y = RandomVariate.of(distribution, 3);
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

  public void testHe1() {
    _check(LieAlgebras.he1());
  }

  public void testSl2() {
    _check(LieAlgebras.sl2());
  }

  public void testSe2() {
    _check(LieAlgebras.se2());
  }

  public void testSo3() {
    _check(LieAlgebras.so3());
  }

  public void testSparse() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.of(1, 2);
    Tensor cp = cliffordAlgebra.cp();
    assertTrue(cp instanceof SparseArray);
    BinaryOperator<Tensor> binaryOperator = BakerCampbellHausdorff.of(cp, 3);
    int n = cp.length();
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    Random random = new Random(1234);
    Tensor x = RandomVariate.of(distribution, random, n).divide(RealScalar.of(20));
    Tensor y = RandomVariate.of(distribution, random, n).divide(RealScalar.of(20));
    Tensor apply = binaryOperator.apply(x, y);
    ExactTensorQ.require(apply);
  }

  public void testJacobiFail() throws ClassNotFoundException, IOException {
    Tensor ad = LieAlgebras.sl2();
    Serialization.copy(BakerCampbellHausdorff.of(ad, 2));
    ad.set(Scalar::zero, Tensor.ALL, 1, 2);
    AssertFail.of(() -> BakerCampbellHausdorff.of(ad, 2));
  }

  public void testDegreeFail() {
    Tensor ad = Array.zeros(2, 2, 2);
    BakerCampbellHausdorff.of(ad, 1);
    AssertFail.of(() -> BakerCampbellHausdorff.of(ad, 0));
  }
}
