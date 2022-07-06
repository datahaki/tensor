// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;

class NdCollectRadiusTest {
  @Test
  void testClusterRadius() {
    int maxDensity = 2;
    NdMap<String> m1 = new NdListMap<>();
    NdMap<String> m2 = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -1), Tensors.vector(2, 10)), maxDensity);
    int index = 0;
    Distribution b = BernoulliDistribution.of(RealScalar.of(0.25));
    Distribution ux = UniformDistribution.of(-2, 2);
    Distribution uy = UniformDistribution.of(-1, 10);
    for (int c = 0; c < 20; ++c) {
      Tensor location = Tensors.of(RandomVariate.of(ux), RandomVariate.of(uy));
      String value = "p" + (++index);
      m1.insert(location, value);
      m2.insert(location, value);
      while (Scalars.isZero(RandomVariate.of(b))) {
        value = "p" + (++index);
        m1.insert(location, value);
        m2.insert(location, value);
      }
    }
    for (NdCenters ndCenters : NdCenters.values()) {
      assertEquals(m1.size(), m2.size());
      NdCenterInterface ndCenterInterface = ndCenters.apply(Tensors.vector(0.2, 4.3));
      {
        Scalar radius = RealScalar.of(4);
        Collection<NdMatch<String>> c1 = NdCollectRadius.of(m1, ndCenterInterface, radius);
        Collection<NdMatch<String>> c2 = NdCollectRadius.of(m2, ndCenterInterface, radius);
        assertTrue(0 < c1.size());
        assertTrue(0 < c2.size());
        assertEquals(c1.size(), c2.size());
        Set<String> s1 = c1.stream().map(NdMatch::value).collect(Collectors.toSet());
        Set<String> s2 = c2.stream().map(NdMatch::value).collect(Collectors.toSet());
        assertEquals(s1, s2);
      }
      assertTrue(NdInsideRadius.anyMatch(m2, ndCenters.apply(Tensors.vector(0, 0)), RealScalar.of(10)));
      assertFalse(NdInsideRadius.anyMatch(m2, ndCenters.apply(Tensors.vector(3, 0)), RealScalar.of(0.2)));
    }
  }

  @ParameterizedTest
  @EnumSource(NdCenters.class)
  void testEmpty(NdCenters ndCenters) {
    NdCenterInterface ndCenterInterface = ndCenters.apply(Tensors.vector(0.2, 4.3));
    Scalar radius = RealScalar.of(4);
    NdCollectRadius<Object> ndCollectRadius = new NdCollectRadius<>(ndCenterInterface, radius);
    assertTrue(ndCollectRadius.list().isEmpty());
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> new NdCollectRadius<>(null, Pi.VALUE));
  }

  @Test
  void testNonPositiveFail() {
    NdCenterInterface ndCenterInterface = NdCenters.VECTOR_INFINITY_NORM.apply(Array.zeros(2));
    assertThrows(Throw.class, () -> new NdCollectRadius<>(ndCenterInterface, RealScalar.ONE.negate()));
  }
}
