// code by jph
package ch.alpine.tensor;

import java.util.Map;
import java.util.stream.Collectors;

import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BooleanScalarTest extends TestCase {
  public void testString() {
    Tensor logical = Tensors.of(BooleanScalar.of(true), BooleanScalar.of(false));
    assertEquals(logical.toString(), "{true, false}");
  }

  public void testPlus() {
    assertEquals(BooleanScalar.TRUE.add(BooleanScalar.TRUE), BooleanScalar.FALSE);
    assertEquals(BooleanScalar.FALSE.add(BooleanScalar.FALSE), BooleanScalar.FALSE);
    // ---
    assertEquals(BooleanScalar.TRUE.add(BooleanScalar.FALSE), BooleanScalar.TRUE);
    assertEquals(BooleanScalar.FALSE.add(BooleanScalar.TRUE), BooleanScalar.TRUE);
  }

  public void testNegate() {
    assertEquals(BooleanScalar.TRUE.negate(), BooleanScalar.TRUE);
    assertEquals(BooleanScalar.FALSE.negate(), BooleanScalar.FALSE);
  }

  public void testReciprocal() {
    assertEquals(BooleanScalar.TRUE.reciprocal(), BooleanScalar.TRUE);
    AssertFail.of(() -> BooleanScalar.FALSE.reciprocal());
  }

  public void testZero() {
    assertEquals(BooleanScalar.TRUE.zero(), BooleanScalar.FALSE);
    assertEquals(BooleanScalar.FALSE.zero(), BooleanScalar.FALSE);
  }

  public void testMultiply() {
    assertEquals(BooleanScalar.TRUE.multiply(BooleanScalar.TRUE), BooleanScalar.TRUE);
    assertEquals(BooleanScalar.FALSE.multiply(BooleanScalar.FALSE), BooleanScalar.FALSE);
    // ---
    assertEquals(BooleanScalar.TRUE.multiply(BooleanScalar.FALSE), BooleanScalar.FALSE);
    assertEquals(BooleanScalar.FALSE.multiply(BooleanScalar.TRUE), BooleanScalar.FALSE);
  }

  public void testEquals() {
    assertFalse(BooleanScalar.TRUE.equals(null));
  }

  public void testSort() {
    Tensor logical = Tensors.of(BooleanScalar.TRUE, BooleanScalar.FALSE, BooleanScalar.FALSE, BooleanScalar.TRUE, BooleanScalar.TRUE);
    Tensor sorted = Sort.of(logical);
    assertEquals(sorted.toString(), "{false, false, true, true, true}");
  }

  public void testMapping() {
    Tensor values = RandomVariate.of(BinomialDistribution.of(10, RationalScalar.of(3, 7)), 200);
    Tensor result = values.map(s -> BooleanScalar.of(Scalars.lessThan(s, RealScalar.of(5))));
    Map<Tensor, Long> map = Tally.of(result);
    assertTrue(10 < map.get(BooleanScalar.TRUE));
    assertTrue(10 < map.get(BooleanScalar.FALSE));
  }

  public void testNumber() {
    Tensor values = RandomVariate.of(BinomialDistribution.of(10, RationalScalar.of(3, 7)), 200);
    Tensor result = Tensor.of(values.stream() //
        .map(Scalar.class::cast) //
        .map(s -> Scalars.lessThan(s, RealScalar.of(5))).map(BooleanScalar::of));
    Tensor zeroOne = Tensors.vector(Primitives.toStreamNumber(result).collect(Collectors.toList()));
    assertTrue(10 < Total.ofVector(zeroOne).number().intValue());
  }

  public void testNumberType() {
    assertTrue(BooleanScalar.TRUE.number() instanceof Integer);
    assertTrue(BooleanScalar.FALSE.number() instanceof Integer);
  }

  public void testAccumulate() {
    Tensor values = RandomVariate.of(BinomialDistribution.of(10, RationalScalar.of(3, 7)), 200);
    Tensor result = Tensor.of(values.stream() //
        .map(Scalar.class::cast) //
        .map(s -> Scalars.lessThan(s, RealScalar.of(5))).map(BooleanScalar::of));
    Tensor accum = Accumulate.of(result);
    assertEquals(Last.of(accum), Total.of(result));
  }

  public void testExactNumberQ() {
    ExactScalarQ.require(BooleanScalar.FALSE);
    ExactScalarQ.require(BooleanScalar.TRUE);
  }

  public void testSqrt() {
    assertEquals(Sqrt.of(BooleanScalar.FALSE), BooleanScalar.FALSE);
    assertEquals(Sqrt.of(BooleanScalar.TRUE), BooleanScalar.TRUE);
  }
}
