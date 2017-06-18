// code by jph
package ch.ethz.idsc.tensor;

import java.util.Map;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.alg.Accumulate;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.io.ExtractPrimitives;
import ch.ethz.idsc.tensor.pdf.BinomialDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Tally;
import ch.ethz.idsc.tensor.red.Total;
import junit.framework.TestCase;

public class BooleanScalarTest extends TestCase {
  public void testSimple() {
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

  public void testMultiply() {
    assertEquals(BooleanScalar.TRUE.multiply(BooleanScalar.TRUE), BooleanScalar.TRUE);
    assertEquals(BooleanScalar.FALSE.multiply(BooleanScalar.FALSE), BooleanScalar.FALSE);
    // ---
    assertEquals(BooleanScalar.TRUE.multiply(BooleanScalar.FALSE), BooleanScalar.FALSE);
    assertEquals(BooleanScalar.FALSE.multiply(BooleanScalar.TRUE), BooleanScalar.FALSE);
  }

  public void testSort() {
    Tensor logical = Tensors.of(BooleanScalar.TRUE, BooleanScalar.FALSE, BooleanScalar.FALSE, BooleanScalar.TRUE, BooleanScalar.TRUE);
    Tensor sorted = Sort.of(logical);
    assertEquals(sorted.toString(), "{false, false, true, true, true}");
  }

  public void testMapping() {
    Tensor values = RandomVariate.of(BinomialDistribution.of(10, RationalScalar.of(3, 7)), 200);
    Tensor result = Tensor.of(values.flatten(0) //
        .map(Scalar.class::cast) //
        .map(s -> Scalars.lessThan(s, RealScalar.of(5))).map(BooleanScalar::of));
    Map<Tensor, Long> map = Tally.of(result);
    assertTrue(10 < map.get(BooleanScalar.TRUE));
    assertTrue(10 < map.get(BooleanScalar.FALSE));
  }

  public void testNumber() {
    Tensor values = RandomVariate.of(BinomialDistribution.of(10, RationalScalar.of(3, 7)), 200);
    Tensor result = Tensor.of(values.flatten(0) //
        .map(Scalar.class::cast) //
        .map(s -> Scalars.lessThan(s, RealScalar.of(5))).map(BooleanScalar::of));
    Tensor zeroOne = Tensors.vector(ExtractPrimitives.toStreamNumber(result).collect(Collectors.toList()));
    assertTrue(10 < Total.of(zeroOne).Get().number().intValue());
  }

  public void testAccumulate() {
    Tensor values = RandomVariate.of(BinomialDistribution.of(10, RationalScalar.of(3, 7)), 200);
    Tensor result = Tensor.of(values.flatten(0) //
        .map(Scalar.class::cast) //
        .map(s -> Scalars.lessThan(s, RealScalar.of(5))).map(BooleanScalar::of));
    Tensor accum = Accumulate.of(result);
    assertEquals(Last.of(accum), Total.of(result));
  }
}
