// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NdClusterNearestTest extends TestCase {
  public void testSimple() {
    NdBox ndBounds = NdBox.of(Tensors.vector(-2, -3), Tensors.vector(8, 9));
    for (int n = 1; n < 10; ++n) {
      NdMap<String> ndTreeMap = NdTreeMap.of(ndBounds, n);
      ndTreeMap.toString();
      assertTrue(ndTreeMap.isEmpty());
      ndTreeMap.add(Tensors.vector(1, 1), "d1");
      assertFalse(ndTreeMap.isEmpty());
      ndTreeMap.add(Tensors.vector(1, 0), "d2");
      ndTreeMap.add(Tensors.vector(0, 1), "d3");
      ndTreeMap.add(Tensors.vector(1, 1), "d4");
      ndTreeMap.add(Tensors.vector(0.1, 0.1), "d5");
      ndTreeMap.add(Tensors.vector(6, 7), "d6");
      ndTreeMap.toString();
      {
        Tensor center = Tensors.vector(0, 0);
        NdCenterInterface distancer = NdCenterBase.of2Norm(center);
        Collection<NdMatch<String>> cluster = NdClusterNearest.of(ndTreeMap, distancer, 1);
        assertTrue(cluster.iterator().next().value().equals("d5"));
      }
      {
        Tensor center = Tensors.vector(5, 5);
        NdCenterInterface distancer = NdCenterBase.of2Norm(center);
        Collection<NdMatch<String>> cluster = NdClusterNearest.of(ndTreeMap, distancer, 1);
        assertTrue(cluster.iterator().next().value().equals("d6"));
      }
      {
        Tensor center = Tensors.vector(1.1, 0.9);
        NdCenterInterface distancer = NdCenterBase.of2Norm(center);
        Collection<NdMatch<String>> cluster = NdClusterNearest.of(ndTreeMap, distancer, 2);
        assertEquals(cluster.size(), 2);
        List<String> list = Arrays.asList("d1", "d4");
        for (NdMatch<String> point : cluster)
          assertTrue(list.contains(point.value()));
      }
    }
  }

  public void testClusterNearest() {
    int maxDensity = 2;
    NdMap<String> m1 = new NdListMap<>();
    NdMap<String> m2 = NdTreeMap.of(NdBox.of(Tensors.vector(-2, -1), Tensors.vector(2, 10)), maxDensity);
    int index = 0;
    Distribution ux = UniformDistribution.of(-2, 2);
    Distribution uy = UniformDistribution.of(-1, 10);
    for (int c = 0; c < 20; ++c) {
      Tensor location = Tensors.of(RandomVariate.of(ux), RandomVariate.of(uy));
      String value = "p" + (++index);
      m1.add(location, value);
      m2.add(location, value);
    }
    assertEquals(m1.size(), m2.size());
    NdCenterInterface ndCenterInterface = NdCenterBase.of1Norm(Tensors.vector(0.2, 4.3));
    {
      int limit = 6;
      Collection<NdMatch<String>> c1 = NdClusterNearest.of(m1, ndCenterInterface, limit);
      Collection<NdMatch<String>> c2 = NdClusterNearest.of(m2, ndCenterInterface, limit);
      assertTrue(0 < c1.size());
      assertTrue(0 < c2.size());
      assertEquals(c1.size(), c2.size());
      assertEquals(c1.size(), limit);
      Set<String> s1 = c1.stream().map(NdMatch::value).collect(Collectors.toSet());
      Set<String> s2 = c2.stream().map(NdMatch::value).collect(Collectors.toSet());
      assertEquals(s1, s2);
    }
  }

  public void testProtected() {
    Tensor center = Tensors.vector(0, 0);
    NdCenterInterface ndCenterInterface = NdCenterBase.of2Norm(center);
    assertTrue(new NdClusterNearest<>(ndCenterInterface, 1).queue().isEmpty());
  }

  public void testNullFail() {
    AssertFail.of(() -> new NdClusterNearest<>(null, 1));
  }

  public void testNonPositiveFail() {
    Tensor center = Tensors.vector(0, 0);
    NdCenterInterface ndCenterInterface = NdCenterBase.of2Norm(center);
    AssertFail.of(() -> new NdClusterNearest<>(ndCenterInterface, 0));
  }
}
