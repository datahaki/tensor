// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

public class NdCollectNearestTest {
  @Test
  public void testSimple() {
    CoordinateBoundingBox box = CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9));
    for (int n = 1; n < 10; ++n) {
      NdMap<String> ndTreeMap = NdTreeMap.of(box, n);
      ndTreeMap.toString();
      assertTrue(ndTreeMap.isEmpty());
      ndTreeMap.insert(Tensors.vector(1, 1), "d1");
      assertFalse(ndTreeMap.isEmpty());
      ndTreeMap.insert(Tensors.vector(1, 0), "d2");
      ndTreeMap.insert(Tensors.vector(0, 1), "d3");
      ndTreeMap.insert(Tensors.vector(1, 1), "d4");
      ndTreeMap.insert(Tensors.vector(0.1, 0.1), "d5");
      ndTreeMap.insert(Tensors.vector(6, 7), "d6");
      ndTreeMap.toString();
      {
        Tensor center = Tensors.vector(0, 0);
        NdCenterInterface distancer = NdCenters.VECTOR_2_NORM.apply(center);
        Collection<NdMatch<String>> cluster = NdCollectNearest.of(ndTreeMap, distancer, 1);
        assertTrue(cluster.iterator().next().value().equals("d5"));
      }
      {
        Tensor center = Tensors.vector(5, 5);
        NdCenterInterface distancer = NdCenters.VECTOR_2_NORM.apply(center);
        Collection<NdMatch<String>> cluster = NdCollectNearest.of(ndTreeMap, distancer, 1);
        assertTrue(cluster.iterator().next().value().equals("d6"));
      }
      {
        Tensor center = Tensors.vector(1.1, 0.9);
        NdCenterInterface distancer = NdCenters.VECTOR_2_NORM.apply(center);
        Collection<NdMatch<String>> cluster = NdCollectNearest.of(ndTreeMap, distancer, 2);
        assertEquals(cluster.size(), 2);
        List<String> list = Arrays.asList("d1", "d4");
        for (NdMatch<String> point : cluster)
          assertTrue(list.contains(point.value()));
      }
    }
  }

  @Test
  public void testClusterNearest() {
    int maxDensity = 2;
    NdMap<String> m1 = new NdListMap<>();
    NdMap<String> m2 = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -1), Tensors.vector(2, 10)), maxDensity);
    int index = 0;
    Distribution ux = UniformDistribution.of(-2, 2);
    Distribution uy = UniformDistribution.of(-1, 10);
    for (int c = 0; c < 20; ++c) {
      Tensor location = Tensors.of(RandomVariate.of(ux), RandomVariate.of(uy));
      String value = "p" + (++index);
      m1.insert(location, value);
      m2.insert(location, value);
    }
    assertEquals(m1.size(), m2.size());
    for (NdCenters ndCenters : NdCenters.values()) {
      NdCenterInterface ndCenterInterface = ndCenters.apply(Tensors.vector(0.2, 4.3));
      int limit = 6;
      Collection<NdMatch<String>> c1 = NdCollectNearest.of(m1, ndCenterInterface, limit);
      Collection<NdMatch<String>> c2 = NdCollectNearest.of(m2, ndCenterInterface, limit);
      assertTrue(0 < c1.size());
      assertTrue(0 < c2.size());
      assertEquals(c1.size(), c2.size());
      assertEquals(c1.size(), limit);
      Set<String> s1 = c1.stream().map(NdMatch::value).collect(Collectors.toSet());
      Set<String> s2 = c2.stream().map(NdMatch::value).collect(Collectors.toSet());
      assertEquals(s1, s2);
    }
  }

  @Test
  public void testEmpty() {
    NdMap<Void> ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(0), Tensors.vector(1)));
    for (NdCenters ndCenters : NdCenters.values()) {
      NdCenterInterface ndCenterInterface = ndCenters.apply(Tensors.vector(0.2));
      NdMatch<Void> ndMatch = NdCollectNearest.of(ndMap, ndCenterInterface);
      assertTrue(Objects.isNull(ndMatch));
    }
  }

  @Test
  public void testProtected() {
    for (NdCenters ndCenters : NdCenters.values()) {
      NdCenterInterface ndCenterInterface = ndCenters.apply(Array.zeros(2));
      assertTrue(new NdCollectNearest<>(ndCenterInterface, 1).queue().isEmpty());
    }
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> new NdCollectNearest<>(null, 1));
  }

  @Test
  public void testNonPositiveFail() {
    for (NdCenters ndCenters : NdCenters.values()) {
      NdCenterInterface ndCenterInterface = ndCenters.apply(Array.zeros(2));
      assertThrows(IllegalArgumentException.class, () -> new NdCollectNearest<>(ndCenterInterface, 0));
    }
  }
}
