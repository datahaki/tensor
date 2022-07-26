// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ParetoDistribution;
import ch.alpine.tensor.pdf.d.PascalDistribution;
import ch.alpine.tensor.pdf.d.PoissonDistribution;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.red.Total;

class UpperTriangularizeTest {
  @Test
  void testIncludingDiagonal() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 5, 2}}");
    Tensor actual = Tensors.fromString("{{1, 2, 3}, {0, 5, 6}, {0, 0, 9}, {0, 0, 0}}");
    assertEquals(UpperTriangularize.of(matrix), actual);
  }

  @Test
  void testAboveDiagonal() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 5, 2}}");
    Tensor actual = Tensors.fromString("{{0, 2, 3}, {0, 0, 6}, {0, 0, 0}, {0, 0, 0}}");
    assertEquals(UpperTriangularize.of(matrix, 1), actual);
  }

  @Test
  void testFull() {
    Distribution distribution = PoissonDistribution.of(10);
    Tensor matrix = RandomVariate.of(distribution, 10, 10);
    Tensor result = Total.of(Tensors.of( //
        LowerTriangularize.of(matrix, -1), //
        DiagonalMatrix.with(Diagonal.of(matrix)), //
        UpperTriangularize.of(matrix, 1)));
    assertEquals(matrix, result);
  }

  @Test
  void testRectangular1() {
    Distribution distribution = ParetoDistribution.of(0.3, 0.4);
    Tensor matrix = RandomVariate.of(distribution, 8, 12);
    for (int k = -12; k <= 12; ++k) {
      Tensor result = Total.of(Tensors.of( //
          LowerTriangularize.of(matrix, k), //
          UpperTriangularize.of(matrix, k + 1)));
      assertEquals(matrix, result);
    }
  }

  @Test
  void testRectangular2() {
    Distribution distribution = PascalDistribution.of(3, .2);
    Tensor matrix = RandomVariate.of(distribution, 12, 8);
    for (int k = -12; k <= 12; ++k) {
      Tensor result = Total.of(Tensors.of( //
          LowerTriangularize.of(matrix, k), //
          UpperTriangularize.of(matrix, k + 1)));
      assertEquals(matrix, result);
    }
  }

  @Test
  void testEmpty() {
    Tensor matrix = Tensors.fromString("{{}}");
    for (int k = -2; k < 3; ++k) {
      assertEquals(matrix, LowerTriangularize.of(matrix, k));
      assertEquals(matrix, UpperTriangularize.of(matrix, k));
    }
  }

  @Test
  void test1x1() {
    Tensor matrix = Tensors.fromString("{{1}}");
    for (int k = -3; k <= 3; ++k)
      assertEquals(Tensors.fromString("{{" + (k <= 0 ? 1 : 0) + "}}"), UpperTriangularize.of(matrix, k));
  }

  @Test
  void testGaussScalar() {
    Tensor matrix = Tensors.matrix((i, j) -> GaussScalar.of(2 * i + j + 1, 7), 3, 4);
    for (int k = -3; k < 5; ++k) {
      Tensor lower = LowerTriangularize.of(matrix, k);
      Tensor upper = UpperTriangularize.of(matrix, k + 1);
      assertEquals(matrix, lower.add(upper));
    }
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> UpperTriangularize.of(RealScalar.ONE, 0));
  }

  @Test
  void testRank3Fail() {
    assertThrows(ClassCastException.class, () -> UpperTriangularize.of(LeviCivitaTensor.of(3), 0));
  }
}
