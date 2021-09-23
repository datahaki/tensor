// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.BernoulliDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NdClusterRadiusTest extends TestCase {
  public void testSimple() {
    int maxDensity = 2;
    NdMap<String> m1 = new NdListMap<>();
    NdMap<String> m2 = NdTreeMap.of(Tensors.vector(-2, -1), Tensors.vector(2, 10), maxDensity);
    int index = 0;
    Distribution b = BernoulliDistribution.of(RealScalar.of(0.25));
    Distribution ux = UniformDistribution.of(-2, 2);
    Distribution uy = UniformDistribution.of(-1, 10);
    for (int c = 0; c < 20; ++c) {
      Tensor location = Tensors.of(RandomVariate.of(ux), RandomVariate.of(uy));
      String value = "p" + (++index);
      m1.add(location, value);
      m2.add(location, value);
      while (Scalars.isZero(RandomVariate.of(b))) {
        value = "p" + (++index);
        m1.add(location, value);
        m2.add(location, value);
      }
    }
    assertEquals(m1.size(), m2.size());
    NdCenterInterface ndCenterInterface = NdCenterBase.of2Norm(Tensors.vector(0.2, 4.3));
    {
      Scalar radius = RealScalar.of(4);
      Collection<NdMatch<String>> c1 = NdClusterRadius.of(m1, ndCenterInterface, radius);
      Collection<NdMatch<String>> c2 = NdClusterRadius.of(m2, ndCenterInterface, radius);
      assertTrue(0 < c1.size());
      assertTrue(0 < c2.size());
      assertEquals(c1.size(), c2.size());
      Set<String> s1 = c1.stream().map(NdMatch::value).collect(Collectors.toSet());
      Set<String> s2 = c2.stream().map(NdMatch::value).collect(Collectors.toSet());
      assertEquals(s1, s2);
    }
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

  public void testEmpty() {
    NdCenterInterface ndCenterInterface = NdCenterBase.of2Norm(Tensors.vector(0.2, 4.3));
    Scalar radius = RealScalar.of(4);
    NdClusterRadius<Object> sphericalNdCluster = new NdClusterRadius<>(ndCenterInterface, radius);
    assertTrue(sphericalNdCluster.list().isEmpty());
  }

  public void testNullFail() {
    AssertFail.of(() -> new NdClusterRadius<>(null, Pi.VALUE));
  }

  public void testNonPositiveFail() {
    Tensor center = Tensors.vector(0, 0);
    NdCenterInterface ndCenterInterface = NdCenterBase.of2Norm(center);
    AssertFail.of(() -> new NdClusterRadius<>(ndCenterInterface, RealScalar.ONE.negate()));
  }
}
