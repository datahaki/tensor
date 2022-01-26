// code by jph
package ch.alpine.tensor.lie.ad;

import java.io.IOException;
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
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.spa.SparseArray;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BakerCampbellHausdorffTest extends TestCase {
  private static void _check(Tensor ad) {
    BakerCampbellHausdorff bakerCampbellHausdorff = //
        (BakerCampbellHausdorff) BakerCampbellHausdorff.of(ad, BchApprox.DEGREE);
    BchApprox appx = (BchApprox) BchApprox.of(ad);
    int n = ad.length();
    for (int c0 = 0; c0 < n; ++c0)
      for (int c1 = 0; c1 < n; ++c1) {
        Tensor x = UnitVector.of(n, c0);
        Tensor y = UnitVector.of(n, c1);
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

  public void testHe1() {
    _check(TestHelper.he1());
  }

  public void testSl2() {
    _check(TestHelper.sl2());
  }

  public void testSl2Sophus() {
    Tensor ad = Tensors.fromString( //
        "{{{0, 0, 0}, {0, 0, 1}, {0, -1, 0}}, {{0, 0, 1}, {0, 0, 0}, {-1, 0, 0}}, {{0, -1, 0}, {1, 0, 0}, {0, 0, 0}}}");
    _check(ad);
    assertEquals(Det.of(KillingForm.of(ad)), RealScalar.of(-8));
  }

  public void testSe2() {
    _check(TestHelper.se2());
  }

  public void testSo3() {
    _check(TestHelper.so3());
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
    Tensor ad = TestHelper.sl2();
    Serialization.copy(BakerCampbellHausdorff.of(ad, 2));
    ad.set(Scalar::zero, Tensor.ALL, 1, 2);
    AssertFail.of(() -> BakerCampbellHausdorff.of(ad, 2));
  }

  public void testDegreeFail() {
    Tensor ad = Array.sparse(2, 2, 2);
    BakerCampbellHausdorff.of(ad, 1);
    AssertFail.of(() -> BakerCampbellHausdorff.of(ad, 1, null));
    AssertFail.of(() -> BakerCampbellHausdorff.of(ad, 0));
  }

  public void testMatrixLogExpExpSe2() {
    MatrixAlgebra matrixAlgebra = new MatrixAlgebra(TestHelper.se2_basis());
    TestHelper.check(matrixAlgebra, 8);
  }

  public void testMatrixLogExpExpSo3() {
    MatrixAlgebra matrixAlgebra = new MatrixAlgebra(TestHelper.so3_basis());
    TestHelper.check(matrixAlgebra, 8);
  }
}
