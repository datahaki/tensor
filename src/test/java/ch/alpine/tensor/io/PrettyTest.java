// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.ResourceData;

class PrettyTest {
  @Test
  void testEmpty() {
    assertEquals(Pretty.of(Tensors.empty()), "[]");
  }

  @Test
  void testScalar() {
    Scalar scalar = ComplexScalar.of(3, 4);
    String string = Pretty.of(scalar);
    assertEquals(string, scalar.toString());
  }

  @Test
  void testVector() {
    String s = Pretty.of(Tensors.vector(10, 2, 3));
    assertEquals(s, "[ 10   2   3 ]");
  }

  @Test
  void testMatrix() {
    String s = Pretty.of(Tensors.matrixInt(new int[][] { { 10, 2, 3 }, { -1, 1, 9 } }));
    assertEquals(s, "[\n [ 10   2   3 ]\n [ -1   1   9 ]\n]");
  }

  @Test
  void testNonArray() {
    String s = Pretty.of(Tensors.fromString("{1, 2, {3}}"));
    assertEquals(s, "[\n 1  2  [ 3 ]\n]");
  }

  @Test
  void testNonArrayNested() {
    String s = Pretty.of(Tensors.fromString("{1, 2, {3, {4}}}"));
    assertEquals(s, "[\n 1  2  [\n 3   [ 4 ]\n ]\n]");
  }

  @Test
  void testRegressionV051() {
    Tensor tensor = Tensors.fromString("{1[A], {2, 3, 4, 5[s]}, {7, {8.1, 9.0}}, {{-1, 2, 3}, {4, 5, 6}}}");
    String string = Pretty.of(tensor);
    String pty051 = ResourceData.object("/ch/alpine/tensor/io/pretty.string");
    assertEquals(string, pty051);
  }
}
