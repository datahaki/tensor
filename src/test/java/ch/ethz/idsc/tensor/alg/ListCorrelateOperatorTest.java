// code by jph
package ch.ethz.idsc.tensor.alg;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ListCorrelateOperatorTest extends TestCase {
  public void testNarrow1() {
    Tensor kernel = Tensors.vector(2, 1, 3);
    Tensor tensor = Tensors.vector(4, 5);
    AssertFail.of(() -> ListCorrelate.of(kernel, tensor));
  }

  public void testNarrow2() {
    Tensor kernel = Tensors.fromString("{{1, 2, 3}}");
    Tensor tensor = Tensors.fromString("{{1, 2}}");
    AssertFail.of(() -> ListCorrelate.of(kernel, tensor));
  }

  public void testNarrow3() {
    Tensor kernel = Tensors.fromString("{{1, 2, 3}, {2, 3, 4}}");
    Tensor tensor = Tensors.fromString("{{1, 2, 3}}");
    AssertFail.of(() -> ListCorrelate.of(kernel, tensor));
  }

  public void testScalarFail() {
    Tensor kernel = RealScalar.ZERO;
    Tensor tensor = RealScalar.ONE;
    AssertFail.of(() -> ListCorrelate.of(kernel, tensor));
  }

  public void testRankFail() {
    Tensor kernel = Tensors.vector(1, -1);
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 1, 3, 0, 1 }, //
        { 0, 1, -1, 3, 3 } });
    AssertFail.of(() -> ListCorrelate.of(kernel, matrix));
  }

  public void testNullFail() {
    AssertFail.of(() -> ListCorrelate.with(null));
  }

  /***************************************************/
  public void testConvolveNullFail() {
    AssertFail.of(() -> ListConvolve.with(null));
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(ListCorrelate.class.getModifiers()));
    assertFalse(Modifier.isPublic(ListCorrelateOperator.class.getModifiers()));
  }
}
