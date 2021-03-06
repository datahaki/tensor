// code by jph
package ch.alpine.tensor.red;

import java.io.IOException;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.MapThread;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MinTest extends TestCase {
  static Tensor min(List<Tensor> col) {
    return col.stream().reduce(Min::of).get();
  }

  public void testColumnwise() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { 1, 3, 3 }, { 2, 2, 7 } });
    Tensor res = matrix.stream().reduce(Entrywise.min()).get();
    Tensor map = MapThread.of(MinTest::min, matrix.stream().collect(Collectors.toList()), 1);
    assertEquals(map, Tensors.vector(1, 2, 3));
    assertEquals(map, res);
  }

  public void testRowwise() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { 8, 3, 3 }, { 2, 2, 7 } });
    Tensor map = Tensor.of(matrix.stream().map( //
        row -> row.stream().reduce(Min::of).get()));
    assertEquals(map, Tensors.vector(3, 2));
  }

  public void testElementWise() {
    Tensor tensor = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    Tensor capped = tensor.map(Min.function(RealScalar.ZERO));
    Tensor blub = Tensors.matrixInt(new int[][] { { -8, 0, -3 }, { 0, -2, 0 } });
    assertEquals(capped, blub);
  }

  public void testSet() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    ScalarUnaryOperator _op = Min.function(RealScalar.ZERO);
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(_op);
    matrix.set(scalarUnaryOperator, Tensor.ALL, 0);
    Tensor blub = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 0, -2, 7 } });
    assertEquals(matrix, blub);
  }

  public void testGenericInteger() {
    UnaryOperator<Integer> function = Min.function(100);
    assertEquals(function.apply(50), Integer.valueOf(50));
    assertEquals(function.apply(200), Integer.valueOf(100));
  }

  public void testGenericString() {
    UnaryOperator<String> function = Min.function("math");
    assertEquals(function.apply("library"), "library");
    assertEquals(function.apply("tensor"), "math");
  }

  public void testFail() {
    Scalar string = StringScalar.of("string");
    Scalar gauss = GaussScalar.of(1, 3);
    AssertFail.of(() -> Min.of(string, gauss));
    AssertFail.of(() -> Min.of(gauss, string));
  }
}
