// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.mat.re.RowReduce;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class ToeplitzMatrixTest {
  @Test
  void testSquare() {
    Tensor matrix = ToeplitzMatrix.of(Tensors.vector(1, 2, 3, 4, 5));
    assertTrue(SquareMatrixQ.INSTANCE.isMember(matrix));
    assertEquals(matrix.get(0), Range.of(3, 6));
    assertEquals(matrix.get(1), Range.of(2, 5));
    assertEquals(matrix.get(2), Range.of(1, 4));
    assertEquals(MatrixRank.of(matrix), 2);
  }

  @Test
  void testSymmetric() {
    SymmetricMatrixQ.INSTANCE.requireMember(ToeplitzMatrix.of(Tensors.vector(5, 4, 3, 4, 5)));
  }

  @Test
  void testRandom() {
    Random random = new Random(3);
    Tensor vector = RandomVariate.of(UniformDistribution.unit(20), random, 5);
    Tensor matrix = ToeplitzMatrix.of(vector);
    assertEquals(MatrixRank.of(matrix), 3);
    assertInstanceOf(DecimalScalar.class, Det.of(matrix));
  }

  @Test
  void testRank2() {
    Tensor matrix = ToeplitzMatrix.of(Tensors.vector(0, 1, 0, 1, 0));
    SymmetricMatrixQ.INSTANCE.requireMember(matrix);
    assertEquals(RowReduce.of(matrix), Tensors.fromString("{{1, 0, 1}, {0, 1, 0}, {0, 0, 0}}"));
    assertEquals(MatrixRank.of(matrix), 2);
  }

  @Test
  void testFullRank() {
    Tensor matrix = RowReduce.of(ToeplitzMatrix.of(Tensors.vector(1, 2, 3, 5, 9)));
    assertEquals(matrix, IdentityMatrix.of(3));
  }

  @Test
  void testRankDeficient() {
    Tensor matrix = ToeplitzMatrix.of(Tensors.vector(1, 2, 3, 4, 5));
    assertEquals(Det.of(matrix), RealScalar.of(0));
    assertEquals(NullSpace.of(matrix), Tensors.fromString("{{1, -2, 1}}"));
  }

  @Test
  void testFailEven() {
    assertThrows(Exception.class, () -> ToeplitzMatrix.of(Tensors.vector(1, 2)));
  }

  @Test
  void testFailEmpty() {
    assertThrows(Exception.class, () -> ToeplitzMatrix.of(Tensors.empty()));
  }

  @Test
  void testFailScalar() {
    assertThrows(Throw.class, () -> ToeplitzMatrix.of(RealScalar.of(5)));
  }

  @Test
  void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> ToeplitzMatrix.of(HilbertMatrix.of(5)));
  }
}
