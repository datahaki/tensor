// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.LowerTriangularize;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class HessenbergDecompositionTest {
  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testReal(int n) throws ClassNotFoundException, IOException {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    matrix.set(Scalar::zero, 1, 0);
    HessenbergDecomposition hessenbergDecomposition = HessenbergDecomposition.of(matrix);
    assertTrue(hessenbergDecomposition.toString().startsWith("HessenbergDecomposition["));
    TestHelper.check(matrix, hessenbergDecomposition);
    Serialization.copy(hessenbergDecomposition);
    Tensor lt = LowerTriangularize.of(hessenbergDecomposition.getH(), -2);
    Chop.NONE.requireAllZero(lt);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testComplex(int n) {
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n);
    HessenbergDecomposition hessenbergDecomposition = HessenbergDecomposition.of(matrix);
    TestHelper.check(matrix, hessenbergDecomposition);
    Tensor h = hessenbergDecomposition.getH();
    Tensor lt = LowerTriangularize.of(h, -2);
    Chop.NONE.requireAllZero(lt);
    HessenbergDecomposition hd2 = HessenbergDecomposition.of(h);
    TestHelper.check(h, hd2);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomUnits(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m")), n, n);
    HessenbergDecomposition hessenbergDecomposition = HessenbergDecomposition.of(matrix);
    TestHelper.check(matrix, hessenbergDecomposition);
    Tensor h = hessenbergDecomposition.getH();
    Tensor lt = LowerTriangularize.of(h, -2);
    Chop.NONE.requireAllZero(lt);
    HessenbergDecomposition hd2 = HessenbergDecomposition.of(h);
    TestHelper.check(h, hd2);
  }

  @Test
  void testIdMat() {
    Tensor matrix = IdentityMatrix.of(5);
    HessenbergDecomposition hessenbergDecomposition = HessenbergDecomposition.of(matrix);
    TestHelper.check(matrix, hessenbergDecomposition);
  }

  @ParameterizedTest
  @ValueSource(ints = { 4, 15, 20, 31 })
  void testSoN(int n) {
    Tensor x = RandomVariate.of(NormalDistribution.standard(), n);
    Tensor y = RandomVariate.of(NormalDistribution.standard(), n);
    Tensor matrix = TensorWedge.of(x, y);
    HessenbergDecomposition hessenbergDecomposition = HessenbergDecomposition.of(matrix);
    TestHelper.check(matrix, hessenbergDecomposition);
  }
}
