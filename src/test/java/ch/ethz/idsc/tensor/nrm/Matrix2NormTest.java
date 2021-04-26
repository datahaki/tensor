// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.LehmerTensor;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.LogisticDistribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class Matrix2NormTest extends TestCase {
  public void testMatrix1() {
    Tensor matrix = Tensors.matrix(new Number[][] { { 1, 2, 3 }, { 9, -3, 0 } });
    Scalar nrm = Matrix2Norm.of(matrix);
    assertEquals(nrm, Matrix2Norm.of(Transpose.of(matrix)));
    // Mathematica: 9.493062577750756
    Chop._14.requireClose(nrm, DoubleScalar.of(9.493062577750756));
  }

  public void testMatrix2() {
    Tensor matrix = Tensors.fromString("{{}}");
    AssertFail.of(() -> Matrix2Norm.of(matrix));
  }

  private static void _check(Tensor x) {
    Scalar n2 = Matrix2Norm.of(x);
    Scalar nb = Matrix2Norm.bound(x);
    if (Scalars.lessThan(nb, n2) && !Tolerance.CHOP.isClose(n2, nb)) {
      System.err.println("n2=" + n2);
      System.err.println("nb=" + nb);
      fail();
    }
  }

  public void testQuantity() {
    Unit unit = Unit.of("m*K^1/2");
    for (int n = 2; n < 6; ++n) {
      _check(HilbertMatrix.of(n));
      _check(RandomVariate.of(NormalDistribution.standard(), n, n).map(s -> Quantity.of(s, "m")));
      _check(RandomVariate.of(UniformDistribution.of(-0.05, 0.05), n, n).map(s -> Quantity.of(s, "m")));
      _check(RandomVariate.of(UniformDistribution.of(-5, 5), n, n).map(s -> Quantity.of(s, unit)));
    }
  }

  public void testNonSquareQuantity() {
    for (int n = 3; n < 6; ++n) {
      _check(RandomVariate.of(NormalDistribution.standard(), n - 2, n).map(s -> Quantity.of(s, "m")));
      _check(RandomVariate.of(UniformDistribution.of(-0.05, 0.05), n - 2, n).map(s -> Quantity.of(s, "m*s")));
      _check(RandomVariate.of(UniformDistribution.of(-5, 5), n - 2, n).map(s -> Quantity.of(s, "m")));
    }
  }

  public void testNonSquareQuantity2() {
    for (int n = 3; n < 6; ++n) {
      _check(RandomVariate.of(NormalDistribution.standard(), n, n - 2).map(s -> Quantity.of(s, "m")));
      _check(RandomVariate.of(UniformDistribution.of(-0.05, 0.05), n, n - 2).map(s -> Quantity.of(s, "m^-2")));
      _check(RandomVariate.of(UniformDistribution.of(-5, 5), n, n - 2).map(s -> Quantity.of(s, "m")));
    }
  }

  public void testComplex() {
    Distribution distribution = LogisticDistribution.of(2, 3);
    Tensor re = RandomVariate.of(distribution, 5, 3);
    Tensor im = RandomVariate.of(distribution, 5, 3);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    Scalar norm2bound1 = Matrix2Norm.bound(matrix);
    assertEquals(Imag.FUNCTION.apply(norm2bound1), RealScalar.ZERO);
    Scalar norm2bound2 = Matrix2Norm.bound(Transpose.of(matrix));
    Tolerance.CHOP.requireClose(norm2bound1, norm2bound2);
  }

  public void testZero() {
    assertEquals(Matrix2Norm.bound(Array.zeros(2, 3)), RealScalar.ZERO);
    assertEquals(Matrix2Norm.bound(Array.zeros(3, 2)), RealScalar.ZERO);
  }

  public void testNonMatrixFail() {
    AssertFail.of(() -> Matrix2Norm.bound(RealScalar.of(2)));
    AssertFail.of(() -> Matrix2Norm.bound(Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> Matrix2Norm.bound(LehmerTensor.of(3)));
  }

  public void testNonArray() {
    AssertFail.of(() -> Matrix2Norm.bound(Tensors.fromString("{{1, 2, 3}, {4, 5}}")));
  }
}
