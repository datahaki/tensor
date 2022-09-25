// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.pow.Sqrt;

class BooleanScalarTest {
  @Test
  void testString() {
    Tensor logical = Tensors.of(BooleanScalar.of(true), BooleanScalar.of(false));
    assertEquals(logical.toString(), "{true, false}");
  }

  @Test
  void testPlus() {
    assertEquals(BooleanScalar.TRUE.add(BooleanScalar.TRUE), BooleanScalar.FALSE);
    assertEquals(BooleanScalar.FALSE.add(BooleanScalar.FALSE), BooleanScalar.FALSE);
    // ---
    assertEquals(BooleanScalar.TRUE.add(BooleanScalar.FALSE), BooleanScalar.TRUE);
    assertEquals(BooleanScalar.FALSE.add(BooleanScalar.TRUE), BooleanScalar.TRUE);
  }

  @Test
  void testNegate() {
    assertEquals(BooleanScalar.TRUE.negate(), BooleanScalar.TRUE);
    assertEquals(BooleanScalar.FALSE.negate(), BooleanScalar.FALSE);
  }

  @Test
  void testReciprocal() {
    assertEquals(BooleanScalar.TRUE.reciprocal(), BooleanScalar.TRUE);
    assertThrows(Throw.class, BooleanScalar.FALSE::reciprocal);
  }

  @Test
  void testZero() {
    assertEquals(BooleanScalar.TRUE.zero(), BooleanScalar.FALSE);
    assertEquals(BooleanScalar.FALSE.zero(), BooleanScalar.FALSE);
  }

  @Test
  void testMultiply() {
    assertEquals(BooleanScalar.TRUE.multiply(BooleanScalar.TRUE), BooleanScalar.TRUE);
    assertEquals(BooleanScalar.FALSE.multiply(BooleanScalar.FALSE), BooleanScalar.FALSE);
    // ---
    assertEquals(BooleanScalar.TRUE.multiply(BooleanScalar.FALSE), BooleanScalar.FALSE);
    assertEquals(BooleanScalar.FALSE.multiply(BooleanScalar.TRUE), BooleanScalar.FALSE);
  }

  @Test
  void testEquals() {
    assertNotEquals(null, BooleanScalar.TRUE);
  }

  @Test
  void testSort() {
    Tensor logical = Tensors.of(BooleanScalar.TRUE, BooleanScalar.FALSE, BooleanScalar.FALSE, BooleanScalar.TRUE, BooleanScalar.TRUE);
    Tensor sorted = Sort.of(logical);
    assertEquals(sorted.toString(), "{false, false, true, true, true}");
  }

  @Test
  void testMapping() {
    Tensor values = RandomVariate.of(BinomialDistribution.of(10, RationalScalar.of(3, 7)), 200);
    Tensor result = values.map(s -> BooleanScalar.of(Scalars.lessThan(s, RealScalar.of(5))));
    Map<Tensor, Long> map = Tally.of(result);
    assertTrue(10 < map.get(BooleanScalar.TRUE));
    assertTrue(10 < map.get(BooleanScalar.FALSE));
  }

  @Test
  void testNumber() {
    Tensor values = RandomVariate.of(BinomialDistribution.of(10, RationalScalar.of(3, 7)), 200);
    Tensor result = Tensor.of(values.stream() //
        .map(Scalar.class::cast) //
        .map(s -> Scalars.lessThan(s, RealScalar.of(5))).map(BooleanScalar::of));
    Tensor zeroOne = Tensors.vector(Primitives.toStreamNumber(result).collect(Collectors.toList()));
    assertTrue(10 < Total.ofVector(zeroOne).number().intValue());
  }

  @Test
  void testNumberType() {
    assertInstanceOf(Integer.class, BooleanScalar.TRUE.number());
    assertInstanceOf(Integer.class, BooleanScalar.FALSE.number());
  }

  @Test
  void testAccumulate() {
    Tensor values = RandomVariate.of(BinomialDistribution.of(10, RationalScalar.of(3, 7)), 200);
    Tensor result = Tensor.of(values.stream() //
        .map(Scalar.class::cast) //
        .map(s -> Scalars.lessThan(s, RealScalar.of(5))).map(BooleanScalar::of));
    Tensor accum = Accumulate.of(result);
    assertEquals(Last.of(accum), Total.of(result));
  }

  @Test
  void testExactNumberQ() {
    ExactScalarQ.require(BooleanScalar.FALSE);
    ExactScalarQ.require(BooleanScalar.TRUE);
  }

  @Test
  void testSqrt() {
    assertEquals(Sqrt.of(BooleanScalar.FALSE), BooleanScalar.FALSE);
    assertEquals(Sqrt.of(BooleanScalar.TRUE), BooleanScalar.TRUE);
  }
}
