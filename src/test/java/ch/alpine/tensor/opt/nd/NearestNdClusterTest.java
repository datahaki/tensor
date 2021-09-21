// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NearestNdClusterTest extends TestCase {
  public void testSimple() {
    for (int n = 0; n < 10; ++n) {
      NdMap<String> ndTreeMap = NdTreeMap.of(Tensors.vector(-2, -3), Tensors.vector(8, 9), n);
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
        NdCenterInterface distancer = EuclideanNdCenter.of(center);
        Collection<NdMatch<String>> cluster = NearestNdCluster.of(ndTreeMap, distancer, 1);
        assertTrue(cluster.iterator().next().value().equals("d5"));
      }
      {
        Tensor center = Tensors.vector(5, 5);
        NdCenterInterface distancer = EuclideanNdCenter.of(center);
        Collection<NdMatch<String>> cluster = NearestNdCluster.of(ndTreeMap, distancer, 1);
        assertTrue(cluster.iterator().next().value().equals("d6"));
      }
      {
        Tensor center = Tensors.vector(1.1, 0.9);
        NdCenterInterface distancer = EuclideanNdCenter.of(center);
        Collection<NdMatch<String>> cluster = NearestNdCluster.of(ndTreeMap, distancer, 2);
        assertEquals(cluster.size(), 2);
        List<String> list = Arrays.asList("d1", "d4");
        for (NdMatch<String> point : cluster)
          assertTrue(list.contains(point.value()));
      }
    }
  }

  public void testProtected() {
    Tensor center = Tensors.vector(0, 0);
    NdCenterInterface ndCenterInterface = EuclideanNdCenter.of(center);
    assertTrue(new NearestNdCluster<>(ndCenterInterface, 1).queue().isEmpty());
  }

  public void testNullFail() {
    AssertFail.of(() -> new NearestNdCluster<>(null, 1));
  }

  public void testNonPositiveFail() {
    Tensor center = Tensors.vector(0, 0);
    NdCenterInterface ndCenterInterface = EuclideanNdCenter.of(center);
    AssertFail.of(() -> new NearestNdCluster<>(ndCenterInterface, 0));
  }
}
