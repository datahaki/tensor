// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.IntegerQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.UpperEvaluation;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.gam.Factorial;
import ch.alpine.tensor.sca.ply.Polynomial;

class EllipticCurveTest {
  @Test
  void testN10() {
    assertEquals(EllipticCurve.of(-1, 0).discriminant(), RealScalar.of(64));
    assertEquals(EllipticCurve.of(0, +3).discriminant(), RealScalar.of(-3888));
  }

  @Test
  void testUnits() {
    EllipticCurve ellipticCurve = EllipticCurve.of(Quantity.of(-1, "m^4"), Quantity.of(1, "m^6"));
    List<Tensor> list = Stream.of("{-1[m^2], 1[m^3]}", "{0[m^2], 1[m^3]}", "{1[m^2], 1[m^3]}") //
        .map(Tensors::fromString).toList();
    for (Tensor p : list)
      _check(ellipticCurve, p);
  }

  @Test
  void testCongruent() {
    EllipticCurve ellipticCurve = EllipticCurve.of(-25, 0);
    Tensor p = Tensors.fromString("{25/4, 75/8}");
    assertTrue(ellipticCurve.isMember(p));
    Tensor q = ellipticCurve.combine(p, p);
    Scalar x = q.Get(0);
    Scalar y = q.Get(1);
    Scalar n = RealScalar.of(5);
    Scalar a = x.multiply(x).subtract(n.multiply(n)).divide(y);
    Scalar b = x.multiply(n).multiply(RealScalar.of(2)).divide(y);
    Scalar c = x.multiply(x).add(n.multiply(n)).divide(y);
    Scalar vv = a.multiply(a).add(b.multiply(b));
    Scalar ww = c.multiply(c);
    assertEquals(vv, ww);
    assertEquals(a.multiply(b), n.add(n));
  }

  @Test
  void testN11() {
    EllipticCurve ellipticCurve = EllipticCurve.of(-1, 1);
    assertEquals(ellipticCurve.discriminant(), RealScalar.of(-368));
    List<Tensor> list = Stream.of("{-1, 1}", "{0, 1}", "{1, 1}") //
        .map(Tensors::fromString).toList();
    for (Tensor p : list)
      _check(ellipticCurve, p);
    Tensor p = list.get(0);
    Tensor q = list.get(1);
    Tensor pq = ellipticCurve.combine(p, q);
    Tensor r = list.get(2);
    Tensor invert = ellipticCurve.invert(r);
    assertEquals(pq, invert);
    ellipticCurve.combine(r, r);
    assertEquals(ellipticCurve, ellipticCurve);
    ellipticCurve.hashCode();
    ellipticCurve.toString();
    assertEquals(ellipticCurve.invert(EllipticCurve.NEUTRAL), ellipticCurve.neutral(q));
  }

  @Test
  void testNoRationalSolution() {
    EllipticCurve ellipticCurve = EllipticCurve.of(0, 6);
    ellipticCurve.requireMember(EllipticCurve.NEUTRAL);
    assertEquals(ellipticCurve.discriminant(), RealScalar.of(-15552));
  }

  @Test
  void testP0N63() {
    EllipticCurve ellipticCurve = EllipticCurve.of(0, -63);
    Tensor constraint = ellipticCurve.defect(EllipticCurve.NEUTRAL);
    assertEquals(constraint, RealScalar.ZERO);
    assertTrue(Chop.NONE.allZero(RealScalar.ZERO));
    ellipticCurve.requireMember(EllipticCurve.NEUTRAL);
    Tensor p = Tensors.vector(4, 1);
    assertTrue(ellipticCurve.isMember(p));
    Tensor p2 = ellipticCurve.combine(p, p);
    assertEquals(p2, Tensors.vector(568, -13537));
  }

  private static void _checkInverse(EllipticCurve ellipticCurve, Tensor p) {
    Tensor pi = ellipticCurve.invert(p);
    assertEquals(ellipticCurve.combine(p, pi), EllipticCurve.NEUTRAL);
    assertEquals(ellipticCurve.combine(p, EllipticCurve.NEUTRAL), p);
    assertEquals(ellipticCurve.combine(EllipticCurve.NEUTRAL, p), p);
  }

  @ParameterizedTest
  @ValueSource(strings = { "{2,3}", "{0,1}", "{-1,0}" })
  void testFinite(String point) {
    Tensor p = Tensors.fromString(point);
    EllipticCurve ellipticCurve = EllipticCurve.of(0, 1);
    ellipticCurve.requireMember(EllipticCurve.NEUTRAL);
    ellipticCurve.requireMember(p);
    _checkInverse(ellipticCurve, p);
    _check(ellipticCurve, p);
  }

  @Test
  void testLikeIntegers() {
    EllipticCurve ellipticCurve = EllipticCurve.of(0, -2);
    Tensor p = Tensors.fromString("{3,5}");
    ellipticCurve.requireMember(p);
    Set<Tensor> set = new HashSet<>();
    for (int i = 1; i < 30; ++i)
      assertTrue(set.add(ellipticCurve.raise(p, i)));
  }

  @Test
  void testGoogleEx1() {
    EllipticCurve ellipticCurve = EllipticCurve.of(-4, 1);
    Tensor p = Tensors.vector(0, 1);
    Tensor q = Tensors.vector(4, 7);
    ellipticCurve.requireMember(p);
    ellipticCurve.requireMember(q);
    Set<Tensor> set = new HashSet<>();
    for (int i = 1; i < 10; ++i) {
      assertTrue(set.add(ellipticCurve.raise(p, i)));
    }
  }

  @Test
  void testSilverman1N1() {
    EllipticCurve ellipticCurve = EllipticCurve.of(1, -1);
    Tensor p = Tensors.vector(2, 3);
    for (int i = 1; i < 4; ++i) {
      Scalar factorial = Factorial.of(i);
      Tensor q = ellipticCurve.raise(p, Scalars.bigIntegerValueExact(factorial));
      assertEquals(q.length(), 2);
    }
  }

  @Test
  void test1616() {
    EllipticCurve ellipticCurve = EllipticCurve.of(-16, 16);
    Tensor p = Tensors.vector(0, 4);
    Tensor q = Tensors.vector(1, 1);
    Tensor r = Tensors.vector(4, 4);
    ellipticCurve.requireMember(p);
    ellipticCurve.requireMember(q);
    ellipticCurve.requireMember(r);
    assertThrows(Exception.class, () -> ellipticCurve.requireMember(Tensors.vector(100, 3)));
    assertNotEquals(ellipticCurve, EllipticCurve.of(-16, 17));
  }

  @Test
  void test1616b() {
    EllipticCurve ellipticCurve = EllipticCurve.of(-16, 16);
    Polynomial p = ellipticCurve.polynomial();
    Polynomial d = p.derivative();
    Tensor cp3 = p.times(p).coeffs().multiply(RealScalar.of(4));
    Tensor cd2 = d.times(d).times(d).coeffs();
    cd2.copy();
    TensorUnaryOperator op = PadRight.zeros(cp3.length());
    Tensor pc = op.apply(p.coeffs());
    pc.copy();
  }

  private static void _check(EllipticCurve ellipticCurve, Tensor p) {
    Tensor q = p;
    assertEquals(ellipticCurve.raise(p, 0), EllipticCurve.NEUTRAL);
    for (int i = 1; i < 10; ++i) {
      Tensor r = ellipticCurve.raise(p, i);
      assertEquals(r, q);
      q = ellipticCurve.combine(p, q);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = { "{-2, 3}", "{-1, 4}", "{+2, 5}", "{+4, 9}", "{+8, 23}", "{43, 282}", "{52, 375}", "{5234, 378661}" })
  void test17power(String point) {
    _check(EllipticCurve.of(0, 17), Tensors.fromString(point));
  }

  @Test
  void test17() {
    EllipticCurve ellipticCurve = EllipticCurve.of(0, 17);
    List<Tensor> list = Stream.of("{-2, 3}", "{-1, 4}", "{+2, 5}", "{+4, 9}", "{+8, 23}", "{43, 282}", "{52, 375}", "{5234, 378661}") //
        .map(Tensors::fromString).toList();
    for (int i = 0; i < list.size(); ++i)
      ellipticCurve.combine(list.get(i), list.get(i));
    for (Tensor p : list)
      assertTrue(ellipticCurve.isMember(p));
    Tensor p = ellipticCurve.complete(RealScalar.of(-2));
    Tensor q = ellipticCurve.complete(RealScalar.of(+4));
    Tensor r = ellipticCurve.combine(p, q);
    for (int x = 0; x < 10; ++x)
      r = ellipticCurve.combine(p, r);
    Tensor P = Tensors.vector(-1, 4);
    Tensor Q = Tensors.vector(2, 5);
    Tensor S = ellipticCurve.combine(P, Q);
    assertEquals(S, Tensors.fromString("{-8/9,-109/27}"));
  }

  @Test
  void test43_166() {
    EllipticCurve ellipticCurve = EllipticCurve.of(-43, 166);
    Tensor P = Tensors.vector(3, 8);
    for (int i = 1; i < 4; i++) {
      Tensor Pexp = ellipticCurve.raise(P, 1 << i);
      assertTrue(Pexp.stream().map(Scalar.class::cast).allMatch(IntegerQ::of));
    }
  }

  @Test
  void testLjunggren() {
    EllipticCurve ellipticCurve = EllipticCurve.of(-2, 0);
    List<Tensor> list = Stream.of("{0, 0}", "{-1, 1}", "{+2, 2}", "{338, 6214}") //
        .map(Tensors::fromString).toList();
    for (Tensor p : list)
      assertTrue(ellipticCurve.isMember(p));
    for (int i = 1; i < list.size(); ++i)
      ellipticCurve.combine(list.get(i), list.get(i));
    for (int i = 0; i < list.size(); ++i)
      for (int j = i + 1; j < list.size(); ++j) {
        Tensor p = list.get(i);
        Tensor q = list.get(j);
        Tensor r1 = ellipticCurve.combine(p, q);
        Tensor r2 = ellipticCurve.combine(q, p);
        assertEquals(r1, r2);
      }
  }

  private static Set<Tensor> _findAll(EllipticCurve ellipticCurve, int mod) {
    Set<Tensor> set = new HashSet<>();
    for (int i = 0; i < mod; ++i)
      for (int j = 0; j < mod; ++j) {
        Tensor p = Tensors.of(GaussScalar.of(i, mod), GaussScalar.of(j, mod));
        if (ellipticCurve.isMember(p))
          set.add(p);
      }
    return set;
  }

  private static Set<Integer> _checkOrder(EllipticCurve ellipticCurve, int mod, Set<Tensor> set) {
    Set<Integer> order = new HashSet<>();
    for (Tensor p : set)
      for (int i = 1; i <= set.size() + 2; ++i) {
        Tensor q = ellipticCurve.raise(p, i);
        if (q.equals(EllipticCurve.NEUTRAL)) {
          order.add(i);
          break;
        }
      }
    return order;
  }

  @Test
  void testFinite877() {
    EllipticCurve ellipticCurve = EllipticCurve.of(877, 0);
    Scalar xt = Scalars.fromString("612776083187947368101/78841535860683900210");
    Scalar x0 = xt.multiply(xt);
    Tensor m = Tensors.vector(0, 0);
    assertTrue(ellipticCurve.isMember(m));
    Tensor p = ellipticCurve.complete(x0);
    assertTrue(ellipticCurve.isMember(p));
    Tensor q = ellipticCurve.combine(m, p);
    assertNotEquals(p, q);
    assertNotEquals(ellipticCurve, new Object());
  }

  @Test
  void testFinite7() {
    int mod = 7;
    EllipticCurve ellipticCurve = EllipticCurve.of(GaussScalar.of(0, mod), GaussScalar.of(2, mod));
    Set<Tensor> set = _findAll(ellipticCurve, mod);
    assertEquals(set.size(), 8);
    Set<Integer> order = _checkOrder(ellipticCurve, mod, set);
    assertEquals(order.size(), 1);
    assertTrue(order.contains(3));
  }

  @Test
  void testFinite11() {
    int mod = 11;
    EllipticCurve ellipticCurve = EllipticCurve.of(GaussScalar.of(3, mod), GaussScalar.of(2, mod));
    Set<Tensor> set = _findAll(ellipticCurve, mod);
    assertEquals(set.size(), 12);
    Set<Integer> order = _checkOrder(ellipticCurve, mod, set);
    assertEquals(order.size(), 1);
    Tensor p = Tensors.of(GaussScalar.of(2, mod), GaussScalar.of(4, mod));
    Tensor q = Tensors.of(GaussScalar.of(3, mod), GaussScalar.of(4, mod));
    Tensor r = ellipticCurve.combine(p, q);
    assertTrue(ellipticCurve.isMember(r));
    assertTrue(ellipticCurve.isMember(Tensors.of(GaussScalar.of(4, mod), GaussScalar.of(1, mod))));
    assertTrue(ellipticCurve.isMember(Tensors.of(GaussScalar.of(6, mod), GaussScalar.of(4, mod))));
    assertTrue(ellipticCurve.isMember(Tensors.of(GaussScalar.of(7, mod), GaussScalar.of(5, mod))));
    assertTrue(ellipticCurve.isMember(Tensors.of(GaussScalar.of(10, mod), GaussScalar.of(3, mod))));
  }

  @Test
  void testFinite13() {
    int mod = 13;
    EllipticCurve ellipticCurve = EllipticCurve.of(GaussScalar.of(0, mod), GaussScalar.of(2, mod));
    Set<Tensor> set = _findAll(ellipticCurve, mod);
    assertEquals(set.size(), 18);
    Set<Integer> order = _checkOrder(ellipticCurve, mod, set);
    assertEquals(order.size(), 1);
    assertTrue(order.contains(19));
  }

  @Test
  void testFinite19() {
    int mod = 19;
    EllipticCurve ellipticCurve = EllipticCurve.of(GaussScalar.of(2, mod), GaussScalar.of(1, mod));
    Set<Tensor> set = _findAll(ellipticCurve, mod);
    assertEquals(set.size(), 26);
    Set<Integer> order = _checkOrder(ellipticCurve, mod, set);
    assertEquals(order.size(), 3);
    assertTrue(order.contains(3));
    assertTrue(order.contains(27));
    Polynomial polynomial = ellipticCurve.polynomial();
    polynomial.derivative();
  }

  @Test
  void testFiniteSecp256k1() throws ClassNotFoundException, IOException {
    BigInteger prime = BigInteger.TWO.pow(256).subtract(BigInteger.TWO.pow(32)).subtract(BigInteger.valueOf(977));
    EllipticCurve ellipticCurve = EllipticCurve.of(GaussScalar.of(0, prime), GaussScalar.of(7, prime));
    Serialization.copy(ellipticCurve);
    Tensor set = Tensors.empty();
    for (int c = 0; c < 100; ++c)
      try {
        Tensor p = ellipticCurve.complete(GaussScalar.of(c, prime));
        set.append(p);
        ellipticCurve.raise(p, 3);
      } catch (Exception exception) {
        // ---
      }
    int n = set.length();
    assertEquals(n, 46);
    Tensor matrix = UpperEvaluation.of(set, set, ellipticCurve::combine, s -> s);
    Map<Tensor, Long> map = Tally.of(Flatten.stream(matrix, 1));
    assertEquals(map.size(), 46 * 47 / 2);
  }

  @Test
  void testCurve25519() {
    BigInteger prime = BigInteger.TWO.pow(255).subtract(BigInteger.valueOf(19));
    EllipticCurve ellipticCurve = EllipticCurve.montgomery(GaussScalar.of(486662, prime), GaussScalar.of(1, prime));
    Tensor set = Tensors.empty();
    for (int c = 0; c < 100; ++c)
      try {
        Tensor p = ellipticCurve.complete(GaussScalar.of(c, prime));
        set.append(p);
        ellipticCurve.raise(p, 3);
      } catch (Exception exception) {
        // ---
      }
    int n = set.length();
    assertEquals(n, 46);
    Tensor matrix = UpperEvaluation.of(set, set, ellipticCurve::combine, s -> s);
    Map<Tensor, Long> map = Tally.of(Flatten.stream(matrix, 1));
    assertEquals(map.size(), 46 * 47 / 2);
    // System.out.println(ellipticCurve.polynomial());
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> EllipticCurve.of(0, 0));
  }
}
