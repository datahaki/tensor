// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.NavigableMap;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.red.Total;

class NdTreeMapTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    CoordinateBoundingBox box = CoordinateBounds.of( //
        Tensors.fromString("{1[m], 2[m], 3[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    NdMap<Void> ndMap = NdTreeMap.of(box);
    for (int count = 0; count < 50; ++count)
      ndMap.insert(TestHelper.sample(box), null);
    ndMap = Serialization.copy(ndMap);
    Tensor center = Tensors.fromString("{3/2[m], 5/2[m], 4[m]}");
    NdCenterInterface ndCenterInterface = NdCenters.VECTOR_2_NORM.apply(center);
    {
      Collection<NdMatch<Void>> collection = NdCollectRadius.of(ndMap, ndCenterInterface, Quantity.of(RealScalar.of(2), "m"));
      assertFalse(collection.isEmpty());
    }
    {
      Collection<NdMatch<Void>> collection = NdCollectNearest.of(ndMap, ndCenterInterface, 4);
      assertEquals(collection.size(), 4);
    }
  }

  @Test
  void testBinSize() {
    NdMap<String> ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(1, 1)));
    Distribution distribution = BernoulliDistribution.of(.3);
    for (int count = 0; count < 50; ++count)
      ndMap.insert(RandomVariate.of(distribution, 2), "p" + count);
    ndMap.toString();
  }

  @Test
  void testParallel() {
    NdMap<Void> ndTreeMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(1, 1)), 2);
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 1000; ++count)
      ndTreeMap.insert(RandomVariate.of(distribution, 2), null);
    for (int count = 0; count < 20; ++count) {
      new Thread(() -> {
        Tensor center = RandomVariate.of(distribution, 2);
        NdCenterInterface distancer = NdCenters.VECTOR_2_NORM.apply(center);
        Collection<NdMatch<Void>> cluster = NdCollectNearest.of(ndTreeMap, distancer, 100);
        assertEquals(cluster.size(), 100);
      }).start();
    }
  }

  @Test
  void testEmpty() {
    NdMap<String> ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 10);
    assertTrue(ndMap.isEmpty());
    NdCenterInterface distancer = NdCenters.VECTOR_2_NORM.apply(Tensors.vector(0, 0));
    Collection<NdMatch<String>> cluster = NdCollectNearest.of(ndMap, distancer, 2);
    assertEquals(cluster.size(), 0);
  }

  @Test
  void testClear() throws Exception {
    NdMap<String> ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 10);
    ndMap.insert(Tensors.vector(1, 1), "d1");
    ndMap.insert(Tensors.vector(1, 0), "d2");
    ndMap.insert(Tensors.vector(0, 1), "d3");
    NdCenterInterface ndCenter = NdCenters.VECTOR_2_NORM.apply(Tensors.vector(0, 0));
    {
      Collection<NdMatch<String>> cluster = NdCollectNearest.of(ndMap, ndCenter, 5);
      assertEquals(cluster.size(), 3);
    }
    NdMap<String> ndMap2 = Serialization.copy(ndMap);
    {
      ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 10);
      Collection<NdMatch<String>> cluster = NdCollectNearest.of(ndMap, ndCenter, 5);
      assertEquals(cluster.size(), 0);
    }
    ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 10);
    {
      Collection<NdMatch<String>> cluster = NdCollectNearest.of(ndMap2, ndCenter, 5);
      assertEquals(cluster.size(), 3);
    }
  }

  @Test
  void testCornerCase() {
    NdMap<String> ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 3);
    Tensor location = Array.zeros(2);
    for (int c = 0; c < 100; ++c)
      ndMap.insert(location, "s" + c);
  }

  @Test
  void testSimple1() {
    final int n = 10;
    NdMap<String> ndTreeMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(1, 1)), n);
    // ndTreeMap.binSize();
    for (int c = 0; c < 800; ++c)
      ndTreeMap.insert(RandomVariate.of(UniformDistribution.unit(), 2), "s" + c);
    NdBinsize<String> ndBinsize = new NdBinsize<>();
    ndTreeMap.visit(ndBinsize);
    Tensor flatten = Flatten.of(ndBinsize.bins());
    assertEquals(Total.of(flatten), RealScalar.of(800));
    NavigableMap<Tensor, Long> map = Tally.sorted(flatten);
    map.lastKey();
    // assertEquals(last, RealScalar.of(n));
  }

  @Test
  void testMixedUnits() {
    CoordinateBoundingBox box = CoordinateBounds.of( //
        Tensors.fromString("{1[m], 2[s], 3[A]}"), //
        Tensors.fromString("{2[m], 3[s], 4[A]}"));
    NdMap<String> ndMap = NdTreeMap.of(box);
    for (int c = 0; c < 100; ++c) {
      Tensor tensor = TestHelper.sample(box);
      ndMap.insert(tensor, "" + c);
    }
    Tensor center = TestHelper.sample(box);
    NdCenterBase ndCenterBase = new NdCenterBase(center) {
      @Override
      public Scalar distance(Tensor point) {
        Tensor tensor = Tensor.of(center.subtract(point).stream().map(Scalar.class::cast).map(Unprotect::withoutUnit));
        return VectorInfinityNorm.of(tensor);
      }
    };
    {
      Collection<NdMatch<String>> collection = NdCollectNearest.of(ndMap, ndCenterBase, 3);
      assertEquals(collection.size(), 3);
    }
    {
      Collection<NdMatch<String>> collection = NdCollectRadius.of(ndMap, ndCenterBase, RealScalar.of(1));
      collection.isEmpty();
    }
  }

  @Test
  void testPrint() {
    NdMap<String> ndTreeMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(1, 1)), 3);
    for (int c = 0; c < 12; ++c) {
      Tensor location = RandomVariate.of(UniformDistribution.unit(), 2);
      ndTreeMap.insert(location, "s" + c);
    }
  }

  @Test
  void testLeafSizeFail() {
    CoordinateBoundingBox box = CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(1, 1));
    assertThrows(IllegalArgumentException.class, () -> NdTreeMap.of(box, -1));
    assertThrows(IllegalArgumentException.class, () -> NdTreeMap.of(box, +0));
  }

  @Test
  void testFail0() {
    assertThrows(NullPointerException.class, () -> NdTreeMap.of(null));
    assertThrows(NullPointerException.class, () -> NdTreeMap.of(null, 2));
  }

  @Test
  void testFail1() {
    NdMap<String> ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 2);
    Tensor location = Array.zeros(3);
    assertThrows(IllegalArgumentException.class, () -> ndMap.insert(location, "string"));
  }
}
