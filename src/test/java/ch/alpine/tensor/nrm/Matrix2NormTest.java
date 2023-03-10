// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.LehmerTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.LogisticDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;

class Matrix2NormTest {
  @Test
  void testMatrix1() {
    Tensor matrix = Tensors.matrix(new Number[][] { { 1, 2, 3 }, { 9, -3, 0 } });
    Scalar nrm = Matrix2Norm.of(matrix);
    assertEquals(nrm, Matrix2Norm.of(Transpose.of(matrix)));
    // Mathematica: 9.493062577750756
    Chop._14.requireClose(nrm, DoubleScalar.of(9.493062577750756));
  }

  @Test
  void testMatrix2() {
    Tensor matrix = Tensors.fromString("{{}}");
    assertThrows(Exception.class, () -> Matrix2Norm.of(matrix));
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

  @Test
  void testQuantity() {
    Unit unit = Unit.of("m*K^1/2");
    for (int n = 2; n < 6; ++n) {
      _check(HilbertMatrix.of(n));
      _check(RandomVariate.of(NormalDistribution.standard(), n, n).map(s -> Quantity.of(s, "m")));
      _check(RandomVariate.of(UniformDistribution.of(-0.05, 0.05), n, n).map(s -> Quantity.of(s, "m")));
      _check(RandomVariate.of(UniformDistribution.of(-5, 5), n, n).map(s -> Quantity.of(s, unit)));
    }
  }

  @Test
  void testNonSquareQuantity() {
    for (int n = 3; n < 6; ++n) {
      _check(RandomVariate.of(NormalDistribution.standard(), n - 2, n).map(s -> Quantity.of(s, "m")));
      _check(RandomVariate.of(UniformDistribution.of(-0.05, 0.05), n - 2, n).map(s -> Quantity.of(s, "m*s")));
      _check(RandomVariate.of(UniformDistribution.of(-5, 5), n - 2, n).map(s -> Quantity.of(s, "m")));
    }
  }

  @Test
  void testNonSquareQuantity2() {
    for (int n = 3; n < 6; ++n) {
      _check(RandomVariate.of(NormalDistribution.standard(), n, n - 2).map(s -> Quantity.of(s, "m")));
      _check(RandomVariate.of(UniformDistribution.of(-0.05, 0.05), n, n - 2).map(s -> Quantity.of(s, "m^-2")));
      _check(RandomVariate.of(UniformDistribution.of(-5, 5), n, n - 2).map(s -> Quantity.of(s, "m")));
    }
  }

  @Test
  void testComplex() {
    Distribution distribution = LogisticDistribution.of(2, 3);
    Tensor re = RandomVariate.of(distribution, 5, 3);
    Tensor im = RandomVariate.of(distribution, 5, 3);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    Scalar norm2bound1 = Matrix2Norm.bound(matrix);
    assertEquals(Im.FUNCTION.apply(norm2bound1), RealScalar.ZERO);
    Scalar norm2bound2 = Matrix2Norm.bound(Transpose.of(matrix));
    Tolerance.CHOP.requireClose(norm2bound1, norm2bound2);
  }

  @Test
  void testZero() {
    assertEquals(Matrix2Norm.bound(Array.zeros(2, 3)), RealScalar.ZERO);
    assertEquals(Matrix2Norm.bound(Array.zeros(3, 2)), RealScalar.ZERO);
  }

  @Test
  void testNonMatrixFail() {
    assertThrows(Throw.class, () -> Matrix2Norm.bound(RealScalar.of(2)));
    assertThrows(Throw.class, () -> Matrix2Norm.bound(Tensors.vector(1, 2, 3)));
    assertThrows(ClassCastException.class, () -> Matrix2Norm.bound(LehmerTensor.of(3)));
  }

  @Test
  void testNonArray() {
    assertThrows(IllegalArgumentException.class, () -> Matrix2Norm.bound(Tensors.fromString("{{1, 2, 3}, {4, 5}}")));
  }
}
