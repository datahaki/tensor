// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;

class QuaternionImplTest {
  @Test
  public void testImmutable() {
    Quaternion quaternion = Quaternion.of(1, 3, -2, 2);
    assertThrows(UnsupportedOperationException.class, () -> quaternion.xyz().set(RealScalar.ONE, 1));
  }

  @Test
  public void testPlusReal() {
    Quaternion quaternion = Quaternion.of(3, 1, 2, 5);
    Scalar real = RealScalar.of(4);
    assertEquals(quaternion.add(real), Quaternion.of(7, 1, 2, 5));
    assertEquals(real.add(quaternion), Quaternion.of(7, 1, 2, 5));
  }

  @Test
  public void testPower2() {
    Scalar quaternion = Quaternion.of(1, 3, -2, 2);
    Scalar q2 = Power.of(quaternion, RealScalar.of(2));
    Scalar qm = quaternion.multiply(quaternion);
    Tolerance.CHOP.requireClose(q2, qm);
  }

  @Test
  public void testPower3() {
    Scalar quaternion = Quaternion.of(1, 3, -2, 2);
    Scalar q3 = Power.of(quaternion, RealScalar.of(3));
    Scalar qm = quaternion.multiply(quaternion).multiply(quaternion);
    Tolerance.CHOP.requireClose(q3, qm);
  }

  @Test
  public void testPower3Random() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Scalar quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
      Scalar q3 = Power.of(quaternion, RealScalar.of(3));
      Scalar qm = quaternion.multiply(quaternion).multiply(quaternion);
      Tolerance.CHOP.requireClose(q3, qm);
    }
  }

  @Test
  public void testPowerN1Random() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Scalar quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
      Scalar qr = Power.of(quaternion, RealScalar.of(-1));
      Scalar qm = quaternion.reciprocal();
      Tolerance.CHOP.requireClose(qr, qm);
    }
  }

  @Test
  public void testPowerN2Random() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Scalar quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
      Scalar q2r = Power.of(quaternion, RealScalar.of(-2));
      Scalar qm = quaternion.multiply(quaternion).reciprocal();
      Tolerance.CHOP.requireClose(q2r, qm);
    }
  }

  @Test
  public void testPowerReal() {
    Scalar quaternion = Quaternion.of(3, 0, 0, 0);
    Scalar qm = quaternion.multiply(quaternion);
    Scalar q2 = Power.of(quaternion, RealScalar.of(2));
    Tolerance.CHOP.requireClose(q2, qm);
  }

  @Test
  public void testPower0() {
    assertEquals(Power.of(Quaternion.of(3, 1, 2, 3), RealScalar.ZERO), Quaternion.ONE);
    assertEquals(Power.of(Quaternion.of(3, 0, 0, 0), RealScalar.ZERO), Quaternion.ONE);
    assertEquals(Power.of(Quaternion.of(0, 1, 2, 3), RealScalar.ZERO), Quaternion.ONE);
  }

  @Test
  public void testPowerExact() {
    Scalar scalar = Power.of(Quaternion.of(-2, 1, 2, 3), RealScalar.of(4));
    assertEquals(scalar, Quaternion.of(-124, 80, 160, 240));
    ExactScalarQ.require(scalar);
  }

  @Test
  public void testPowerExactNumeric() {
    Scalar scalar = Power.of(Quaternion.of(-2, 1, 2, 3), Pi.VALUE);
    assertFalse(ExactScalarQ.of(scalar));
  }

  @Test
  public void testPowerXYZ0() {
    Scalar scalar = Power.of(Quaternion.of(2.0, 0, 0, 0), RealScalar.of(3));
    assertEquals(scalar, Quaternion.of(8, 0, 0, 0));
    Quaternion quaternion = (Quaternion) scalar;
    ExactTensorQ.require(quaternion.xyz());
  }

  @Test
  public void testUnaffected() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Quaternion quaternion = Quaternion.of(RealScalar.ZERO, vector);
    vector.set(RealScalar.ZERO, 1);
    assertEquals(quaternion.xyz(), Tensors.vector(1, 2, 3));
  }

  @Test
  public void testPlusFail() {
    Scalar quaternion = Quaternion.of(1, 3, -2, 2);
    Scalar quantity = Quantity.of(1, "m");
    assertThrows(TensorRuntimeException.class, () -> quaternion.add(quantity));
    assertThrows(TensorRuntimeException.class, () -> quantity.add(quaternion));
  }

  @Test
  public void testMultiplyFail() {
    Scalar quaternion = Quaternion.of(1, 3, -2, 2);
    Scalar gaussScalar = GaussScalar.of(3, 11);
    assertFalse(quaternion.equals(gaussScalar));
    assertFalse(gaussScalar.equals(quaternion));
    assertThrows(TensorRuntimeException.class, () -> quaternion.multiply(gaussScalar));
    assertThrows(TensorRuntimeException.class, () -> gaussScalar.multiply(quaternion));
  }

  @Test
  public void testAbs() {
    Scalar scalar = Abs.FUNCTION.apply(Quaternion.of(2, 2, 4, 5));
    assertEquals(ExactScalarQ.require(scalar), RealScalar.of(7));
  }

  @Test
  public void testAbsSquared() {
    Scalar scalar = AbsSquared.FUNCTION.apply(Quaternion.of(1, 2, 3, 4));
    assertEquals(ExactScalarQ.require(scalar), RealScalar.of(30));
  }

  @Test
  public void testNumberFail() {
    Quaternion quaternion = Quaternion.of(1, 3, -2, 2);
    assertThrows(TensorRuntimeException.class, () -> quaternion.number());
  }

  @Test
  public void testSign() {
    Scalar q1 = Quaternion.of(Double.MIN_VALUE, 0, Double.MIN_VALUE, 0);
    Scalar abs = Abs.FUNCTION.apply(q1);
    assertEquals(abs, RealScalar.of(Double.MIN_VALUE));
    Scalar scalar = Sign.FUNCTION.apply(q1);
    Tolerance.CHOP.requireClose(scalar, Quaternion.of(0.7071067811865475, 0, 0.7071067811865475, 0));
  }

  @Test
  public void testEquals() {
    Quaternion q0 = Quaternion.of(1, 3, -2, 2);
    Quaternion q1 = Quaternion.of(1, 3, -2, 2);
    Quaternion q2 = Quaternion.of(1, 3, -2, 4);
    assertTrue(q0.equals(q1));
    assertFalse(q1.equals(q2));
  }

  @Test
  public void testEqualsQR() {
    Quaternion q0 = Quaternion.of(3, 0, 0, 0);
    Scalar q1 = RealScalar.of(3);
    assertTrue(q0.equals(q1));
    assertTrue(q1.equals(q0));
  }

  @Test
  public void testEqualsQRFalse() {
    Quaternion q0 = Quaternion.of(3, 0, 1, 0);
    Scalar q1 = RealScalar.of(3);
    assertFalse(q0.equals(q1));
    assertFalse(q1.equals(q0));
  }

  @Test
  public void testHashcode() {
    Tensor tensor = Tensors.of( //
        Quaternion.of(1, 3, -2, 2), //
        Quaternion.of(3, 1, -2, 2), //
        Quaternion.of(3, 2, -2, 1), //
        Quaternion.of(1, 3, 2, -2));
    long count = tensor.stream().mapToInt(Tensor::hashCode).distinct().count();
    assertEquals(count, tensor.length());
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    Scalar q2 = Serialization.copy(q1);
    assertEquals(q1, q2);
  }

  @Test
  public void testQuantity() {
    Quaternion quaternion = Quaternion.of( //
        Quantity.of(1, "m"), //
        Quantity.of(2, "m"), //
        Quantity.of(3, "m"), //
        Quantity.of(4, "m"));
    Scalar one = quaternion.one();
    assertEquals(quaternion, quaternion.multiply(one));
  }

  @Test
  public void testQuantity2() {
    Scalar scalar = Quantity.of(Quaternion.of(1, 2, 3, 4), "m");
    Scalar one = scalar.one();
    Scalar product = scalar.multiply(one);
    assertEquals(scalar, product);
  }

  @Test
  public void testGaussScalar() {
    Scalar q1 = Quaternion.of( //
        GaussScalar.of(11, 23), //
        GaussScalar.of(3, 23), //
        GaussScalar.of(8, 23), //
        GaussScalar.of(20, 23));
    Scalar q2 = q1.reciprocal();
    Scalar res = q1.multiply(q2);
    assertEquals(res, q1.one());
    ExactScalarQ.require(q2);
  }

  @Test
  public void testToString() {
    Quaternion quaternion = Quaternion.of(1, 2, 3, 4);
    String string = quaternion.toString();
    assertEquals(string, "{\"w\": 1, \"xyz\": {2, 3, 4}}");
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QuaternionImpl.class.getModifiers()));
  }
}
