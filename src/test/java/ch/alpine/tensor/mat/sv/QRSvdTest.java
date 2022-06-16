// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.qr.GramSchmidt;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class QRSvdTest {
  @Test
  void testSquare() {
    Tensor matrix = HilbertMatrix.of(6);
    QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
    SingularValueDecomposition approximateSvd = QRSvd.of(qrDecomposition);
    Tensor v1 = SingularValueList.of(matrix);
    Tensor v2 = SingularValueList.of(approximateSvd);
    Tolerance.CHOP.requireClose(v1, v2);
  }

  @RepeatedTest(3)
  void testRect5x2() throws ClassNotFoundException, IOException {
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(), 5, 2);
    QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
    List<Integer> list = Dimensions.of(qrDecomposition.getR());
    // TODO TENSOR QR the zeros in r are to be avoided when computing SVD
    // System.out.println(list);
    list.size();
    SingularValueDecomposition approximateSvd = Serialization.copy(QRSvd.of(qrDecomposition));
    Tensor v1 = SingularValueList.of(matrix);
    Tensor v2 = SingularValueList.of(approximateSvd);
    Tolerance.CHOP.requireClose(v1, v2);
  }

  @RepeatedTest(3)
  void testRect5x3() {
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(), 5, 3);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(3, 3));
    SingularValueDecomposition approximateSvd = QRSvd.of(qrDecomposition);
    Tensor v1 = SingularValueList.of(matrix);
    Tensor v2 = SingularValueList.of(approximateSvd);
    Tolerance.CHOP.requireClose(v1, v2);
  }

  @RepeatedTest(3)
  void testMatrix6x4() {
    Tensor matrix = RandomVariate.of(TriangularDistribution.with(0.2, 1), 6, 4);
    SingularValueDecomposition svd = QRSvd.of(matrix);
    Tensor v1 = SingularValueList.of(matrix);
    Tensor v2 = SingularValueList.of(svd);
    Tolerance.CHOP.requireClose(v1, v2);
    Tensor approx = Dot.of(svd.getU(), DiagonalMatrix.with(svd.values()), Transpose.of(svd.getV()));
    Tolerance.CHOP.requireClose(approx, matrix);
  }

  void testMatrix6x4NonTrivial() {
    Random random = new Random(3);
    Tensor matrix = RandomVariate.of(LogNormalDistribution.standard(), random, 6, 4);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    assertEquals(Tensors.vectorInt(qrDecomposition.sigma()), Tensors.vector(2, 0, 3, 1));
    SingularValueDecomposition svd = QRSvd.of(qrDecomposition);
    Tensor v1 = SingularValueList.of(matrix);
    Tensor v2 = SingularValueList.of(svd);
    Tolerance.CHOP.requireClose(v1, v2);
    Tensor approx = Dot.of(svd.getU(), DiagonalMatrix.with(svd.values()), Transpose.of(svd.getV()));
    Tolerance.CHOP.requireClose(approx, matrix);
  }

  void testMatrix60x4NonTrivial() {
    Random random = new Random(2);
    Tensor matrix = RandomVariate.of(LogNormalDistribution.standard(), random, 6, 4);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    assertEquals(Tensors.vectorInt(qrDecomposition.sigma()), Tensors.vector(3, 1, 2, 0));
    SingularValueDecomposition svd = QRSvd.of(qrDecomposition);
    Tensor v1 = SingularValueList.of(matrix);
    Tensor v2 = SingularValueList.of(svd);
    Tolerance.CHOP.requireClose(v1, v2);
    Tensor approx = Dot.of(svd.getU(), DiagonalMatrix.with(svd.values()), Transpose.of(svd.getV()));
    Tolerance.CHOP.requireClose(approx, matrix);
  }
}
