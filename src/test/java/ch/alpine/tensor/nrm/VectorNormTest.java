// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class VectorNormTest extends TestCase {
  public void testOdd() {
    Tensor tensor = Tensors.vector(2.3, 1.0, 3.2);
    Scalar n = VectorNorm.of(1.5).apply(tensor);
    // 4.7071
    assertEquals(n, RealScalar.of(4.707100665786122));
  }

  public void testNormP() {
    Scalar n = VectorNorm.of(1.23).apply(Tensors.vector(1, 2, 3));
    assertEquals(n, RealScalar.of(4.982125211204371));
  }

  public void testNormalize() {
    TensorScalarFunction vni = VectorNorm.of(2.6);
    Tensor nrm = Normalize.with(vni).apply(Tensors.vector(1, 2, 3));
    Chop._15.requireClose(vni.apply(nrm), RealScalar.ONE);
  }

  public void testNormalize2() {
    Distribution distribution = NormalDistribution.standard();
    TensorScalarFunction vni = VectorNorm.of(3.4);
    Tensor vector = RandomVariate.of(distribution, 1000);
    Tensor result = Normalize.with(vni).apply(vector);
    Scalar norm = vni.apply(result);
    Chop._15.requireClose(norm, RealScalar.ONE);
  }

  public void testQuantity1() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    Scalar lhs = VectorNorm.of(RationalScalar.of(7, 3)).apply(vec);
    Scalar rhs = Quantity.of(4.774145448367236, "m");
    Chop._13.requireClose(lhs, rhs);
  }

  public void testQuantity2() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    Scalar lhs = VectorNorm.of(Math.PI).apply(vec); // the result has unit [m^1.0]
    Scalar rhs = Quantity.of(4.457284396597481, "m");
    Chop._13.requireClose(lhs, rhs);
  }

  public void testToString() {
    TensorScalarFunction tensorScalarFunction = VectorNorm.of(3);
    String string = tensorScalarFunction.toString();
    assertTrue(string.startsWith("VectorNorm["));
  }

  public void testNormPFail() {
    AssertFail.of(() -> VectorNorm.of(0.99));
  }

  public void testMatrixFail() {
    TensorScalarFunction tensorScalarFunction = VectorNorm.of(2.6);
    AssertFail.of(() -> tensorScalarFunction.apply(IdentityMatrix.of(2)));
  }

  public void testScalarFail() {
    TensorScalarFunction tensorScalarFunction = VectorNorm.of(2.6);
    AssertFail.of(() -> tensorScalarFunction.apply(RealScalar.of(12)));
  }

  public void testPNullFail() {
    AssertFail.of(() -> VectorNorm.of((Number) null));
    AssertFail.of(() -> VectorNorm.of((Scalar) null));
  }
}
