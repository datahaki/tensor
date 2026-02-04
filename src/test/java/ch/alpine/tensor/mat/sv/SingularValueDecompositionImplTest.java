// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.chq.FiniteTensorQ;
import ch.alpine.tensor.io.Get;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

class SingularValueDecompositionImplTest {
  private static void _check(Tensor matrix) {
    assertTrue(SquareMatrixQ.INSTANCE.isMember(matrix));
    SingularValueDecompositionWrap.of(matrix);
    Tensor svd = IdentityMatrix.of(matrix.length()).subtract(Transpose.of(matrix));
    SingularValueDecompositionWrap.of(svd);
  }

  @Test
  void testResource() throws Exception {
    _check(Get.of(Unprotect.file("/ch/alpine/tensor/mat/sv/svd0.mathematica")));
  }

  @Test
  void testCondition1() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/sv/svd3.csv");
    SingularValueDecompositionWrap.of(matrix);
  }

  @Test
  void testCondition2() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/sv/svd2.csv");
    SingularValueDecompositionWrap.of(matrix);
  }

  @Test
  void testCondition1UnitA() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/sv/svd3.csv");
    SingularValueDecompositionWrap.of(matrix.map(s -> Quantity.of(s, "m")));
  }

  @Test
  void testCondition1UnitB() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/sv/svd3.csv").map(s -> Quantity.of(s, "m"));
    matrix.append(matrix.get(0));
    SingularValueDecompositionWrap.of(matrix);
  }

  @Test
  void testCondition2UnitA() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/sv/svd2.csv").map(s -> Quantity.of(s, "m"));
    SingularValueDecompositionWrap.of(matrix);
  }

  @Test
  void testCondition2UnitB() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/sv/svd2.csv").map(s -> Quantity.of(s, "m"));
    matrix.append(matrix.get(0));
    SingularValueDecompositionWrap.of(matrix);
  }

  @Test
  void testEps() {
    Tensor A = Tensors.fromString("{{1, 0}, {0, 1E-14}}");
    assertTrue(FiniteTensorQ.of(A));
    SingularValueDecompositionWrap.of(A.map(s -> Quantity.of(s, "kg")));
    SingularValueDecomposition svd = SingularValueDecompositionWrap.of(A);
    assertEquals(NullSpace.of(svd).length(), 1);
    assertEquals(NullSpace.of(svd, Chop._20), Tensors.empty());
    assertTrue(svd.toString().startsWith("SingularValueDecomposition["));
  }

  @Test
  void testDecimalScalar() {
    Tensor matrix = HilbertMatrix.of(5, 3).map(N.DECIMAL128);
    SingularValueDecompositionWrap.of(matrix);
  }

  @ParameterizedTest
  @ValueSource(ints = { 3, 5, 10 })
  void testDecimalScalar(int n) {
    SingularValueDecompositionWrap.of(RandomVariate.of(UniformDistribution.unit(50), n + 2, n));
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(SingularValueDecompositionImpl.class.getModifiers()));
  }

  @Test
  void testConvergenceArtificialFail() {
    Tensor matrix = HilbertMatrix.of(5, 3).map(N.DECIMAL128);
    SingularValueDecomposition.MAX_ITERATIONS.set(2);
    assertThrows(Exception.class, () -> SingularValueDecomposition.of(matrix));
    SingularValueDecomposition.MAX_ITERATIONS.remove();
  }

  @Test
  void testUnit() {
    Tensor a = Tensors.fromString("{{1, 0}, {0, 0}}").map(s -> Quantity.of(s, "m"));
    SingularValueDecomposition svd = SingularValueDecompositionWrap.of(a);
    ExactTensorQ.require(svd.values());
    assertEquals(svd.values(), Tensors.fromString("{1[m], 0[m]}"));
    Tensor tensor = PseudoInverse.of(svd);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{{1[m^-1], 0[m^-1]}, {0[m^-1], 0[m^-1]}}"));
  }
}
