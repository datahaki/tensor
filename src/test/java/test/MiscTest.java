// code by jph
package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.ArgMax;
import ch.alpine.tensor.ext.ArgMin;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.cd.CholeskyDecomposition;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixLog;
import ch.alpine.tensor.mat.ex.MatrixSqrt;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;
import ch.alpine.tensor.mat.pd.Orthogonalize;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.qr.GramSchmidt;
import ch.alpine.tensor.mat.qr.HessenbergDecomposition;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.mat.qr.SchurDecomposition;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.mat.re.RowReduce;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.nrm.Matrix1Norm;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.nrm.MatrixInfinityNorm;
import ch.alpine.tensor.nrm.NormalizeUnlessZero;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.N;

class MiscTest {
  static List<Function<Tensor, ?>> functions() {
    return Arrays.asList( //
        RowReduce::of, //
        MatrixRank::of, //
        NullSpace::of, //
        m -> LeastSquares.of(m, Tensors.vector(1)), //
        b -> LeastSquares.of(HilbertMatrix.of(1), b), //
        m -> LinearSolve.of(m, Tensors.vector(1)), //
        b -> LinearSolve.of(HilbertMatrix.of(1), b), //
        QRDecomposition::of, //
        GramSchmidt::of, //
        Orthogonalize::of, //
        CholeskyDecomposition::of, //
        MatrixQ::require, //
        Inverse::of, //
        PseudoInverse::of, //
        t -> PseudoInverse.of(SingularValueDecomposition.of(t)), //
        Eigensystem::of, //
        Eigensystem::ofSymmetric, //
        InfluenceMatrix::of, //
        Det::of, //
        MatrixExp::of, //
        MatrixLog::of, //
        MatrixSqrt::of, //
        MatrixSqrt::ofSymmetric, //
        SchurDecomposition::of, //
        HessenbergDecomposition::of //
    );
  }

  @ParameterizedTest
  @MethodSource("functions")
  void testFail(Function<Tensor, ?> function) {
    assertThrows(NullPointerException.class, () -> function.apply(null));
    assertThrows(Exception.class, () -> function.apply(RealScalar.ONE));
    assertThrows(Exception.class, () -> function.apply(Pi.VALUE));
    assertThrows(Exception.class, () -> function.apply(Tensors.empty()));
    assertThrows(Exception.class, () -> function.apply(Tensors.vector(1, 2, 3)));
    assertThrows(Exception.class, () -> function.apply(Tensors.vector(1.0, 2, 3)));
    assertThrows(Exception.class, () -> function.apply(LeviCivitaTensor.of(3)));
    assertThrows(Exception.class, () -> function.apply(LeviCivitaTensor.of(3).map(N.DOUBLE)));
    {
      Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
      assertThrows(Exception.class, () -> function.apply(tensor));
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4, 5}}").map(N.DOUBLE);
      assertThrows(Exception.class, () -> function.apply(tensor));
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
      assertThrows(Exception.class, () -> function.apply(tensor));
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4, 5}}").map(N.DOUBLE);
      assertThrows(Exception.class, () -> function.apply(tensor));
    }
  }

  /** the location of the test here asserts that the constants are public:
   * ArgMin.EMPTY
   * ArgMax.EMPTY */
  @Test
  void testConvention() {
    assertEquals(ArgMin.EMPTY, -1);
    assertEquals(ArgMax.EMPTY, -1);
  }

  static List<TensorScalarFunction> matrixnorms() {
    return Arrays.asList( //
        Matrix1Norm::of, //
        Matrix2Norm::of, //
        MatrixInfinityNorm::of);
  }

  @ParameterizedTest
  @MethodSource("matrixnorms")
  void testZero(TensorScalarFunction norm) {
    assertEquals(norm.apply(Array.zeros(1, 1)), RealScalar.ZERO);
    assertEquals(norm.apply(Array.zeros(5, 3)), RealScalar.ZERO);
  }

  @ParameterizedTest
  @MethodSource("matrixnorms")
  void testFails(TensorScalarFunction norm) {
    assertThrows(Throw.class, () -> norm.apply(RealScalar.ONE));
    assertThrows(Exception.class, () -> norm.apply(Tensors.empty()));
    assertThrows(Exception.class, () -> norm.apply(Tensors.vector(1, 2, 3)));
    assertThrows(Exception.class, () -> norm.apply(LeviCivitaTensor.of(3)));
    assertThrows(Exception.class, () -> norm.apply(Tensors.fromString("{{1, 2}, {3}}")));
  }

  static List<TensorScalarFunction> vectornorms() {
    return Arrays.asList( //
        Vector1Norm::of, //
        Vector2Norm::of, //
        VectorInfinityNorm::of //
    );
  }

  @ParameterizedTest
  @MethodSource("vectornorms")
  void testEmptyFail(TensorScalarFunction norm) {
    assertThrows(NoSuchElementException.class, () -> norm.apply(Tensors.empty()));
    assertThrows(Throw.class, () -> norm.apply(RealScalar.ONE));
    assertThrows(ClassCastException.class, () -> norm.apply(Tensors.fromString("{{1, 2}, {3}}")));
  }

  @ParameterizedTest
  @MethodSource("vectornorms")
  void testOk1(TensorScalarFunction norm) {
    Tensor v = Tensors.vector(0, 0, 0, 0);
    assertEquals(v, NormalizeUnlessZero.with(norm).apply(v));
  }

  @ParameterizedTest
  @MethodSource("vectornorms")
  void testEmpty(TensorScalarFunction norm) {
    TensorUnaryOperator tensorUnaryOperator = NormalizeUnlessZero.with(norm);
    Tensor tensor = Tensors.empty();
    assertThrows(NoSuchElementException.class, () -> tensorUnaryOperator.apply(tensor));
  }
}
