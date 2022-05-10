// code by jph
package ch.alpine.tensor.mat.ev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.nrm.MatrixInfinityNorm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.N;

class EigensystemTest {
  @RepeatedTest(12)
  public void testPhase1Tuning(RepetitionInfo repetitionInfo) throws IOException {
    Distribution distribution = UniformDistribution.of(-2, 2);
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = Symmetrize.of(RandomVariate.of(distribution, n, n));
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor vectors = eigensystem.vectors();
    Tensor values = eigensystem.values();
    OrthogonalMatrixQ.require(vectors);
    Tensor recons = Transpose.of(vectors).dot(Times.of(values, vectors));
    Scalar err = MatrixInfinityNorm.of(matrix.subtract(recons));
    if (!Tolerance.CHOP.isClose(matrix, recons)) {
      System.err.println(err);
      // System.err.println("error");
      System.out.println("n=" + n);
      System.out.println(matrix);
      Export.of(HomeDirectory.file("eigensystem_fail_" + System.currentTimeMillis() + ".csv"), matrix);
      fail();
    }
  }

  @Test
  public void testQuantity() {
    Tensor matrix = Tensors.fromString("{{10[m], -2[m]}, {-2[m], 4[m]}}");
    SymmetricMatrixQ.require(matrix);
    {
      Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
      assertInstanceOf(Quantity.class, eigensystem.values().Get(0));
      assertInstanceOf(Quantity.class, eigensystem.values().Get(1));
    }
    {
      Eigensystem eigensystem = Eigensystem.ofSymmetric(N.DOUBLE.of(matrix));
      assertInstanceOf(Quantity.class, eigensystem.values().Get(0));
      assertInstanceOf(Quantity.class, eigensystem.values().Get(1));
    }
  }

  @ParameterizedTest
  @ValueSource(ints = { 8, 9 })
  public void testQuantityLarge(int n) {
    Tensor x = Symmetrize.of(RandomVariate.of(NormalDistribution.standard(), n, n)).map(s -> Quantity.of(s, "m"));
    Eigensystem eigensystem = Eigensystem.ofSymmetric(x);
    eigensystem.values().map(QuantityMagnitude.singleton("m"));
  }

  @ParameterizedTest
  @ValueSource(ints = { 8, 9 })
  public void testQuantityDegenerate(int n) {
    int r = 4;
    Tensor v = Join.of( //
        RandomVariate.of(NormalDistribution.standard(), r), //
        Array.zeros(n - 4)).map(s -> Quantity.of(s, "m"));
    Tensor x = RandomVariate.of(NormalDistribution.standard(), n, n);
    Tensor matrix = Transpose.of(x).dot(Times.of(v, x));
    // System.out.println(Pretty.of(matrix.map(Round._1)));
    assertEquals(MatrixRank.of(matrix), r);
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    eigensystem.values().map(QuantityMagnitude.singleton("m"));
  }

  @Test
  public void testQuantityMixed() {
    Tensor matrix = Tensors.fromString("{{10[m^2], 2[m*kg]}, {2[m*kg], 4[kg^2]}}");
    SymmetricMatrixQ.require(matrix);
    assertThrows(TensorRuntimeException.class, () -> Eigensystem.ofSymmetric(matrix));
  }

  @Test
  public void testEmptyFail() {
    assertThrows(TensorRuntimeException.class, () -> Eigensystem.ofSymmetric(Tensors.empty()));
  }

  @Test
  public void testNonSymmetricFail() {
    assertThrows(TensorRuntimeException.class, () -> Eigensystem.ofSymmetric(Tensors.fromString("{{1, 2}, {3, 4}}")));
  }

  @Test
  public void testComplexFail() {
    Tensor matrix = Tensors.fromString("{{I, 0}, {0, I}}");
    SymmetricMatrixQ.require(matrix);
    assertThrows(ClassCastException.class, () -> Eigensystem.ofSymmetric(matrix));
  }

  @Test
  public void testComplex2Fail() {
    Tensor matrix = Tensors.fromString("{{0, I}, {I, 0}}");
    SymmetricMatrixQ.require(matrix);
    assertThrows(ClassCastException.class, () -> Eigensystem.ofSymmetric(matrix));
  }

  @Test
  public void testNonSymmetric2Fail() {
    assertThrows(TensorRuntimeException.class, () -> Eigensystem.ofSymmetric(Array.zeros(2, 3)));
  }
}
