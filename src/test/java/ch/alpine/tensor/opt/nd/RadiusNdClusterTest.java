// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.BernoulliDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class RadiusNdClusterTest extends TestCase {
  public void testSimple() {
    int dim = 2;
    int dep = 3;
    NdMap<String> m1 = new NdListMap<>();
    NdMap<String> m2 = new NdTreeMap<>(Tensors.vector(-2, -1), Tensors.vector(2, 10), dim, dep);
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
    NdCenterInterface ndCenterInterface = EuclideanNdCenter.of(Tensors.vector(0.2, 4.3));
    Scalar radius = RealScalar.of(4);
    Collection<NdMatch<String>> c1 = SphericalNdCluster.of(m1, ndCenterInterface, radius);
    // TODO
    // Collection<NdMatch<String>> c2 = m2.cluster(SphericalNdCluster.create(ndCenterInterface, radius));
    // assertEquals(c1.size(), c2.size());
    // System.out.println(c2.size());
    // assertTrue(c1.size() <= n);
    // Scalar s1 = addDistances(c1, center, dinf);
    // Scalar s2 = addDistances(c2, center, dinf);
    // Chop._10.requireClose(s1, s2);
  }
}
