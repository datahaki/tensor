// code by guedelmi
package ch.alpine.tensor.mat.ev;

import java.lang.reflect.Modifier;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.BasisTransform;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.alg.TensorComparator;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.mat.sv.SingularValueList;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.red.Pmul;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import junit.framework.TestCase;

public class JacobiMethodTest extends TestCase {
  private static void checkEquation(Tensor matrix, Eigensystem eigensystem) {
    assertTrue(eigensystem.toString().startsWith("Eigensystem["));
    Tensor vectors = eigensystem.vectors();
    Tensor values = eigensystem.values();
    {
      Tensor sol = LinearSolve.of(vectors, Pmul.of(values, vectors));
      Tolerance.CHOP.requireClose(sol, matrix);
    }
    {
      Tensor sol = Transpose.of(vectors).dot(Pmul.of(values, vectors));
      Tolerance.CHOP.requireClose(sol, matrix);
    }
    Tensor Vi = Inverse.of(eigensystem.vectors());
    Tensor diagonalMatrix = DiagonalMatrix.with(eigensystem.values());
    Tensor res = Vi.dot(diagonalMatrix).dot(eigensystem.vectors());
    Tensor btr = BasisTransform.ofMatrix(diagonalMatrix, eigensystem.vectors());
    Tolerance.CHOP.requireClose(res, matrix);
    Tolerance.CHOP.requireClose(btr, matrix);
    assertEquals(res.subtract(matrix).map(Chop._08), matrix.multiply(RealScalar.ZERO));
    // testing determinant
    Scalar det = Det.of(matrix);
    Tensor prd = Times.pmul(eigensystem.values());
    Tolerance.CHOP.requireClose(det, prd);
    Tensor norm = Tensor.of(eigensystem.vectors().stream().map(Vector2Norm::of));
    Tolerance.CHOP.requireClose(norm, Tensors.vector(i -> RealScalar.ONE, norm.length()));
    // testing orthogonality
    final Tensor Vt = Transpose.of(eigensystem.vectors());
    int n = eigensystem.values().length();
    Tensor id = IdentityMatrix.of(n);
    Tolerance.CHOP.requireClose(Vt.dot(eigensystem.vectors()), id);
    Tolerance.CHOP.requireClose(eigensystem.vectors().dot(Vt), id);
    assertTrue(OrthogonalMatrixQ.of(eigensystem.vectors()));
    assertTrue(OrthogonalMatrixQ.of(Vt));
    // assert that values are sorted from max to min
    assertEquals(eigensystem.values(), Reverse.of(Sort.of(eigensystem.values())));
  }

  public void testJacobiWithTensor1() {
    Tensor tensor = Tensors.fromString("{{2, 3, 0, 1}, {3, 1, 7, 5}, {0, 7, 10, 9}, {1, 5, 9, 13}}");
    Eigensystem eigensystem = Eigensystem.ofSymmetric(tensor);
    Tensor expEigvl = Tensors.fromString("{ 23.853842147040694,  3.3039323944179757, 2.8422254585413294, -4}");
    Tensor expEigvc = Transpose.of(Tensors.fromString(
        "{{0.08008068980475883, -0.5948978891329353, 0.6877622539503787, -0.4082482904638631}, {0.35340348774478036, -0.45368409809391813, 0.05108862221538444, 0.8164965809277261}, {0.6267262856848018, -0.3124703070549013, -0.5855850095196097, -0.408248290463863}, {0.6898602907849903, 0.5853456652704466, 0.4259850130546227, -6.117111473932377E-17}}"));
    Tolerance.CHOP.requireClose(expEigvl.subtract(eigensystem.values()), Array.zeros(4));
    Tolerance.CHOP.requireClose(expEigvc.subtract(eigensystem.vectors()), Array.zeros(4, 4));
    checkEquation(tensor, eigensystem);
  }

  public void testJacobiWithTensor2() {
    Tensor tensor = Tensors.fromString("{{0, 3, 0, 1}, {3, 0, 7, 5}, {0, 7, -2, 9}, {1, 5, 9, 0}}");
    Eigensystem eigensystem = Eigensystem.ofSymmetric(tensor);
    Tensor expEigvl = Tensors.fromString("{13.741843166529974899, 0.42515310634896474734, -5.4100072556794011520, -10.756989017199538494}");
    Tensor expEigvc = Tensors.empty();
    expEigvc.append(Tensors.vector(-0.16135639309137209668, -0.54323075821807030942, -0.57753040539597776347, -0.58764197312438517024));
    expEigvc.append(Tensors.vector(-0.92624362790255729732, -0.20474246988585952709, 0.22707481664854011532, 0.22043205401887170741));
    expEigvc.append(Tensors.vector(0.31354840524937794336, -0.75632770019948717296, 0.041096194862756938126, 0.57268395319262161336));
    expEigvc.append(Tensors.vector(-0.13313246690429592645, 0.30157797376766923332, -0.78307519514730625756, 0.52737056301918462298));
    checkEquation(tensor, eigensystem);
    assertEquals(Tolerance.CHOP.of(expEigvl.subtract(eigensystem.values())), Array.zeros(4));
  }

  public void testHilberts() {
    for (int size = 1; size < 10; ++size) {
      Tensor matrix = HilbertMatrix.of(size);
      Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
      checkEquation(matrix, eigensystem);
      Tensor values1 = SingularValueList.of(matrix);
      Tolerance.CHOP.requireClose(eigensystem.values(), values1);
    }
  }

  public void testZeros() {
    for (int c = 1; c < 10; ++c) {
      Tensor matrix = Array.zeros(c, c);
      Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
      checkEquation(matrix, eigensystem);
      SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
      Tensor values1 = Reverse.of(Sort.of(svd.values()));
      Tensor values2 = Sort.of(svd.values(), TensorComparator.INSTANCE.reversed());
      Tolerance.CHOP.requireClose(eigensystem.values(), values1);
      assertEquals(values1, values2);
    }
  }

  public void testHilbert1() {
    Tensor matrix = HilbertMatrix.of(1);
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor expected = Tensors.vector(1);
    Tolerance.CHOP.requireClose(expected.subtract(eigensystem.values()), Array.zeros(matrix.length()));
  }

  public void testHilbert2() {
    Tensor matrix = HilbertMatrix.of(2);
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor expected = Tensors.vector(1.2675918792439982155, 0.065741454089335117813);
    Tolerance.CHOP.requireClose(expected.subtract(eigensystem.values()), Array.zeros(matrix.length()));
  }

  public void testHilbert3() {
    Tensor matrix = HilbertMatrix.of(3);
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor expected = Tensors.vector(1.4083189271236539575, 0.12232706585390584656, 0.0026873403557735292310);
    Tolerance.CHOP.requireClose(expected.subtract(eigensystem.values()), Array.zeros(matrix.length()));
  }

  public void testChallenge1() {
    Tensor matrix = ResourceData.of("/mat/jacobi1.csv");
    Tolerance.CHOP.requireClose(matrix, IdentityMatrix.of(3));
    checkEquation(matrix, Eigensystem.ofSymmetric(matrix));
  }

  public void testChallenge2() {
    Tensor matrix = ResourceData.of("/mat/jacobi2.csv");
    Tolerance.CHOP.requireClose(matrix, IdentityMatrix.of(3));
    checkEquation(matrix, Eigensystem.ofSymmetric(matrix));
  }

  public void testDecimalScalar() {
    Tensor matrix = HilbertMatrix.of(5).map(N.DECIMAL128);
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    assertTrue(eigensystem.vectors().Get(3, 3) instanceof DecimalScalar);
    assertTrue(eigensystem.values().Get(4) instanceof DecimalScalar);
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Eigensystem.class.getModifiers()));
    assertFalse(Modifier.isPublic(EigensystemImpl.class.getModifiers()));
  }
}