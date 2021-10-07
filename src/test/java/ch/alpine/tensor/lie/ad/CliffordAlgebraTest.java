// code by jph
package ch.alpine.tensor.lie.ad;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CliffordAlgebraTest extends TestCase {
  public void testD0() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.positive(0);
    assertEquals(cliffordAlgebra.gp(), Tensors.fromString("{{{1}}}"));
  }

  public void testDnegativeFail() {
    AssertFail.of(() -> CliffordAlgebra.positive(-1));
  }

  public void testD1() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.positive(1);
    assertEquals(cliffordAlgebra.gp(), Tensors.fromString("{{{1, 0}, {0, 1}}, {{0, 1}, {1, 0}}}"));
    assertEquals(cliffordAlgebra.gp(), CliffordAlgebra.of(1, 0).gp());
  }

  public void testD1Complex() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.negative(1);
    assertEquals(cliffordAlgebra.gp(), Tensors.fromString("{{{1, 0}, {0, -1}}, {{0, 1}, {1, 0}}}"));
    assertEquals(cliffordAlgebra.gp(), CliffordAlgebra.of(0, 1).gp());
    Tensor x = Tensors.vector(2, 5);
    Tensor y = Tensors.vector(1, -3);
    Tensor m = cliffordAlgebra.gp(x, y);
    Scalar cx = ComplexScalar.of(2, 5);
    Scalar cy = ComplexScalar.of(1, -3);
    Scalar cm = cx.multiply(cy);
    assertEquals(Real.of(cm), m.Get(0));
    assertEquals(Imag.of(cm), m.Get(1));
    Scalar ce = Exp.FUNCTION.apply(cx);
    Tensor xe = cliffordAlgebra.exp(x);
    Chop._10.requireClose(Real.of(ce), xe.Get(0));
    Chop._10.requireClose(Imag.of(ce), xe.Get(1));
    Scalar cr = cx.reciprocal();
    Tensor xr = cliffordAlgebra.reciprocal(x);
    ExactTensorQ.require(cr);
    ExactTensorQ.require(xr);
    assertEquals(Real.of(cr), xr.Get(0));
    assertEquals(Imag.of(cr), xr.Get(1));
  }

  public void testD2() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.positive(2);
    Tensor gp = cliffordAlgebra.gp();
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

  public void testD2Quaternions() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.negative(2);
    Tensor x = Tensors.vector(2, 5, -3, -4);
    Tensor y = Tensors.vector(1, 8, 4, 9);
    Tensor m = cliffordAlgebra.gp(x, y);
    Quaternion q1 = Quaternion.of(2, 5, -3, -4);
    Quaternion q2 = Quaternion.of(1, 8, 4, 9);
    Quaternion qm = q1.multiply(q2);
    assertEquals(qm.xyz(), m.extract(1, 4));
    assertEquals(qm.w(), m.Get(0));
    Quaternion qe = q1.exp();
    Tensor xe = cliffordAlgebra._exp(N.DOUBLE.of(x));
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

  public void testD3() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.positive(3);
    Tensor gp = cliffordAlgebra.gp();
    Tensor x = Tensors.vector(1, 2, 3, 4, 5, -3, -4, -1);
    Tensor m = gp.dot(x);
    Tensor mi = LinearSolve.of(m, UnitVector.of(8, 0));
    assertEquals(mi, cliffordAlgebra.reciprocal(x));
    assertEquals(cliffordAlgebra.grade(x, 0), Tensors.vector(1, 0, 0, 0, 0, 0, 0, 0));
    assertEquals(cliffordAlgebra.grade(x, 1), Tensors.vector(0, 2, 3, 4, 0, 0, 0, 0));
    assertEquals(cliffordAlgebra.grade(x, 2), Tensors.vector(0, 0, 0, 0, 5, -3, -4, 0));
    assertEquals(cliffordAlgebra.grade(x, 3), Tensors.vector(0, 0, 0, 0, 0, 0, 0, -1));
    AssertFail.of(() -> cliffordAlgebra.grade(x, 4));
  }

  public void testD3Quaternions() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.negative(3);
    Tensor x = Tensors.vector(2, 0, 0, 0, 5, -3, -4, 0);
    Tensor y = Tensors.vector(1, 0, 0, 0, 8, 4, 9, 0);
    Tensor m = cliffordAlgebra.gp(x, y);
    Quaternion q1 = Quaternion.of(2, 5, -3, -4);
    Quaternion q2 = Quaternion.of(1, 8, 4, 9);
    Quaternion qm = q1.multiply(q2);
    assertEquals(qm.xyz(), m.extract(1 + 3, 1 + 3 + 3));
    assertEquals(qm.w(), m.Get(0));
    Quaternion qe = q1.exp();
    Tensor xe = cliffordAlgebra._exp(N.DOUBLE.of(x));
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

  public void testD4() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.positive(4);
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

  public void testExp() {
    CliffordAlgebra cliffordAlgebra = CliffordAlgebra.positive(3);
    Tensor a = RandomVariate.of(UniformDistribution.unit(), 8);
    Tensor exp1 = cliffordAlgebra.exp(a);
    Tensor exp2 = cliffordAlgebra._exp(a);
    Chop._08.requireClose(exp1, exp2);
  }

  public void testCommutatorPositive() {
    for (int p = 0; p <= 2; ++p)
      for (int q = 0; q < 2; ++q) {
        CliffordAlgebra cliffordAlgebra = CliffordAlgebra.of(p, q);
        JacobiIdentity.require(cliffordAlgebra.cp());
      }
  }

  public void testCommutatorNegative() {
    for (int n = 0; n <= 3; ++n) {
      CliffordAlgebra cliffordAlgebra = CliffordAlgebra.negative(n);
      JacobiIdentity.require(cliffordAlgebra.cp());
    }
  }
}
