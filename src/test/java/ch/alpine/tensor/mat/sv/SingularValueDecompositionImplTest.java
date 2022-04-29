// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.chq.FiniteTensorQ;
import ch.alpine.tensor.io.Get;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

public class SingularValueDecompositionImplTest {
  private static void _check(Tensor matrix) {
    assertTrue(SquareMatrixQ.of(matrix));
    InitTest.svd(matrix);
    Tensor svd = IdentityMatrix.of(matrix.length()).subtract(Transpose.of(matrix));
    InitTest.svd(svd);
  }

  @Test
  public void testResource() throws Exception {
    String string = getClass().getResource("/mat/svd0.mathematica").getPath();
    _check(Get.of(Paths.get(string).toFile()));
  }

  @Test
  public void testCondition1() {
    Tensor matrix = ResourceData.of("/mat/svd3.csv");
    InitTest.svd(matrix);
  }

  @Test
  public void testCondition2() {
    Tensor matrix = ResourceData.of("/mat/svd2.csv");
    InitTest.svd(matrix);
  }

  @Test
  public void testCondition1UnitA() {
    Tensor matrix = ResourceData.of("/mat/svd3.csv");
    InitTest.svd(matrix.map(s -> Quantity.of(s, "m")));
  }

  @Test
  public void testCondition1UnitB() {
    Tensor matrix = ResourceData.of("/mat/svd3.csv").map(s -> Quantity.of(s, "m"));
    matrix.append(matrix.get(0));
    InitTest.svd(matrix);
  }

  @Test
  public void testCondition2UnitA() {
    Tensor matrix = ResourceData.of("/mat/svd2.csv").map(s -> Quantity.of(s, "m"));
    InitTest.svd(matrix);
  }

  @Test
  public void testCondition2UnitB() {
    Tensor matrix = ResourceData.of("/mat/svd2.csv").map(s -> Quantity.of(s, "m"));
    matrix.append(matrix.get(0));
    InitTest.svd(matrix);
  }

  @Test
  public void testEps() {
    Tensor A = Tensors.fromString("{{1, 0}, {0, 1E-14}}");
    assertTrue(FiniteTensorQ.of(A));
    InitTest.svd(A.map(s -> Quantity.of(s, "kg")));
    SingularValueDecomposition svd = InitTest.svd(A);
    assertEquals(NullSpace.of(svd).length(), 1);
    assertEquals(NullSpace.of(svd, Chop._20), Tensors.empty());
    assertTrue(svd.toString().startsWith("SingularValueDecomposition["));
  }

  @Test
  public void testDecimalScalar() {
    Tensor matrix = HilbertMatrix.of(5, 3).map(N.DECIMAL128);
    SingularValueDecomposition.of(matrix);
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(SingularValueDecompositionImpl.class.getModifiers()));
  }

  @Test
  public void testUnit() {
    Tensor a = Tensors.fromString("{{1, 0}, {0, 0}}").map(s -> Quantity.of(s, "m"));
    SingularValueDecomposition svd = SingularValueDecomposition.of(a);
    ExactTensorQ.require(svd.values());
    assertEquals(svd.values(), Tensors.fromString("{1[m], 0[m]}"));
    Tensor tensor = PseudoInverse.of(svd);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{{1[m^-1], 0[m^-1]}, {0[m^-1], 0[m^-1]}}"));
  }
}
