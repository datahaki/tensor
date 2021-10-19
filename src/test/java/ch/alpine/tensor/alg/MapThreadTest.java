// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MapThreadTest extends TestCase {
  public void testEmptyPositive() {
    assertEquals(MapThread.of(l -> l.get(0), Collections.emptyList(), 1), Tensors.empty());
    assertEquals(MapThread.of(l -> l.get(0), Collections.emptyList(), 2), Tensors.empty());
    AssertFail.of(() -> MapThread.of(l -> l.get(0), Collections.emptyList(), 0));
  }

  public void testEmptyZero() {
    Tensor result = MapThread.of(l -> ComplexScalar.I, Collections.emptyList(), 0);
    assertEquals(ComplexScalar.I, result);
  }

  public void testNegFail() {
    AssertFail.of(() -> MapThread.of(l -> ComplexScalar.I, Collections.emptyList(), -1));
  }

  public void testFail() {
    List<Tensor> list = Arrays.asList(HilbertMatrix.of(2, 3), HilbertMatrix.of(3, 3));
    MapThread.of(l -> ComplexScalar.I, list, 0);
    AssertFail.of(() -> MapThread.of(l -> ComplexScalar.I, list, 1));
  }
}
