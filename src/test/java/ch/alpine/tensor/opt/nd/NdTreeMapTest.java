// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.IOException;
import java.util.Collection;
import java.util.NavigableMap;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.BernoulliDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NdTreeMapTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    NdBox ndBox = NdBox.of( //
        Tensors.fromString("{1[m], 2[m], 3[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    NdMap<Void> ndMap = NdTreeMap.of(ndBox);
    for (int count = 0; count < 50; ++count)
      ndMap.add(TestHelper.sample(ndBox), null);
    ndMap = Serialization.copy(ndMap);
    Tensor center = Tensors.fromString("{3/2[m], 5/2[m], 4[m]}");
    NdCenterInterface ndCenterInterface = NdCenterBase.of2Norm(center);
    {
      Collection<NdMatch<Void>> collection = NdClusterRadius.of(ndMap, ndCenterInterface, Quantity.of(RealScalar.of(2), "m"));
      assertFalse(collection.isEmpty());
    }
    {
      Collection<NdMatch<Void>> collection = NdClusterNearest.of(ndMap, ndCenterInterface, 4);
      assertEquals(collection.size(), 4);
    }
  }

  public void testBinSize() {
    NdMap<String> ndMap = NdTreeMap.of(NdBox.of(Tensors.vector(0, 0), Tensors.vector(1, 1)));
    Distribution distribution = BernoulliDistribution.of(.3);
    for (int count = 0; count < 50; ++count)
      ndMap.add(RandomVariate.of(distribution, 2), "p" + count);
    ndMap.toString();
  }

  public void testParallel() {
    NdMap<Void> ndTreeMap = NdTreeMap.of(NdBox.of(Tensors.vector(0, 0), Tensors.vector(1, 1)), 2);
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 1000; ++count)
      ndTreeMap.add(RandomVariate.of(distribution, 2), null);
    for (int count = 0; count < 20; ++count) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          Tensor center = RandomVariate.of(distribution, 2);
          NdCenterInterface distancer = NdCenterBase.of2Norm(center);
          Collection<NdMatch<Void>> cluster = NdClusterNearest.of(ndTreeMap, distancer, 100);
          assertEquals(cluster.size(), 100);
        }
      }).start();
    }
  }

  public void testEmpty() throws Exception {
    NdMap<String> ndMap = NdTreeMap.of(NdBox.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 10);
    assertTrue(ndMap.isEmpty());
    NdCenterInterface distancer = NdCenterBase.of2Norm(Tensors.vector(0, 0));
    Collection<NdMatch<String>> cluster = NdClusterNearest.of(ndMap, distancer, 2);
    assertEquals(cluster.size(), 0);
  }

  public void testClear() throws Exception {
    NdMap<String> ndMap = NdTreeMap.of(NdBox.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 10);
    ndMap.add(Tensors.vector(1, 1), "d1");
    ndMap.add(Tensors.vector(1, 0), "d2");
    ndMap.add(Tensors.vector(0, 1), "d3");
    NdCenterInterface ndCenter = NdCenterBase.of2Norm(Tensors.vector(0, 0));
    {
      Collection<NdMatch<String>> cluster = NdClusterNearest.of(ndMap, ndCenter, 5);
      assertEquals(cluster.size(), 3);
    }
    NdMap<String> ndMap2 = Serialization.copy(ndMap);
    {
      ndMap = NdTreeMap.of(NdBox.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 10);
      Collection<NdMatch<String>> cluster = NdClusterNearest.of(ndMap, ndCenter, 5);
      assertEquals(cluster.size(), 0);
    }
    ndMap = NdTreeMap.of(NdBox.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 10);
    {
      Collection<NdMatch<String>> cluster = NdClusterNearest.of(ndMap2, ndCenter, 5);
      assertEquals(cluster.size(), 3);
    }
  }

  public void testCornerCase() {
    NdMap<String> ndMap = NdTreeMap.of(NdBox.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 3);
    Tensor location = Array.zeros(2);
    for (int c = 0; c < 100; ++c)
      ndMap.add(location, "s" + c);
  }

  public void testSimple1() {
    final int n = 10;
    NdMap<String> ndTreeMap = NdTreeMap.of(NdBox.of(Tensors.vector(0, 0), Tensors.vector(1, 1)), n);
    // ndTreeMap.binSize();
    for (int c = 0; c < 800; ++c)
      ndTreeMap.add(RandomVariate.of(UniformDistribution.unit(), 2), "s" + c);
    NdBinsize<String> ndBinsize = new NdBinsize<>();
    ndTreeMap.visit(ndBinsize);
    Tensor flatten = Flatten.of(ndBinsize.bins());
    assertEquals(Total.of(flatten), RealScalar.of(800));
    NavigableMap<Tensor, Long> map = Tally.sorted(flatten);
    map.lastKey();
    // assertEquals(last, RealScalar.of(n));
  }

  public void testPrint() {
    NdMap<String> ndTreeMap = NdTreeMap.of(NdBox.of(Tensors.vector(0, 0), Tensors.vector(1, 1)), 3);
    for (int c = 0; c < 12; ++c) {
      Tensor location = RandomVariate.of(UniformDistribution.unit(), 2);
      ndTreeMap.add(location, "s" + c);
    }
  }

  public void testLeafSizeFail() {
    NdBox ndBox = NdBox.of(Tensors.vector(0, 0), Tensors.vector(1, 1));
    AssertFail.of(() -> NdTreeMap.of(ndBox, -1));
    AssertFail.of(() -> NdTreeMap.of(ndBox, +0));
  }

  public void testFail0() {
    AssertFail.of(() -> NdTreeMap.of(null));
    AssertFail.of(() -> NdTreeMap.of(null, 2));
  }

  public void testFail1() {
    NdMap<String> ndMap = NdTreeMap.of(NdBox.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 2);
    Tensor location = Array.zeros(3);
    AssertFail.of(() -> ndMap.add(location, "string"));
  }
}
