// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;

class HessenbergDecompositionTest {
  private static final HessenbergDecomposition _check(Tensor matrix) {
    HessenbergDecomposition hessenbergDecomposition = HessenbergDecomposition.of(matrix);
    Tensor p = hessenbergDecomposition.getUnitary();
    UnitaryMatrixQ.require(p);
    Tensor h = hessenbergDecomposition.getH();
    Tolerance.CHOP.requireClose(UpperTriangularize.of(h, -1), h);
    Tensor result = Dot.of(p, h, ConjugateTranspose.of(p));
    Tolerance.CHOP.requireClose(matrix, result);
    return hessenbergDecomposition;
  }

  @Test
  void testMathematicaEx() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}}");
    HessenbergDecomposition hessenbergDecomposition = Serialization.copy(_check(matrix));
    assertTrue(hessenbergDecomposition.toString().startsWith("HessenbergDecomposition["));
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandom(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    _check(matrix);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomUnits(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m")), n, n);
    _check(matrix);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomExact(int n) {
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-1, 2), n, n);
    _check(matrix);
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(ints = { 3 })
  void testRandomComplex(int n) {
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n);
    _check(matrix);
  }
}
