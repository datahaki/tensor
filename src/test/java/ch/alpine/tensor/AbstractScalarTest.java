// code by jph
package ch.alpine.tensor;

import java.util.Arrays;

import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class AbstractScalarTest extends TestCase {
  public void testMap() {
    Tensor c = Tensors.fromString("{{1}, {4}, {4}}");
    Tensor a = Tensors.vector(1, 2, 3);
    Tensor b = Tensors.vector(4).unmodifiable();
    a = a.map(s -> b);
    a.set(RealScalar.ONE, 0, 0); // requires copy() in AbstractScalar::map
    assertEquals(a, c);
  }

  public void testSet() {
    Tensor a = Tensors.vector(1, 2, 3);
    Tensor b = Tensors.vector(4).unmodifiable();
    a.set(s -> b, Tensor.ALL);
    a.set(RealScalar.ONE, 0, 0);
    Tensor c = Tensors.fromString("{{1}, {4}, {4}}");
    assertEquals(a, c);
  }

  public void testSetAll() {
    Tensor matrix = HilbertMatrix.of(5);
    matrix.set(Tensor::negate, Tensor.ALL, 1);
    matrix.set(Tensor::negate, 3, Tensor.ALL);
    matrix.set(Tensor::negate, 1);
    matrix.set(Tensor::negate, Tensor.ALL, 3);
    SymmetricMatrixQ.require(matrix);
  }

  public void testGet1Fail() {
    assertEquals(Pi.VALUE.get(), Pi.VALUE);
    AssertFail.of(() -> Pi.VALUE.get(Arrays.asList(0)));
    AssertFail.of(() -> Pi.VALUE.get(Arrays.asList(0, 0)));
    AssertFail.of(() -> Pi.VALUE.get(Arrays.asList(-1)));
    AssertFail.of(() -> Pi.VALUE.get(Arrays.asList(-1, 0)));
    AssertFail.of(() -> RealScalar.ONE.Get(1));
    AssertFail.of(() -> RealScalar.ONE.get(new Integer[] { 1 }));
  }

  public void testGet2Fail() {
    AssertFail.of(() -> RationalScalar.HALF.Get(1, 4));
    AssertFail.of(() -> Pi.TWO.get(new Integer[] { 1, 2 }));
  }

  public void testSetFail() {
    AssertFail.of(() -> RealScalar.ONE.set(RealScalar.ZERO));
    AssertFail.of(() -> RealScalar.ONE.set(s -> RealScalar.ZERO));
  }

  public void testAppendFail() {
    AssertFail.of(() -> RealScalar.ONE.append(RealScalar.ZERO));
  }

  public void testExtractFail() {
    AssertFail.of(() -> RealScalar.ONE.extract(1, 2));
  }

  public void testBlockFail() {
    AssertFail.of(() -> RealScalar.ONE.block(Arrays.asList(1), Arrays.asList(1)));
  }
}
