// code by jph
package ch.alpine.tensor.mat.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.gr.InfluenceMatrixQ;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.mat.sv.SingularValueDecompositionWrap;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.LogisticDistribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.red.Trace;
import ch.alpine.tensor.sca.Chop;

class BenIsraelCohenTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testQuantity(int r) {
    Random random = new Random(2);
    Distribution distribution = LogisticDistribution.of(1, 5);
    ScalarUnaryOperator suo = QuantityMagnitude.singleton("K^1/2*m^-1");
    Tensor p1 = RandomVariate.of(distribution, random, 8, r);
    Tensor p2 = RandomVariate.of(distribution, random, r, 4).maps(s -> Quantity.of(s, "m*K^-1/2"));
    Tensor design = p1.dot(p2);
    Tensor pinv = BenIsraelCohen.of(design);
    suo.apply(pinv.Get(0, 0));
    new InfluenceMatrixQ(Chop._10).requireMember(design.dot(pinv)); // 1e-12 does not always work
  }

  @Test
  void testMathematica() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/pi/bic1.csv");
    Tensor mathem = Import.of("/ch/alpine/tensor/mat/pi/bic1pinv.csv");
    Tensor pinv = BenIsraelCohen.of(matrix);
    Tolerance.CHOP.requireClose(pinv, mathem);
    SingularValueDecomposition svd = SingularValueDecompositionWrap.of(matrix);
    Chop._08.requireClose(pinv, PseudoInverse.of(svd));
  }

  @Test
  void testMixedUnitsSquare() {
    Tensor matrix = Tensors.fromString( //
        "{{-4/5[m^-2], 3/10[m^-1*rad^-1]}, {3/10[m^-1*rad^-1], -1/20[rad^-2]}}");
    Tensor inv1 = Inverse.of(matrix);
    Tensor pinv = BenIsraelCohen.of(matrix);
    Tolerance.CHOP.requireClose(inv1, pinv);
  }

  @Test
  void testMixedUnitsGeneral() {
    Tensor matrix = Tensors.fromString( //
        "{{-4/5[m], 3/10[m], 1/2[m]}, {3[s], -2[s], 1[s]}}");
    Tensor pinv1 = BenIsraelCohen.of(matrix);
    Tensor pinv2 = BenIsraelCohen.of(Transpose.of(matrix));
    Tolerance.CHOP.requireClose(Transpose.of(pinv1), pinv2);
    InfluenceMatrixQ.INSTANCE.requireMember(pinv1.dot(matrix));
  }

  @Test
  void testMixedUnitsFail() {
    Tensor matrix = Tensors.fromString( //
        "{{-4/5[kg], 3/10[m], 1/2[m]}, {3[s], -2[s], 1[s]}}");
    assertThrows(Throw.class, () -> BenIsraelCohen.of(matrix));
  }

  @Test
  void testZeros() {
    Tensor refine = BenIsraelCohen.of(Array.zeros(4, 3));
    assertEquals(refine, Array.zeros(3, 4));
  }

  @Test
  void testEpsilonNonFail() {
    BenIsraelCohen.of(Tensors.fromString("{{1E-300}}"));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testReal(int r) {
    Random random = new Random(3);
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor p1 = RandomVariate.of(distribution, random, 8, r);
    Tensor p2 = RandomVariate.of(distribution, random, r, 4);
    Tensor matrix = p1.dot(p2);
    Tensor refine = BenIsraelCohen.of(matrix);
    Chop._09.requireClose(PseudoInverse.of(matrix), refine);
    InfluenceMatrixQ.INSTANCE.requireMember(refine.dot(matrix));
  }

  @Test
  void testExceedIters() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/pi/bic_fail.csv");
    MatrixQ.require(matrix);
    assertThrows(Throw.class, () -> BenIsraelCohen.of(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testComplexFullRank(int r) {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor re = RandomVariate.of(distribution, 5, r);
    Tensor im = RandomVariate.of(distribution, 5, r);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    Tensor refine = BenIsraelCohen.of(matrix);
    Tolerance.CHOP.requireClose(refine.dot(matrix), IdentityMatrix.of(r));
    Tensor pinvtr = BenIsraelCohen.of(Transpose.of(matrix));
    Tolerance.CHOP.requireClose(Transpose.of(pinvtr), refine);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testComplexRankDeficient(int r) {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor p1 = Entrywise.with(ComplexScalar::of).apply( //
        RandomVariate.of(distribution, 5, r), //
        RandomVariate.of(distribution, 5, r));
    Tensor p2 = Entrywise.with(ComplexScalar::of).apply( //
        RandomVariate.of(distribution, r, 4), //
        RandomVariate.of(distribution, r, 4));
    Tensor matrix = p1.dot(p2);
    Tensor refine = BenIsraelCohen.of(matrix);
    Scalar rank = Trace.of(matrix.dot(refine));
    Tolerance.CHOP.requireClose(rank, RealScalar.of(r));
  }

  @Test
  void testThreadLocal() {
    ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 128);
    assertEquals(threadLocal.get(), 128);
    threadLocal.remove();
    assertEquals(threadLocal.get(), 128);
    threadLocal.set(12);
    threadLocal.remove();
    threadLocal.remove();
    assertEquals(threadLocal.get(), 128);
  }

  @Test
  void testAbsurd() {
    assertThrows(Throw.class, () -> BenIsraelCohen.of(Tensors.fromString("{{NaN}}")));
    assertThrows(Throw.class, () -> BenIsraelCohen.of(Tensors.fromString("{{Infinity}}")));
    assertThrows(Throw.class, () -> BenIsraelCohen.of(Tensors.fromString("{{1.7976931348623157e+308}}")));
  }

  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(BenIsraelCohen.class.getModifiers()));
  }
}
