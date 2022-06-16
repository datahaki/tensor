// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

class RowReduceTest {
  // from Mathematica, RowReduce Applications: solving a linear system
  @Test
  void testReduce1() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 1}, {5, 6, 7, 1}, {7, 8, 9, 1}}");
    Tensor reduce = RowReduce.of(matrix);
    Tensor sol = Tensors.fromString("{{1, 0, -1, -1}, {0, 1, 2, 1}, {0, 0, 0, 0}}");
    assertEquals(reduce, sol);
  }

  // from Mathematica, RowReduce Applications: a linear system without solution
  @Test
  void testReduce2() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 1}, {5, 6, 7, -2}, {7, 8, 9, 1}}");
    Tensor reduce = RowReduce.of(matrix);
    Tensor sol = Tensors.fromString("{{1, 0, -1, 0}, {0, 1, 2, 0}, {0, 0, 0, 1}}");
    assertEquals(reduce, sol);
  }

  // from Mathematica, RowReduce Applications: for a degenerate square matrix
  @Test
  void testReduce3() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 4, 1, 0, 0, 0}, {5, 6, 7, 8, 0, 1, 0, 0}, {9, 10, 11, 12, 0, 0, 1, 0}, {13, 14, 15, 16, 0, 0, 0, 1}}");
    Tensor reduce = RowReduce.of(matrix);
    Tensor sol = Tensors.fromString("{{1, 0, -1, -2, 0, 0, -7/2, 5/2}, {0, 1, 2, 3, 0, 0, 13/4, -9/4}, {0, 0, 0, 0, 1, 0, -3, 2}, {0, 0, 0, 0, 0, 1, -2, 1}}");
    assertEquals(reduce, sol);
  }

  @Test
  void testSome() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { -1, -2, -1 }, //
        { -3, 1, 5 }, //
        { 3, 6, 3 }, //
        { 1, 2, 1 } //
    });
    Tensor r = RowReduce.of(A);
    assertEquals(Dimensions.of(r), Dimensions.of(A));
  }

  @Test
  void testReduce2N() {
    Tensor matrix = N.DOUBLE.of(Tensors.fromString("{{1, 2, 3, 1}, {5, 6, 7, -2}, {7, 8, 9, 1}}"));
    Tensor reduce = RowReduce.of(matrix);
    Tensor sol = Tensors.fromString("{{1, 0, -1, 0}, {0, 1, 2, 0}, {0, 0, 0, 1}}");
    Tolerance.CHOP.requireClose(reduce, sol);
  }

  @Test
  void testQuantity1() {
    Tensor ve1 = Tensors.of(Quantity.of(1, "m"), Quantity.of(2, "m"));
    Tensor ve2 = Tensors.of(Quantity.of(2, "m"), Quantity.of(10, "m"));
    Tensor nul = RowReduce.of(Tensors.of(ve1, ve2));
    assertEquals(nul, IdentityMatrix.of(2)); // consistent with Mathematica
  }

  @Test
  void testQuantity2() {
    Tensor ve1 = Tensors.of(Quantity.of(1, "m"), Quantity.of(2, "m"));
    Tensor nul = RowReduce.of(Tensors.of(ve1, ve1));
    Tensor expect = Tensors.fromString("{{1, 2}, {0[m], 0[m]}}");
    assertEquals(nul, expect);
    Chop.NONE.requireClose(nul, expect);
  }

  @Test
  void testPivots() {
    Tensor matrix = HilbertMatrix.of(3, 5);
    Tensor gf1 = RowReduce.of(matrix, Pivots.ARGMAX_ABS);
    Tensor gf2 = RowReduce.of(Reverse.of(matrix), Pivots.FIRST_NON_ZERO);
    assertEquals(gf1, gf2);
  }

  @Test
  void testEmpty() {
    Tensor matrix = Tensors.fromString("{{}, {}}");
    assertEquals(RowReduce.of(matrix), matrix);
  }

  @Test
  void testUnstructuredFail() {
    Tensor matrix = Tensors.fromString("{{}, {2, 3}}");
    assertThrows(IndexOutOfBoundsException.class, () -> RowReduce.of(matrix));
  }

  @Test
  void testVectorFail() {
    assertThrows(IllegalArgumentException.class, () -> RowReduce.of(Tensors.vector(1, 2, 3)));
  }
}
