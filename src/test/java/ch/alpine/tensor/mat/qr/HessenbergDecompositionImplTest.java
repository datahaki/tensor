// code by jph
package ch.alpine.tensor.mat.qr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;

class HessenbergDecompositionImplTest {
  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testReal(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    matrix.set(Scalar::zero, 1, 0);
    HessenbergDecompositionImpl hessenbergDecompositionWiki = new HessenbergDecompositionImpl(matrix);
    TestHelper.check(matrix, hessenbergDecompositionWiki);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testComplex(int n) {
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n);
    HessenbergDecompositionImpl hessenbergDecompositionWiki = new HessenbergDecompositionImpl(matrix);
    TestHelper.check(matrix, hessenbergDecompositionWiki);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomUnits(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m")), n, n);
    HessenbergDecompositionImpl hessenbergDecompositionWiki = new HessenbergDecompositionImpl(matrix);
    TestHelper.check(matrix, hessenbergDecompositionWiki);
  }

  @Test
  void testIdMat() {
    Tensor matrix = IdentityMatrix.of(5);
    HessenbergDecompositionImpl hessenbergDecompositionWiki = new HessenbergDecompositionImpl(matrix);
    TestHelper.check(matrix, hessenbergDecompositionWiki);
  }
}
