// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixFormTest extends TestCase {
  public void testEmpty() {
    assertEquals(MatrixForm.of(Tensors.empty()), "");
  }

  public void testMatrix() {
    Tensor matrix = Tensors.fromString("{{1, 2, 321341234}, {2, 44, 12333}}");
    String string = MatrixForm.of(matrix);
    assertEquals(string, "1  2 321341234\n2 44     12333");
  }

  public void testMatrixBracket() {
    Tensor matrix = Tensors.fromString("{{1, 2, 321341234}, {2, 44, 12333}}");
    String string = MatrixForm.of(matrix, "  ", "[ ", " ]");
    assertEquals(string, "[ 1   2  321341234 ]\n[ 2  44      12333 ]");
  }

  public void testMatrixSeparator() {
    Tensor matrix = Tensors.fromString("{{1, 2, 321341234}, {2, 44, 12333}}");
    String string = MatrixForm.of(matrix, "|", "", "");
    assertEquals(string, "1| 2|321341234\n2|44|    12333");
  }

  public void test3Form() {
    Tensor matrix = Tensors.fromString("{{{1, 2}, 321341234}, {2, {44, 12333}}}");
    String string = MatrixForm.of(matrix, "  ", "[ ", " ]");
    String string2 = "[ {1, 2}    321341234 ]\n[      2  {44, 12333} ]";
    assertEquals(string, string2);
  }

  public void testScalarFail() {
    AssertFail.of(() -> MatrixForm.of(RealScalar.ONE));
  }
}
