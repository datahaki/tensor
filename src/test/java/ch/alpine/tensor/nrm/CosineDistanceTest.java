// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.N;

class CosineDistanceTest {
  @Test
  void testZero() {
    assertEquals(CosineDistance.of(Tensors.vector(0, 0, 0), Tensors.vector(1, 2, 3)), RealScalar.ZERO);
    assertEquals(CosineDistance.of(Tensors.vector(0, 0, 0.0), Tensors.vector(1, 2, 3)), RealScalar.ZERO);
  }

  @Test
  void testSome() {
    Tolerance.CHOP.requireClose( //
        CosineDistance.of(Tensors.vector(1, 1, 1), Tensors.vector(1, 2, 3)), //
        RealScalar.of(0.07417990022744858));
  }

  static List<TensorUnaryOperator> vectornormalizers() {
    return Arrays.asList( //
        Vector1Norm.NORMALIZE, //
        Vector2Norm.NORMALIZE, //
        VectorInfinityNorm.NORMALIZE //
    );
  }

  @ParameterizedTest
  @MethodSource("vectornormalizers")
  void testEmpty(TensorUnaryOperator tuo) {
    assertThrows(Exception.class, () -> tuo.apply(Tensors.empty()));
    assertThrows(Exception.class, () -> tuo.apply(Array.zeros(10)));
    assertThrows(Exception.class, () -> tuo.apply(Array.zeros(10).map(N.DOUBLE)));
    assertThrows(Exception.class, () -> tuo.apply(Array.zeros(10).map(N.DECIMAL128)));
  }

  @ParameterizedTest
  @MethodSource("vectornormalizers")
  void testNormalizePositiveInfinity(TensorUnaryOperator tuo) {
    Tensor vector = Tensors.of(DoubleScalar.POSITIVE_INFINITY, RealScalar.ONE);
    assertThrows(Throw.class, () -> tuo.apply(vector));
    assertThrows(Throw.class, () -> NormalizeUnlessZero.with(Vector2Norm::of).apply(vector));
  }

  @ParameterizedTest
  @MethodSource("vectornormalizers")
  void testNormalizeNegativeInfinity(TensorUnaryOperator tuo) {
    Tensor vector = Tensors.of(DoubleScalar.NEGATIVE_INFINITY, RealScalar.ONE, DoubleScalar.POSITIVE_INFINITY);
    assertThrows(Exception.class, () -> tuo.apply(vector));
  }

  @ParameterizedTest
  @MethodSource("vectornormalizers")
  void testNormalizeNaN(TensorUnaryOperator tuo) {
    Tensor vector = Tensors.of(RealScalar.ONE, DoubleScalar.INDETERMINATE, RealScalar.ONE);
    assertThrows(Exception.class, () -> tuo.apply(vector));
  }

  @ParameterizedTest
  @MethodSource("vectornormalizers")
  void testScalarFail(TensorUnaryOperator tuo) {
    assertThrows(Exception.class, () -> tuo.apply(RealScalar.ONE));
    assertThrows(Exception.class, () -> tuo.apply(Tensors.fromString("{{1, 2}, {3, 4, 5}}")));
    assertThrows(Exception.class, () -> tuo.apply(HilbertMatrix.of(3)));
  }
}
