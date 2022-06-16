// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.ArgMax;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

class GaussScalarTest {
  @Test
  void testReciprocal() {
    long prime = 7919;
    for (int v = 1; v < prime; ++v) {
      Scalar num = GaussScalar.of(v, prime);
      Scalar inv = num.reciprocal();
      assertEquals(num.multiply(inv), GaussScalar.of(1, prime));
      assertEquals(inv.multiply(num), GaussScalar.of(1, prime));
    }
  }

  @Test
  void testDivideUnder() {
    GaussScalar num = GaussScalar.of(132, 193);
    GaussScalar den = GaussScalar.of(37, 193);
    Scalar div1 = num.divide(den);
    Scalar div2 = den.under(num);
    assertEquals(div1, div2);
  }

  @Test
  void testGetter() {
    GaussScalar num = GaussScalar.of(32, 193);
    assertEquals(num.number().intValue(), 32);
    assertEquals(num.number().longValue(), 32);
    assertEquals(num.prime(), BigInteger.valueOf(193));
  }

  @Test
  void testMatrix1() {
    Tensor m = Tensors.matrix(new Scalar[][] { //
        { GaussScalar.of(0, 7), GaussScalar.of(3, 7) }, //
        { GaussScalar.of(1, 7), GaussScalar.of(3, 7) } //
    });
    Tensor b = Tensors.matrix(new Scalar[][] { //
        { GaussScalar.of(6, 7), GaussScalar.of(4, 7) }, //
        { GaussScalar.of(5, 7), GaussScalar.of(0, 7) } //
    });
    Tensor a = LinearSolve.of(m, b, Pivots.FIRST_NON_ZERO);
    assertEquals(m.dot(a), b);
  }

  @Test
  void testNegativePrime() {
    Scalar a = GaussScalar.of(2, 7);
    Scalar b = GaussScalar.of(3, 7);
    assertEquals(GaussScalar.of(-2, 7), a.add(b));
  }

  @Test
  void testMultiplyQuantity() {
    Scalar a = GaussScalar.of(4, 13);
    Scalar b = Quantity.of(GaussScalar.of(7, 13), "some");
    Scalar ab = a.multiply(b);
    Scalar ba = b.multiply(a);
    assertEquals(ab, ba);
  }

  @Test
  void testSqrt() {
    Scalar a = GaussScalar.of(4, 7);
    Scalar s = GaussScalar.of(2, 7);
    assertEquals(Sqrt.of(a), s);
  }

  @Test
  void testSqrt0() {
    Scalar zero = GaussScalar.of(0, 7);
    assertEquals(Sqrt.of(zero), zero);
  }

  @Test
  void testSqrt11() {
    int prime = 11;
    int count = 0;
    for (int c = 0; c < prime; ++c) {
      Scalar s = GaussScalar.of(c, prime);
      try {
        Scalar sqrt = Sqrt.of(s);
        ++count;
        assertEquals(sqrt.multiply(sqrt), s);
      } catch (Exception exception) {
        // ---
      }
    }
    assertEquals(count, 6);
    assertThrows(TensorRuntimeException.class, () -> Sqrt.of(GaussScalar.of(2, 11)));
  }

  @Test
  void testSqrt5() {
    assertEquals(Sqrt.of(GaussScalar.of(1, 5)), GaussScalar.of(1, 5));
    assertEquals(Sqrt.of(GaussScalar.of(1, 5)), GaussScalar.of(1, 5));
    assertThrows(TensorRuntimeException.class, () -> Sqrt.of(GaussScalar.of(2, 5)));
    assertThrows(TensorRuntimeException.class, () -> Sqrt.of(GaussScalar.of(3, 5)));
  }

  @Test
  void testNumber() {
    Scalar scalar = GaussScalar.of(9, 23);
    assertInstanceOf(BigInteger.class, scalar.number());
  }

  @Test
  void testSort() {
    Tensor v = Tensors.of(GaussScalar.of(4, 7), GaussScalar.of(1, 7), GaussScalar.of(2, 7), GaussScalar.of(0, 7));
    Tensor r = Tensors.of(GaussScalar.of(0, 7), GaussScalar.of(1, 7), GaussScalar.of(2, 7), GaussScalar.of(4, 7));
    Tensor s = Sort.of(v);
    assertEquals(s, r);
  }

  @Test
  void testArgMax() {
    Tensor vector = Tensors.of(GaussScalar.of(1, 7), GaussScalar.of(4, 7), GaussScalar.of(2, 7), GaussScalar.of(0, 7));
    int i = ArgMax.of(vector);
    assertEquals(i, 1);
  }

  @Test
  void testPower() {
    int prime = 677;
    Scalar scalar = GaussScalar.of(432, prime);
    Scalar now = GaussScalar.of(1, prime);
    for (int index = 0; index < prime; ++index) {
      assertEquals(Power.of(scalar, index), now);
      now = now.multiply(scalar);
    }
  }

  @Test
  void testPowerNegative() {
    int prime = 677;
    Scalar scalar = GaussScalar.of(432, prime);
    Scalar now = GaussScalar.of(1, prime);
    for (int index = 0; index < prime; ++index) {
      assertEquals(Power.of(scalar, -index), now);
      now = now.divide(scalar);
    }
  }

  @Test
  void testPower2() {
    long prime = 59;
    BinaryPower<Scalar> binaryPower = new BinaryPower<>(ScalarProduct.INSTANCE);
    Random random = new SecureRandom();
    for (int index = 0; index < prime; ++index) {
      GaussScalar gaussScalar = GaussScalar.of(random.nextInt(), prime);
      if (!gaussScalar.number().equals(BigInteger.ZERO))
        for (int exponent = -10; exponent <= 10; ++exponent) {
          Scalar p1 = Power.of(gaussScalar, exponent);
          Scalar p2 = binaryPower.raise(gaussScalar, BigInteger.valueOf(exponent));
          assertEquals(p1, p2);
        }
    }
  }

  @Test
  void testPowerZero() {
    long prime = 43;
    Scalar scalar = GaussScalar.of(1, prime);
    for (int index = 0; index < prime; ++index) {
      GaussScalar gaussScalar = GaussScalar.of(index, prime);
      assertEquals(Power.of(gaussScalar, 0), scalar);
      assertEquals(gaussScalar, //
          Sign.of(gaussScalar).multiply(Abs.of(gaussScalar)));
    }
  }

  @Test
  void testVector2NormSquared() {
    int prime = 107;
    Scalar normSquared = Vector2NormSquared.of(Tensors.of(GaussScalar.of(99, prime)));
    assertEquals(normSquared, GaussScalar.of(64, prime));
  }

  @Test
  void testPowerFail() {
    GaussScalar gaussScalar = GaussScalar.of(3, 107);
    assertEquals(gaussScalar.number(), BigInteger.valueOf(3));
    assertEquals(gaussScalar.prime(), BigInteger.valueOf(107));
    assertThrows(TensorRuntimeException.class, () -> Power.of(gaussScalar, Pi.HALF));
  }

  @Test
  void testSign() {
    assertEquals(Sign.FUNCTION.apply(GaussScalar.of(0, 677)), GaussScalar.of(0, 677));
    assertEquals(Sign.FUNCTION.apply(GaussScalar.of(-432, 677)), GaussScalar.of(1, 677));
  }

  @Test
  void testRounding() {
    Scalar scalar = GaussScalar.of(-432, 677);
    assertEquals(Round.of(scalar), scalar);
    assertEquals(Ceiling.of(scalar), scalar);
    assertEquals(Floor.of(scalar), scalar);
  }

  @Test
  void testSerializable() throws Exception {
    Scalar a = GaussScalar.of(4, 7);
    assertEquals(a, Serialization.parse(Serialization.of(a)));
    assertEquals(a, Serialization.copy(a));
  }

  @Test
  void testHash() {
    Scalar g = GaussScalar.of(4, 7);
    ExactScalarQ.require(g);
    Scalar d = DoubleScalar.of(4.33);
    Scalar z = RealScalar.ZERO;
    Scalar c = ComplexScalar.of(RealScalar.of(2.), RealScalar.of(3.4));
    Set<Scalar> set = new HashSet<>();
    set.add(g);
    set.add(d);
    set.add(z);
    set.add(c);
    assertEquals(set.size(), 4);
  }

  @Test
  void testBinaryOpFail() {
    GaussScalar gs1 = GaussScalar.of(432, 677);
    GaussScalar gs2 = GaussScalar.of(4, 13);
    assertThrows(TensorRuntimeException.class, () -> gs1.multiply(gs2));
    assertThrows(TensorRuntimeException.class, () -> gs1.add(gs2));
    assertThrows(TensorRuntimeException.class, () -> gs1.divide(gs2));
    assertThrows(TensorRuntimeException.class, () -> gs1.under(gs2));
    assertThrows(TensorRuntimeException.class, () -> gs1.compareTo(gs2));
  }

  @Test
  void testHash2() {
    assertFalse(GaussScalar.of(3, 7).hashCode() == GaussScalar.of(7, 3).hashCode());
    assertFalse(GaussScalar.of(1, 7).hashCode() == GaussScalar.of(2, 7).hashCode());
    assertFalse(GaussScalar.of(1, 7).hashCode() == GaussScalar.of(1, 11).hashCode());
  }

  @Test
  void testEquals() {
    assertFalse(GaussScalar.of(3, 7).equals(GaussScalar.of(4, 7)));
    assertFalse(GaussScalar.of(3, 7).equals(GaussScalar.of(3, 11)));
  }

  @Test
  void testEqualsNull() {
    assertFalse(GaussScalar.of(3, 7).equals(null));
  }

  @Test
  void testEqualsMisc() {
    Object object = GaussScalar.of(3, 7);
    assertFalse(object.equals("hello"));
  }

  @Test
  void testToString() {
    String string = GaussScalar.of(3, 7).toString();
    assertTrue(0 < string.indexOf('3'));
    assertTrue(0 < string.indexOf('7'));
    // assertEquals(string, "{\"value\": 3, \"prime\": 7}");
  }

  @Test
  void testDivideZeroFail() {
    Scalar a = GaussScalar.of(3, 13);
    Scalar b = GaussScalar.of(0, 13);
    assertThrows(ArithmeticException.class, () -> a.divide(b));
    assertThrows(ArithmeticException.class, () -> b.under(a));
  }

  @Test
  void testPrimes() {
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/num/primes.vector");
    tensor.extract(3, tensor.length()).stream() //
        .parallel() //
        .map(Scalar.class::cast) //
        .forEach(x -> { // skip 2 3 5
          long prime = x.number().longValue();
          GaussScalar gaussScalar = GaussScalar.of(10, prime);
          GaussScalar inverse = gaussScalar.reciprocal();
          assertEquals(gaussScalar.multiply(inverse).number(), BigInteger.ONE);
        });
  }
}
