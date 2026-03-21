// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class MatrixGradientTest {
  @Test
  void testSimple() {
    Tensor matrix = HilbertMatrix.of(5, 6);
    MatrixGradient matrixGradient = MatrixGradient.of(matrix);
    assertEquals(Dimensions.of(matrix), Dimensions.of(matrixGradient.dx()));
    assertEquals(Dimensions.of(matrix), Dimensions.of(matrixGradient.dy()));
    Tensor diff = matrixGradient.array();
    List<Integer> list = Dimensions.of(diff);
    assertEquals(list.get(2), 2);
  }

  @RepeatedTest(7)
  void testSmall(RepetitionInfo repetitionInfo) {
    int k = repetitionInfo.getCurrentRepetition();
    Tensor matrix = HilbertMatrix.of(k, 3);
    MatrixGradient matrixGradient = MatrixGradient.of(matrix);
    assertEquals(Dimensions.of(matrix), Dimensions.of(matrixGradient.dx()));
    assertEquals(Dimensions.of(matrix), Dimensions.of(matrixGradient.dy()));
    List<Integer> list = Dimensions.of(matrixGradient.array());
    assertEquals(list.subList(0, 2), Dimensions.of(matrix));
    assertEquals(list.get(2), 2);
    Tensor array = matrixGradient.array();
    assertEquals(array.get(0, 0), matrixGradient.Get(0, 0));
    assertEquals(array.get(0, 1), matrixGradient.Get(0, 1));
    Tensor cross = matrixGradient.cross();
    assertEquals(cross.get(0, 0), matrixGradient.Cross(0, 0));
    assertEquals(cross.get(0, 1), matrixGradient.Cross(0, 1));
  }

  @Test
  void testIdentity() {
    Tensor matrix = IdentityMatrix.of(10);
    MatrixGradient matrixGradient = MatrixGradient.of(matrix);
    assertEquals(Dimensions.of(matrix), Dimensions.of(matrixGradient.dx()));
    assertEquals(Dimensions.of(matrix), Dimensions.of(matrixGradient.dy()));
    Tensor diff = matrixGradient.array();
    List<Integer> list = Dimensions.of(diff);
    assertEquals(list.get(2), 2);
  }

  @RepeatedTest(7)
  void test3Dim(RepetitionInfo repetitionInfo) {
    int k = repetitionInfo.getCurrentRepetition();
    Tensor tensor = RandomVariate.of(UniformDistribution.unit(), k, 5, 3);
    MatrixGradient matrixGradient = MatrixGradient.of(tensor);
    assertEquals(Dimensions.of(tensor), Dimensions.of(matrixGradient.dx()));
    assertEquals(Dimensions.of(tensor), Dimensions.of(matrixGradient.dy()));
    List<Integer> list = Dimensions.of(tensor);
    list.add(2);
    assertEquals(list, Dimensions.of(matrixGradient.array()));
    // Tensor array = matrixGradient.array();
    // assertEquals(array.get(0, 0), matrixGradient.Get(0, 0));
    // assertEquals(array.get(0, 1), matrixGradient.Get(0, 1));
    // Tensor cross = matrixGradient.cross();
    // assertEquals(cross.get(0, 0), matrixGradient.Cross(0, 0));
    // assertEquals(cross.get(0, 1), matrixGradient.Cross(0, 1));
  }
}
