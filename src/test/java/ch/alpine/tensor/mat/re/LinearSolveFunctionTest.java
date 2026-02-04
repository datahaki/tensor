package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Int;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import test.SerializableQ;

class LinearSolveFunctionTest {
  @RepeatedTest(10)
  void testSquareExact(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-30, 30), n, n);
    assumeFalse(Scalars.isZero(Det.of(matrix)));
    TensorUnaryOperator solver = LinearSolveFunction.of(matrix);
    SerializableQ.require(solver);
    Tensor inverse = Inverse.of(matrix);
    {
      Tensor sol = solver.apply(IdentityMatrix.of(matrix));
      assertEquals(sol, inverse);
    }
    for (int k = 0; k < n; ++k) {
      Tensor sol = solver.apply(UnitVector.of(n, k));
      assertEquals(sol, inverse.get(Tensor.ALL, k));
    }
  }

  @RepeatedTest(10)
  void testSquareNumeric(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(20), n, n);
    TensorUnaryOperator solver = LinearSolveFunction.of(matrix);
    SerializableQ.require(solver);
    Tensor inverse = Inverse.of(matrix);
    {
      Tensor sol = solver.apply(IdentityMatrix.of(matrix));
      Tolerance.CHOP.requireClose(sol, inverse);
    }
    for (int k = 0; k < n; ++k) {
      Tensor sol = solver.apply(UnitVector.of(n, k));
      Tolerance.CHOP.requireClose(sol, inverse.get(Tensor.ALL, k));
    }
  }

  @RepeatedTest(10)
  void testRectExact(RepetitionInfo repetitionInfo) {
    int col = repetitionInfo.getCurrentRepetition();
    int row = col + 3;
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-30, 30), row, col);
    TensorUnaryOperator solver = LinearSolveFunction.of(matrix);
    SerializableQ.require(solver);
    Tensor grid = RandomVariate.of(DiscreteUniformDistribution.of(-30, 30), col, col);
    Tensor inverse = matrix.dot(grid);
    assumeTrue(MatrixRank.of(matrix) == col);
    {
      Tensor sol = solver.apply(inverse);
      assertEquals(sol, grid);
    }
    for (int k = 0; k < col; ++k) {
      Tensor sol = solver.apply(inverse.get(Tensor.ALL, k));
      assertEquals(sol, grid.get(Tensor.ALL, k));
    }
  }

  @RepeatedTest(10)
  void testRectNumeric(RepetitionInfo repetitionInfo) {
    int col = repetitionInfo.getCurrentRepetition();
    int row = col + 3;
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(20), row, col);
    TensorUnaryOperator solver = LinearSolveFunction.of(matrix);
    SerializableQ.require(solver);
    Tensor grid = RandomVariate.of(NormalDistribution.standard(), col, col);
    Tensor inverse = matrix.dot(grid);
    assumeTrue(MatrixRank.of(matrix) == col);
    {
      Tensor sol = solver.apply(inverse);
      Tolerance.CHOP.requireClose(sol, grid);
    }
    for (int k = 0; k < col; ++k) {
      Tensor sol = solver.apply(inverse.get(Tensor.ALL, k));
      Tolerance.CHOP.requireClose(sol, grid.get(Tensor.ALL, k));
    }
  }

  @RepeatedTest(10)
  void testRectNumericFail(RepetitionInfo repetitionInfo) {
    int col = repetitionInfo.getCurrentRepetition();
    int row = col + 3;
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(20), row, col);
    TensorUnaryOperator solver = LinearSolveFunction.of(matrix);
    // Tensor grid = ;
    Tensor inverse = RandomVariate.of(NormalDistribution.standard(), row, col);
    assumeTrue(MatrixRank.of(matrix) == col);
    assertThrows(Exception.class, () -> solver.apply(inverse));
    Int i = new Int();
    for (int k = 0; k < col; ++k)
      assertThrows(Exception.class, () -> solver.apply(inverse.get(Tensor.ALL, i.getAndIncrement())));
  }

  @Test
  void testGaussian() {
    Scalar[][] arrays = new Scalar[][] { //
        { GaussScalar.of(3, 17) }, //
        { GaussScalar.of(4, 17) } };
    Tensor matrix = Tensors.matrix(arrays);
    TensorUnaryOperator lsf = LinearSolveFunction.of(matrix);
    Tensor result = lsf.apply(matrix.get(Tensor.ALL, 0));
    assertEquals(result, Tensors.of(GaussScalar.of(1, 17)));
  }

  @Test
  void testGaussianFail() {
    Scalar[][] arrays = new Scalar[][] { //
        { GaussScalar.of(0, 17) }, //
        { GaussScalar.of(0, 17) } };
    Tensor matrix = Tensors.matrix(arrays);
    TensorUnaryOperator lsf = LinearSolveFunction.of(matrix);
    Tensor result = lsf.apply(matrix.get(Tensor.ALL, 0));
    assertEquals(result, Tensors.of(GaussScalar.of(0, 17)));
  }

  @Test
  void testInsufficient() {
    Tensor matrix = Tensors.fromString("{{2,0},{0,0}}");
    TensorUnaryOperator tuo = LinearSolveFunction.of(matrix);
    assertEquals(tuo.apply(Tensors.vector(10, 0)), Tensors.vector(5, 0));
  }
}
