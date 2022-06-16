// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;

class MatrixFormTest {
  @Test
  void testEmpty() {
    assertEquals(MatrixForm.of(Tensors.empty()), "");
  }

  @Test
  void testMatrix() {
    Tensor matrix = Tensors.fromString("{{1, 2, 321341234}, {2, 44, 12333}}");
    String string = MatrixForm.of(matrix);
    assertEquals(string, "1  2 321341234\n2 44     12333");
  }

  @Test
  void testMatrixBracket() {
    Tensor matrix = Tensors.fromString("{{1, 2, 321341234}, {2, 44, 12333}}");
    String string = MatrixForm.of(matrix, "  ", "[ ", " ]");
    assertEquals(string, "[ 1   2  321341234 ]\n[ 2  44      12333 ]");
  }

  @Test
  void testMatrixSeparator() {
    Tensor matrix = Tensors.fromString("{{1, 2, 321341234}, {2, 44, 12333}}");
    String string = MatrixForm.of(matrix, "|", "", "");
    assertEquals(string, "1| 2|321341234\n2|44|    12333");
  }

  @Test
  void test3Form() {
    Tensor matrix = Tensors.fromString("{{{1, 2}, 321341234}, {2, {44, 12333}}}");
    String string = MatrixForm.of(matrix, "  ", "[ ", " ]");
    String string2 = "[ {1, 2}    321341234 ]\n[      2  {44, 12333} ]";
    assertEquals(string, string2);
  }

  @Test
  void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> MatrixForm.of(RealScalar.ONE));
  }
}
