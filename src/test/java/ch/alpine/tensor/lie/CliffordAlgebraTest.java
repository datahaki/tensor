// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Numel;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.spa.Nnz;
import ch.alpine.tensor.spa.SparseArray;

class CliffordAlgebraTest {
  @Test
  void testD0() throws ClassNotFoundException, IOException {
    CliffordAlgebra cliffordAlgebra = Serialization.copy(CliffordAlgebraCache.positive(0));
    assertEquals(cliffordAlgebra.gp(), Tensors.fromString("{{{1}}}"));
  }

  @Test
  void testDnegativeFail() {
    assertThrows(Exception.class, () -> CliffordAlgebraCache.positive(-1));
  }

  @Test
  void testD1() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.positive(1);
    assertEquals(cliffordAlgebra.gp(), Tensors.fromString("{{{1, 0}, {0, 1}}, {{0, 1}, {1, 0}}}"));
    assertEquals(cliffordAlgebra.gp(), CliffordAlgebraCache.of(1, 0).gp());
  }

  @Test
  void testD1Complex() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.negative(1);
    assertEquals(cliffordAlgebra.gp(), Tensors.fromString("{{{1, 0}, {0, -1}}, {{0, 1}, {1, 0}}}"));
    assertEquals(cliffordAlgebra.gp(), CliffordAlgebraCache.of(0, 1).gp());
    Tensor x = Tensors.vector(2, 5);
    Tensor y = Tensors.vector(1, -3);
    Tensor m = cliffordAlgebra.gp(x, y);
    Scalar cx = ComplexScalar.of(2, 5);
    Scalar cy = ComplexScalar.of(1, -3);
    Scalar cm = cx.multiply(cy);
    assertEquals(Re.FUNCTION.apply(cm), m.Get(0));
    assertEquals(Im.FUNCTION.apply(cm), m.Get(1));
    Scalar ce = Exp.FUNCTION.apply(cx);
    Tensor xe = cliffordAlgebra.exp(x);
    Chop._10.requireClose(Re.FUNCTION.apply(ce), xe.Get(0));
    Chop._10.requireClose(Im.FUNCTION.apply(ce), xe.Get(1));
    Scalar cr = cx.reciprocal();
    Tensor xr = cliffordAlgebra.reciprocal(x);
    ExactTensorQ.require(cr);
    ExactTensorQ.require(xr);
    assertEquals(Re.FUNCTION.apply(cr), xr.Get(0));
    assertEquals(Im.FUNCTION.apply(cr), xr.Get(1));
  }

  @Test
  void testD2() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.positive(2);
    Tensor gp = cliffordAlgebra.gp();
    assertEquals(Nnz.of((SparseArray) gp), 16);
    SparseArray cp = (SparseArray) cliffordAlgebra.cp();
    assertEquals(Nnz.of(cp), 6);
    assertEquals(Numel.of(cp), 64);
    assertInstanceOf(SparseArray.class, JacobiIdentity.of(cp));
    Tensor x = Tensors.vector(1, 2, 3, 4);
    Tensor m = gp.dot(x);
    LinearSolve.of(m, UnitVector.of(4, 0));
    Tensor xi = cliffordAlgebra.reciprocal(x);
    assertEquals(Dot.of(gp, x, xi), UnitVector.of(4, 0));
    assertEquals(Dot.of(gp, xi, x), UnitVector.of(4, 0));
    assertEquals(Dot.of(gp, Tensors.vector(1, 0, 0, 0), Tensors.vector(0, 1, 0, 0)), UnitVector.of(4, 1));
    assertEquals(Dot.of(gp, Tensors.vector(0, 1, 0, 0), Tensors.vector(1, 0, 0, 0)), UnitVector.of(4, 1));
    assertEquals(Dot.of(gp, Tensors.vector(0, 1, 0, 0), Tensors.vector(0, 1, 0, 0)), UnitVector.of(4, 0));
    assertEquals(Dot.of(gp, Tensors.vector(0, 0, 1, 0), Tensors.vector(0, 0, 1, 0)), UnitVector.of(4, 0));
    assertEquals(Dot.of(gp, Tensors.vector(0, 1, 0, 0), Tensors.vector(0, 0, 1, 0)), UnitVector.of(4, 3));
    Tensor res = TensorWedge.of(Tensors.vector(1, 0), Tensors.vector(0, 1));
    assertEquals(res, Tensors.fromString("{{0, 1}, {-1, 0}}"));
  }

  @PackageTestAccess
  static Tensor _exp(Tensor gp, Tensor a) {
    Tensor sum = UnitVector.of(gp.length(), 0);
    Tensor p = sum;
    for (int k = 1; k < 40; ++k) {
      p = gp.dot(p).dot(a).divide(RealScalar.of(k));
      sum = sum.add(p);
      if (Chop._40.allZero(p))
        break;
    }
    return sum;
  }

  @Test
  void testD2Quaternions() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.negative(2);
    Tensor x = Tensors.vector(2, 5, -3, -4);
    Tensor y = Tensors.vector(1, 8, 4, 9);
    Tensor m = cliffordAlgebra.gp(x, y);
    Quaternion q1 = Quaternion.of(2, 5, -3, -4);
    Quaternion q2 = Quaternion.of(1, 8, 4, 9);
    Quaternion qm = q1.multiply(q2);
    assertEquals(qm.xyz(), m.extract(1, 4));
    assertEquals(qm.w(), m.Get(0));
    Quaternion qe = q1.exp();
    Tensor xe = _exp(cliffordAlgebra.gp(), x.map(N.DOUBLE));
    Tolerance.CHOP.requireClose(qe.xyz(), xe.extract(1, 1 + 3));
    Tolerance.CHOP.requireClose(qe.w(), xe.Get(0));
    Tensor xi = cliffordAlgebra.exp(x);
    Tolerance.CHOP.requireClose(qe.xyz(), xi.extract(1, 1 + 3));
    Tolerance.CHOP.requireClose(qe.w(), xi.Get(0));
    Quaternion qr = q1.reciprocal();
    Tensor xr = cliffordAlgebra.reciprocal(x);
    Tolerance.CHOP.requireClose(qr.xyz(), xr.extract(1, 1 + 3));
    Tolerance.CHOP.requireClose(qr.w(), xr.Get(0));
  }

  @Test
  void testD3() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.positive(3);
    Tensor gp = cliffordAlgebra.gp();
    Tensor x = Tensors.vector(1, 2, 3, 4, 5, -3, -4, -1);
    Tensor m = gp.dot(x);
    Tensor mi = LinearSolve.of(m, UnitVector.of(8, 0));
    assertEquals(mi, cliffordAlgebra.reciprocal(x));
    assertEquals(cliffordAlgebra.grade(x, 0), Tensors.vector(1, 0, 0, 0, 0, 0, 0, 0));
    assertEquals(cliffordAlgebra.grade(x, 1), Tensors.vector(0, 2, 3, 4, 0, 0, 0, 0));
    assertEquals(cliffordAlgebra.grade(x, 2), Tensors.vector(0, 0, 0, 0, 5, -3, -4, 0));
    assertEquals(cliffordAlgebra.grade(x, 3), Tensors.vector(0, 0, 0, 0, 0, 0, 0, -1));
    assertThrows(Exception.class, () -> cliffordAlgebra.grade(x, 4));
  }

  @Test
  void testD3Quaternions() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.negative(3);
    Tensor x = Tensors.vector(2, 0, 0, 0, 5, -3, -4, 0);
    Tensor y = Tensors.vector(1, 0, 0, 0, 8, 4, 9, 0);
    Tensor m = cliffordAlgebra.gp(x, y);
    Quaternion q1 = Quaternion.of(2, 5, -3, -4);
    Quaternion q2 = Quaternion.of(1, 8, 4, 9);
    Quaternion qm = q1.multiply(q2);
    assertEquals(qm.xyz(), m.extract(1 + 3, 1 + 3 + 3));
    assertEquals(qm.w(), m.Get(0));
    Quaternion qe = q1.exp();
    Tensor xe = _exp(cliffordAlgebra.gp(), x.map(N.DOUBLE));
    Tolerance.CHOP.requireClose(qe.xyz(), xe.extract(1 + 3, 1 + 3 + 3));
    Tolerance.CHOP.requireClose(qe.w(), xe.Get(0));
    Tensor xi = cliffordAlgebra.exp(x);
    Tolerance.CHOP.requireClose(qe.xyz(), xi.extract(1 + 3, 1 + 3 + 3));
    Tolerance.CHOP.requireClose(qe.w(), xi.Get(0));
    Quaternion qr = q1.reciprocal();
    Tensor xr = cliffordAlgebra.reciprocal(x);
    Tolerance.CHOP.requireClose(qr.xyz(), xr.extract(1 + 3, 1 + 3 + 3));
    Tolerance.CHOP.requireClose(qr.w(), xr.Get(0));
  }

  @Test
  void testD4() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.positive(4);
    Tensor x = Array.zeros(16);
    Tensor y = Array.zeros(16);
    for (int index = 1 + 4; index < 1 + 4 + 6; ++index) {
      x.set(RandomVariate.of(DiscreteUniformDistribution.of(-9, 9)), index);
      y.set(RandomVariate.of(DiscreteUniformDistribution.of(-9, 9)), index);
    }
    Tensor tensor = cliffordAlgebra.gp(x, y);
    Chop.NONE.requireAllZero(tensor.extract(1, 1 + 4));
    Chop.NONE.requireAllZero(tensor.extract(1 + 4 + 6, 15));
  }

  @Test
  void testExp() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.positive(3);
    Tensor a = RandomVariate.of(UniformDistribution.unit(), 8);
    Tensor exp1 = cliffordAlgebra.exp(a);
    Tensor exp2 = _exp(cliffordAlgebra.gp(), a);
    Chop._08.requireClose(exp1, exp2);
  }

  @Test
  void testReverse() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.positive(2);
    assertEquals(cliffordAlgebra.gp().length(), 4);
    Tensor result = Tensor.of(IdentityMatrix.of(4).stream().map(cliffordAlgebra::reverse));
    assertEquals(result, DiagonalMatrix.of(1, 1, 1, -1));
  }

  @Test
  void testReverseFail() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.positive(2);
    assertThrows(Exception.class, () -> cliffordAlgebra.reverse(Pi.VALUE));
    assertThrows(Exception.class, () -> cliffordAlgebra.reverse(HilbertMatrix.of(4)));
  }

  @Test
  void testCommutator() {
    for (int p = 0; p <= 2; ++p)
      for (int q = 0; q < 2; ++q) {
        CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.of(p, q);
        Tensor cp = cliffordAlgebra.cp();
        assertInstanceOf(SparseArray.class, cp);
        JacobiIdentity.require(cp);
        Tensor x = RandomVariate.of(DiscreteUniformDistribution.of(-10, +10), cp.length());
        Tensor y = RandomVariate.of(DiscreteUniformDistribution.of(-10, +10), cp.length());
        assertEquals(cliffordAlgebra.cp(x, y), cp.dot(x).dot(y));
      }
  }

  @Test
  void testCommutatorNegative() {
    for (int n = 1; n <= 3; ++n) {
      CliffordAlgebra cliffordAlgebra = CliffordAlgebraCache.negative(n);
      JacobiIdentity.require(cliffordAlgebra.cp());
    }
  }

  @Test
  void testEvenGradeSubalgebra() {
    // vectors of even grade form subalgebra
    for (int i = 0; i <= 4; ++i) {
      CliffordAlgebra ca = CliffordAlgebraCache.of(i, 4 - i);
      Tensor gp = ca.gp();
      assertEquals(gp.length(), 16);
      int[] cmp = new int[] { 1, 2, 3, 4, 11, 12, 13, 14 };
      int[] sub = new int[] { 0, 5, 6, 7, 8, 9, 10, 15 };
      Tensor ex = Tensor.of(IntStream.of(cmp).mapToObj(gp::get) //
          .map(slice -> Tensor.of(IntStream.of(sub).mapToObj(slice::get) //
              .map(b -> Tensor.of(IntStream.of(sub).mapToObj(b::get))))));
      Chop.NONE.requireAllZero(ex);
    }
  }
}
