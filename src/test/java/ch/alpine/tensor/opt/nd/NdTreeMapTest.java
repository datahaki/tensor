// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.NavigableMap;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NdTreeMapTest extends TestCase {
  public void testSimple() {
    NdTreeMap<Void> ndTreeMap = new NdTreeMap<>( //
        Tensors.fromString("{1[m], 2[m], 3[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"), //
        2, 10);
    Distribution distribution = UniformDistribution.of(Quantity.of(1, "m"), Quantity.of(4, "m"));
    for (int count = 0; count < 50; ++count)
      ndTreeMap.add(RandomVariate.of(distribution, 3), null);
    Tensor center = Tensors.fromString("{3/2[m], 5/2[m], 4[m]}");
    NdCenterInterface ndCenterInterface = EuclideanNdCenter.of(center);
    {
      Collection<NdMatch<Void>> collection = SphericalNdCluster.of(ndTreeMap, ndCenterInterface, Quantity.of(RealScalar.of(2), "m"));
      assertFalse(collection.isEmpty());
    }
    {
      Collection<NdMatch<Void>> collection = NearestNdCluster.of(ndTreeMap, ndCenterInterface, 4);
      assertEquals(collection.size(), 4);
    }
  }

  public void testBinSize() {
    NdTreeMap<Void> ndTreeMap = new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(1, 1), 2, 10);
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 50; ++count) {
      ndTreeMap.add(RandomVariate.of(distribution, 2), null);
      // ndTreeMap.binSize();
    }
  }

  public void testParallel() {
    NdTreeMap<Void> ndTreeMap = new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(1, 1), 2, 6);
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 1000; ++count)
      ndTreeMap.add(RandomVariate.of(distribution, 2), null);
    for (int count = 0; count < 20; ++count) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          Tensor center = RandomVariate.of(distribution, 2);
          NdCenterInterface distancer = EuclideanNdCenter.of(center);
          Collection<NdMatch<Void>> cluster = NearestNdCluster.of(ndTreeMap, distancer, 100);
          assertEquals(cluster.size(), 100);
        }
      }).start();
    }
  }

  public void testEmpty() throws Exception {
    NdMap<String> ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
    assertTrue(ndMap.isEmpty());
    NdCenterInterface distancer = EuclideanNdCenter.of(Tensors.vector(0, 0));
    Collection<NdMatch<String>> cluster = NearestNdCluster.of(ndMap, distancer, 2);
    assertEquals(cluster.size(), 0);
  }

  public void testClear() throws Exception {
    NdMap<String> ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
    ndMap.add(Tensors.vector(1, 1), "d1");
    ndMap.add(Tensors.vector(1, 0), "d2");
    ndMap.add(Tensors.vector(0, 1), "d3");
    NdCenterInterface ndCenter = EuclideanNdCenter.of(Tensors.vector(0, 0));
    {
      Collection<NdMatch<String>> cluster = NearestNdCluster.of(ndMap, ndCenter, 5);
      assertEquals(cluster.size(), 3);
    }
    NdMap<String> ndMap2 = Serialization.copy(ndMap);
    {
      ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
      Collection<NdMatch<String>> cluster = NearestNdCluster.of(ndMap, ndCenter, 5);
      assertEquals(cluster.size(), 0);
    }
    ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
    {
      Collection<NdMatch<String>> cluster = NearestNdCluster.of(ndMap2, ndCenter, 5);
      assertEquals(cluster.size(), 3);
    }
  }

  public void testCornerCase() {
    NdMap<String> ndTreeMap = //
        new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 3, 2);
    Tensor location = Array.zeros(2);
    for (int c = 0; c < 100; ++c)
      ndTreeMap.add(location, "s" + c);
  }

  public void testSimple1() {
    final int n = 10;
    NdTreeMap<String> ndTreeMap = //
        new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(1, 1), n, 26);
    // ndTreeMap.binSize();
    for (int c = 0; c < 800; ++c)
      ndTreeMap.add(RandomVariate.of(UniformDistribution.unit(), 2), "s" + c);
    NdBinsize<String> ndBinsize = new NdBinsize<>();
    ndTreeMap.visit(ndBinsize);
    Tensor flatten = Flatten.of(ndBinsize.bins());
    assertEquals(Total.of(flatten), RealScalar.of(800));
    NavigableMap<Tensor, Long> map = Tally.sorted(flatten);
    Tensor last = map.lastKey();
    // assertEquals(last, RealScalar.of(n));
  }

  public void testPrint() {
    NdMap<String> ndTreeMap = //
        new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(1, 1), 3, 3);
    for (int c = 0; c < 12; ++c) {
      Tensor location = RandomVariate.of(UniformDistribution.unit(), 2);
      ndTreeMap.add(location, "s" + c);
    }
  }

  public void testDensityFail() {
    AssertFail.of(() -> new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(1, 1), -1, 26));
  }

  public void testFail0() {
    AssertFail.of(() -> new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9, 3), 2, 2));
  }

  public void testFail1() {
    NdMap<String> ndTreeMap = new NdTreeMap<>( //
        Tensors.vector(-2, -3), Tensors.vector(8, 9), 2, 2);
    Tensor location = Array.zeros(3);
    AssertFail.of(() -> ndTreeMap.add(location, "string"));
  }

  public void testFail2() {
    AssertFail.of(() -> new NdTreeMap<>(Tensors.vector(-2, 10), Tensors.vector(8, 9), 10, 10));
  }
}
