// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Tally;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NdTreeMapTest extends TestCase {
  public void testSome() {
    for (int n = 0; n < 10; ++n)
      for (int d = 1; d < 4; ++d) {
        NdTreeMap<String> ndTreeMap = //
            new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), n, d);
        ndTreeMap.toString();
        assertTrue(ndTreeMap.isEmpty());
        ndTreeMap.binSize();
        ndTreeMap.add(Tensors.vector(1, 1), "d1");
        assertFalse(ndTreeMap.isEmpty());
        ndTreeMap.add(Tensors.vector(1, 0), "d2");
        ndTreeMap.add(Tensors.vector(0, 1), "d3");
        ndTreeMap.binSize();
        ndTreeMap.add(Tensors.vector(1, 1), "d4");
        ndTreeMap.add(Tensors.vector(0.1, 0.1), "d5");
        ndTreeMap.add(Tensors.vector(6, 7), "d6");
        ndTreeMap.toString();
        ndTreeMap.binSize();
        {
          Tensor center = Tensors.vector(0, 0);
          NdCenterInterface distancer = EuclideanNdCenter.of(center);
          Collection<NdMatch<String>> cluster = ndTreeMap.cluster(distancer, 1);
          assertTrue(cluster.iterator().next().value().equals("d5"));
        }
        {
          Tensor center = Tensors.vector(5, 5);
          NdCenterInterface distancer = EuclideanNdCenter.of(center);
          Collection<NdMatch<String>> cluster = ndTreeMap.cluster(distancer, 1);
          assertTrue(cluster.iterator().next().value().equals("d6"));
        }
        {
          Tensor center = Tensors.vector(1.1, 0.9);
          NdCenterInterface distancer = EuclideanNdCenter.of(center);
          Collection<NdMatch<String>> cluster = ndTreeMap.cluster(distancer, 2);
          assertEquals(cluster.size(), 2);
          List<String> list = Arrays.asList("d1", "d4");
          for (NdMatch<String> point : cluster)
            assertTrue(list.contains(point.value()));
        }
      }
  }

  public void testBinSize() {
    NdTreeMap<Void> ndTreeMap = new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(1, 1), 2, 10);
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 50; ++count) {
      ndTreeMap.add(RandomVariate.of(distribution, 2), null);
      ndTreeMap.binSize();
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
          Collection<NdMatch<Void>> cluster = ndTreeMap.cluster(distancer, 100);
          assertEquals(cluster.size(), 100);
        }
      }).start();
    }
  }

  public void testEmpty() throws Exception {
    NdMap<String> ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
    assertTrue(ndMap.isEmpty());
    NdCenterInterface distancer = EuclideanNdCenter.of(Tensors.vector(0, 0));
    Collection<NdMatch<String>> cluster = ndMap.cluster(distancer, 2);
    assertEquals(cluster.size(), 0);
  }

  public void testClear() throws Exception {
    NdMap<String> ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
    ndMap.add(Tensors.vector(1, 1), "d1");
    ndMap.add(Tensors.vector(1, 0), "d2");
    ndMap.add(Tensors.vector(0, 1), "d3");
    NdCenterInterface ndCenter = EuclideanNdCenter.of(Tensors.vector(0, 0));
    {
      Collection<NdMatch<String>> cluster = ndMap.cluster(ndCenter, 5);
      assertEquals(cluster.size(), 3);
    }
    NdMap<String> ndMap2 = Serialization.copy(ndMap);
    {
      ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
      Collection<NdMatch<String>> cluster = ndMap.cluster(ndCenter, 5);
      assertEquals(cluster.size(), 0);
    }
    ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
    {
      Collection<NdMatch<String>> cluster = ndMap2.cluster(ndCenter, 5);
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
    ndTreeMap.binSize();
    for (int c = 0; c < 800; ++c)
      ndTreeMap.add(RandomVariate.of(UniformDistribution.unit(), 2), "s" + c);
    Tensor flatten = Flatten.of(ndTreeMap.binSize());
    assertEquals(Total.of(flatten), RealScalar.of(800));
    NavigableMap<Tensor, Long> map = Tally.sorted(flatten);
    Tensor last = map.lastKey();
    assertEquals(last, RealScalar.of(n));
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
