// code by jph
package ch.alpine.tensor.lie.rot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Cosh;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinh;

class QuaternionImplTest {
  @Test
  void testImmutable() {
    Quaternion quaternion = Quaternion.of(1, 3, -2, 2);
    assertThrows(UnsupportedOperationException.class, () -> quaternion.xyz().set(RealScalar.ONE, 1));
  }

  @Test
  void testPlusReal() {
    Quaternion quaternion = Quaternion.of(3, 1, 2, 5);
    Scalar real = RealScalar.of(4);
    assertEquals(quaternion.add(real), Quaternion.of(7, 1, 2, 5));
    assertEquals(real.add(quaternion), Quaternion.of(7, 1, 2, 5));
  }

  @Test
  void testPower2() {
    Scalar quaternion = Quaternion.of(1, 3, -2, 2);
    Scalar q2 = Power.of(quaternion, RealScalar.of(2));
    Scalar qm = quaternion.multiply(quaternion);
    Tolerance.CHOP.requireClose(q2, qm);
  }

  @Test
  void testPower3() {
    Scalar quaternion = Quaternion.of(1, 3, -2, 2);
    Scalar q3 = Power.of(quaternion, RealScalar.of(3));
    Scalar qm = quaternion.multiply(quaternion).multiply(quaternion);
    Tolerance.CHOP.requireClose(q3, qm);
  }

  @ParameterizedTest
  @MethodSource(value = "test.TestDistributions#distributions2")
  void testPower3Random(Distribution distribution) {
    Scalar quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
    Scalar q3 = Power.of(quaternion, 3);
    Scalar qm = quaternion.multiply(quaternion).multiply(quaternion);
    Tolerance.CHOP.requireClose(q3, qm);
  }

  @ParameterizedTest
  @MethodSource(value = "test.TestDistributions#distributions")
  void testPowerN1Random(Distribution distribution) {
    Scalar quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
    Scalar qr = Power.of(quaternion, -1);
    Scalar qm = quaternion.reciprocal();
    Tolerance.CHOP.requireClose(qr, qm);
  }

  @ParameterizedTest
  @MethodSource(value = "test.TestDistributions#distributions")
  void testPowerN2Random(Distribution distribution) {
    Scalar quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
    Scalar q2r = Power.of(quaternion, -2);
    Scalar qm = quaternion.multiply(quaternion).reciprocal();
    Tolerance.CHOP.requireClose(q2r, qm);
  }

  @Test
  void testPowerReal() {
    Scalar quaternion = Quaternion.of(3, 0, 0, 0);
    Scalar qm = quaternion.multiply(quaternion);
    Scalar q2 = Power.of(quaternion, RealScalar.of(2));
    Tolerance.CHOP.requireClose(q2, qm);
  }

  @Test
  void testPower0() {
    assertEquals(Power.of(Quaternion.of(3, 1, 2, 3), RealScalar.ZERO), Quaternion.ONE);
    assertEquals(Power.of(Quaternion.of(3, 0, 0, 0), RealScalar.ZERO), Quaternion.ONE);
    assertEquals(Power.of(Quaternion.of(0, 1, 2, 3), RealScalar.ZERO), Quaternion.ONE);
  }

  @Test
  void testPowerExact() {
    Scalar scalar = Power.of(Quaternion.of(-2, 1, 2, 3), RealScalar.of(4));
    assertEquals(scalar, Quaternion.of(-124, 80, 160, 240));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testPowerExactNumeric() {
    Scalar scalar = Power.of(Quaternion.of(-2, 1, 2, 3), Pi.VALUE);
    assertFalse(ExactScalarQ.of(scalar));
  }

  @Test
  void testPowerXYZ0() {
    Scalar scalar = Power.of(Quaternion.of(2.0, 0, 0, 0), RealScalar.of(3));
    assertEquals(scalar, Quaternion.of(8, 0, 0, 0));
    Quaternion quaternion = (Quaternion) scalar;
    ExactTensorQ.require(quaternion.xyz());
  }

  @Test
  void testUnaffected() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Quaternion quaternion = Quaternion.of(RealScalar.ZERO, vector);
    vector.set(RealScalar.ZERO, 1);
    assertEquals(quaternion.xyz(), Tensors.vector(1, 2, 3));
  }

  @Test
  void testPlusFail() {
    Scalar quaternion = Quaternion.of(1, 3, -2, 2);
    Scalar quantity = Quantity.of(1, "m");
    assertThrows(Throw.class, () -> quaternion.add(quantity));
    assertThrows(Throw.class, () -> quantity.add(quaternion));
  }

  @Test
  void testMultiplyFail() {
    Scalar quaternion = Quaternion.of(1, 3, -2, 2);
    Scalar gaussScalar = GaussScalar.of(3, 11);
    assertNotEquals(quaternion, gaussScalar);
    assertNotEquals(gaussScalar, quaternion);
    assertThrows(Throw.class, () -> quaternion.multiply(gaussScalar));
    assertThrows(Throw.class, () -> gaussScalar.multiply(quaternion));
  }

  @Test
  void testAbs() {
    Scalar scalar = Abs.FUNCTION.apply(Quaternion.of(2, 2, 4, 5));
    assertEquals(ExactScalarQ.require(scalar), RealScalar.of(7));
  }

  @Test
  void testAbsSquared() {
    Scalar scalar = AbsSquared.FUNCTION.apply(Quaternion.of(1, 2, 3, 4));
    assertEquals(ExactScalarQ.require(scalar), RealScalar.of(30));
  }

  @Test
  void testNumberFail() {
    Quaternion quaternion = Quaternion.of(1, 3, -2, 2);
    assertThrows(Throw.class, quaternion::number);
  }

  @Test
  void testSign() {
    Scalar q1 = Quaternion.of(Double.MIN_VALUE, 0, Double.MIN_VALUE, 0);
    Scalar abs = Abs.FUNCTION.apply(q1);
    assertEquals(abs, RealScalar.of(Double.MIN_VALUE));
    Scalar scalar = Sign.FUNCTION.apply(q1);
    Tolerance.CHOP.requireClose(scalar, Quaternion.of(0.7071067811865475, 0, 0.7071067811865475, 0));
  }

  @Test
  void testEquals() {
    Quaternion q0 = Quaternion.of(1, 3, -2, 2);
    Quaternion q1 = Quaternion.of(1, 3, -2, 2);
    Quaternion q2 = Quaternion.of(1, 3, -2, 4);
    assertEquals(q0, q1);
    assertNotEquals(q1, q2);
  }

  @Test
  void testEqualsQR() {
    Quaternion q0 = Quaternion.of(3, 0, 0, 0);
    Scalar q1 = RealScalar.of(3);
    assertEquals(q0, q1);
    assertEquals(q1, q0);
  }

  @Test
  void testEqualsQRFalse() {
    Quaternion q0 = Quaternion.of(3, 0, 1, 0);
    Scalar q1 = RealScalar.of(3);
    assertNotEquals(q0, q1);
    assertNotEquals(q1, q0);
  }

  @Test
  void testHashcode() {
    Tensor tensor = Tensors.of( //
        Quaternion.of(1, 3, -2, 2), //
        Quaternion.of(3, 1, -2, 2), //
        Quaternion.of(3, 2, -2, 1), //
        Quaternion.of(1, 3, 2, -2));
    long count = tensor.stream().mapToInt(Tensor::hashCode).distinct().count();
    assertEquals(count, tensor.length());
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    Scalar q2 = Serialization.copy(q1);
    assertEquals(q1, q2);
  }

  @Test
  void testQuantity() {
    Quaternion quaternion = Quaternion.of( //
        Quantity.of(1, "m"), //
        Quantity.of(2, "m"), //
        Quantity.of(3, "m"), //
        Quantity.of(4, "m"));
    Scalar one = quaternion.one();
    assertEquals(quaternion, quaternion.multiply(one));
  }

  @Test
  void testQuantity2() {
    Scalar scalar = Quantity.of(Quaternion.of(1, 2, 3, 4), "m");
    Scalar one = scalar.one();
    Scalar product = scalar.multiply(one);
    assertEquals(scalar, product);
  }

  @Test
  void testGaussScalar() {
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
  void testTrigonometryExact() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    Quaternion sin = Quaternion.of( //
        25.987532783271178, 12.134775109731375, -8.089850073154249, 8.089850073154249); // mathematica
    Tolerance.CHOP.requireClose(Sin.FUNCTION.apply(q1), sin);
    Quaternion cos = Quaternion.of( //
        16.686402906489768, -18.898792492845683, 12.599194995230455, -12.599194995230455); // mathematica
    Tolerance.CHOP.requireClose(Cos.FUNCTION.apply(q1), cos);
    Quaternion sinh = Quaternion.of( //
        -0.6531361502064957, -0.9333911168998251, 0.6222607445998833, -0.6222607445998833);
    Tolerance.CHOP.requireClose(Sinh.FUNCTION.apply(q1), sinh);
    Quaternion cosh = Quaternion.of( //
        -0.8575908114563202, -0.710865219851931, 0.47391014656795394, -0.47391014656795394);
    Tolerance.CHOP.requireClose(Cosh.FUNCTION.apply(q1), cosh);
  }

  @Test
  void testTrigonometryNumeric() {
    Scalar q1 = Quaternion.of(1, 3., -2, 2);
    Quaternion sin = Quaternion.of( //
        25.987532783271178, 12.134775109731375, -8.089850073154249, 8.089850073154249); // mathematica
    Tolerance.CHOP.requireClose(Sin.FUNCTION.apply(q1), sin);
    Quaternion cos = Quaternion.of( //
        16.686402906489768, -18.898792492845683, 12.599194995230455, -12.599194995230455); // mathematica
    Tolerance.CHOP.requireClose(Cos.FUNCTION.apply(q1), cos);
    Quaternion sinh = Quaternion.of( //
        -0.6531361502064957, -0.9333911168998251, 0.6222607445998833, -0.6222607445998833);
    Tolerance.CHOP.requireClose(Sinh.FUNCTION.apply(q1), sinh);
    Quaternion cosh = Quaternion.of( //
        -0.8575908114563202, -0.710865219851931, 0.47391014656795394, -0.47391014656795394);
    Tolerance.CHOP.requireClose(Cosh.FUNCTION.apply(q1), cosh);
  }

  @Test
  void testToString() {
    Quaternion quaternion = Quaternion.of(1, 2, 3, 4);
    String string = quaternion.toString();
    // assertEquals(string, "{\"w\": 1, \"xyz\": {2, 3, 4}}");
    assertEquals(string, "Quaternion[1, {2, 3, 4}]");
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QuaternionImpl.class.getModifiers()));
  }
}
