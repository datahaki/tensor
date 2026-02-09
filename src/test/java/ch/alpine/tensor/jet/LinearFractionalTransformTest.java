// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.RealEigensystem;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixLog;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Prime;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;

class LinearFractionalTransformTest {
  @Test
  void testMathematica() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString("{{1, 2, 5}, {3, 4, 6}, {7, 8, 1}}");
    LinearFractionalTransform linearFractionalTransform = //
        Serialization.copy(LinearFractionalTransform.of(matrix));
    Tensor vector = linearFractionalTransform.apply(Tensors.vector(2, 3));
    assertEquals(vector, Tensors.fromString("{1/3, 8/13}"));
  }

  @Test
  void test1d() {
    Tensor xy = Tensors.fromString("{{0},{1},{2}}");
    Tensor uv = Tensors.fromString("{{-0.2},{1.1},{1.5}}");
    {
      LinearFractionalTransform lft = LinearFractionalTransform.fit(xy, uv);
      Tolerance.CHOP.requireClose(Tensor.of(xy.stream().map(lft)), uv);
      LinearFractionalTransform lfi = lft.inverse();
      Tolerance.CHOP.requireClose(Tensor.of(uv.stream().map(lfi)), xy);
    }
    {
      LinearFractionalTransform lft = LinearFractionalTransform.fit(uv, xy);
      Tolerance.CHOP.requireClose(Tensor.of(uv.stream().map(lft)), xy);
    }
  }

  @Test
  void testStackOverflow2d() {
    Tensor xy = Tensors.fromString("{{0,0},{1,0},{1,1},{0,1}}");
    Tensor uv = Tensors.fromString("{{-0.2,0.3},{1.1,0.1},{0.9,0.8},{0.1,1.2}}");
    {
      LinearFractionalTransform lft = LinearFractionalTransform.fit(xy, uv);
      Tolerance.CHOP.requireClose(Tensor.of(xy.stream().map(lft)), uv);
      LinearFractionalTransform lfi = lft.inverse();
      Tolerance.CHOP.requireClose(Tensor.of(uv.stream().map(lfi)), xy);
      RealEigensystem eigensystem = RealEigensystem.of(lft.matrix());
      Tensor values = eigensystem.values();
      assertEquals(values.length(), 3);
    }
    {
      LinearFractionalTransform lft = LinearFractionalTransform.fit(uv, xy);
      Tolerance.CHOP.requireClose(Tensor.of(uv.stream().map(lft)), xy);
    }
    {
      LinearFractionalTransform lft = LinearFractionalTransform.fit(xy, xy);
      Tensor v = Tensors.vectorInt(2, 3);
      Tensor r = lft.apply(v);
      ExactTensorQ.require(r);
      assertEquals(v, r);
      assertTrue(lft.toString().startsWith("LinearFractionalTransform["));
    }
  }

  @Test
  void testEquivalent() {
    Tensor xy = Tensors.fromString("{{0,0},{1,0},{1,1},{0,1}}");
    Tensor uv = Tensors.fromString("{{-0.2,0.3},{1.1,0.1},{0.9,0.8},{0.1,1.2}}");
    LinearFractionalTransform lft = LinearFractionalTransform.fit(xy, uv);
    LinearFractionalTransform lfi1 = LinearFractionalTransform.fit(uv, xy);
    LinearFractionalTransform lfi2 = lft.inverse();
    Tensor f0 = Tensors.vectorDouble(.3, .4);
    Tensor f1 = lft.apply(f0);
    Tolerance.CHOP.requireClose(f0, lfi1.apply(f1));
    Tolerance.CHOP.requireClose(f0, lfi2.apply(f1));
    Tolerance.CHOP.requireClose(lfi1.matrix(), lfi2.matrix());
  }

  @RepeatedTest(5)
  void testStackOverflow(RepetitionInfo repetitionInfo) {
    int d = repetitionInfo.getCurrentRepetition();
    int n = d + 2;
    Distribution distribution = DiscreteUniformDistribution.of(-1000, 1000);
    Tensor xy = RandomVariate.of(distribution, n, d);
    Tensor uv = RandomVariate.of(distribution, n, d);
    _checkExact(xy, uv);
  }

  @RepeatedTest(5)
  void testQuantity(RepetitionInfo repetitionInfo) {
    int d = repetitionInfo.getCurrentRepetition();
    int n = d + 2;
    Distribution distribution = DiscreteUniformDistribution.of(-1000, 1000);
    Tensor xy = RandomVariate.of(distribution, n, d).maps(s -> Quantity.of(s, "m"));
    Tensor uv = RandomVariate.of(distribution, n, d).maps(s -> Quantity.of(s, "m"));
    _checkExact(xy, uv);
  }

  @RepeatedTest(5)
  void testGauss(RepetitionInfo repetitionInfo) {
    int d = repetitionInfo.getCurrentRepetition();
    int n = d + 2;
    int p = Prime.of(2000).number().intValue();
    Distribution distribution = DiscreteUniformDistribution.of(-1000, 1000);
    Tensor xy = RandomVariate.of(distribution, n, d).maps(s -> GaussScalar.of(s.number().intValue(), p));
    Tensor uv = RandomVariate.of(distribution, n, d).maps(s -> GaussScalar.of(s.number().intValue(), p));
    _checkExact(xy, uv);
  }

  private static void _checkExact(Tensor xy, Tensor uv) {
    {
      LinearFractionalTransform lft = LinearFractionalTransform.fit(xy, uv);
      assertEquals(Tensor.of(xy.stream().map(lft)), uv);
      LinearFractionalTransform lfi = lft.inverse();
      assertEquals(Tensor.of(uv.stream().map(lfi)), xy);
      LinearFractionalTransform lfs = LinearFractionalTransform.fit(uv, xy);
      assertEquals(lfi.matrix(), lfs.matrix());
    }
    {
      LinearFractionalTransform lft = LinearFractionalTransform.fit(uv, xy);
      assertEquals(Tensor.of(uv.stream().map(lft)), xy);
    }
    {
      LinearFractionalTransform lft = LinearFractionalTransform.fit(xy, xy);
      assertEquals(Tensor.of(uv.stream().map(lft)), uv);
      Tensor mat = lft.matrix();
      Tensor res = Times.of(mat, Transpose.of(mat));
      assertEquals(res, IdentityMatrix.of(res));
      Tensor zero = uv.get(0).maps(Scalar::zero);
      assertEquals(zero, lft.apply(zero));
    }
  }

  private static Tensor split(Tensor a, Tensor b, Scalar s) {
    int n = a.length() - 1;
    Tensor matrix = a.dot(MatrixExp.of(MatrixLog.of(LinearSolve.of(a, b)).multiply(s)));
    matrix = matrix.divide(matrix.Get(n, n));
    return matrix;
  }

  @Test
  void testLogExp() {
    Tensor Axy = Tensors.fromString("{{0,0},{1,0},{1,1},{0,1}}");
    Tensor Auv = Tensors.fromString("{{-0.2,0.3},{1.1,0.1},{0.9,0.8},{0.1,1.2}}");
    Tensor Bxy = Tensors.fromString("{{0.1,-0.1},{1.1,0.1},{1.1,1.2},{0.15,1.25}}");
    Tensor Buv = Tensors.fromString("{{-0.25,0.4},{1.2,-0.15},{0.95,0.87},{-0.12,1.23}}");
    Tensor Amat = LinearFractionalTransform.fit(Axy, Auv).matrix();
    Tensor Bmat = LinearFractionalTransform.fit(Bxy, Buv).matrix();
    Scalar s = RealScalar.of(0.5);
    Tensor d1 = split(Amat, Bmat, s);
    Scalar f = RealScalar.of(0.3);
    Tensor d2 = split(Amat.multiply(f), Bmat.multiply(f), s);
    Tolerance.CHOP.requireClose(d1, d2);
  }
}
