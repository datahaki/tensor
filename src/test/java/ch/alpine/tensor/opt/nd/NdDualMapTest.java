// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.BernoulliDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class NdDualMapTest extends TestCase {
  public void testSimple() {
    NdMap<String> m1 = new NdDualMap<>(Tensors.vector(-1, -2), Tensors.vector(2, 10), 3, 10);
    m1.add(Tensors.vector(1, 0), "p2");
    m1.add(Tensors.vector(1, 5), "p4");
    m1.add(Tensors.vector(0, 0), "p1");
    m1.add(Tensors.vector(1, 1), "p3");
    Tensor center = Tensors.vector(0, 0);
    Collection<NdMatch<String>> cl = m1.cluster(EuclideanNdCenter.of(center), 2);
    Set<String> res = cl.stream().map(NdMatch::value).collect(Collectors.toSet());
    assertTrue(res.contains("p1"));
    assertTrue(res.contains("p2"));
    assertEquals(res.size(), 2);
  }

  private static void _checkCenter(Tensor center, int n, int dim, int dep) {
    NdMap<String> m2 = new NdDualMap<>(Tensors.vector(-2, -1, 0), Tensors.vector(2, 10, 3), dim, dep);
    int index = 0;
    Distribution b = BernoulliDistribution.of(RealScalar.of(0.25));
    Distribution ux = UniformDistribution.of(-2, 2);
    Distribution uy = UniformDistribution.of(-1, 10);
    Distribution uz = UniformDistribution.of(0, 3);
    for (int c = 0; c < 20; ++c) {
      Tensor location = Tensors.of(RandomVariate.of(ux), RandomVariate.of(uy), RandomVariate.of(uz));
      String value = "p" + (++index);
      m2.add(location, value);
      while (Scalars.isZero(RandomVariate.of(b))) {
        value = "p" + (++index);
        m2.add(location, value);
      }
    }
    NdCenterInterface dinf = EuclideanNdCenter.of(center);
    Collection<NdMatch<String>> c2 = m2.cluster(dinf, n);
    // assertTrue(0 < c2.considered());
    assertTrue(c2.size() <= n);
  }

  public void testOne() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(0.3, 0.3, 1.5), 1, dim, 6);
      _checkCenter(Tensors.vector(0.1, 0.3, 1), 1, dim, 6);
      _checkCenter(Tensors.vector(5, 4.3, 1.5), 1, dim, 10);
      _checkCenter(Tensors.vector(5, -3.3, 2.5), 1, dim, 10);
    }
  }

  public void testFew() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(0.3, 0.3, 1.5), 3, dim, 7);
      _checkCenter(Tensors.vector(0.1, 0.3, 1), 3, dim, 7);
      _checkCenter(Tensors.vector(5, 4.3, 1.5), 3, dim, 11);
      _checkCenter(Tensors.vector(5, -3.3, 2.5), 3, dim, 11);
    }
  }

  public void testMany() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(0.3, 0.3, 1.5), 20, dim, 8);
      _checkCenter(Tensors.vector(0.1, 0.3, 1), 20, dim, 8);
      _checkCenter(Tensors.vector(5, 4.3, 1.5), 20, dim, 12);
      _checkCenter(Tensors.vector(5, -3.3, 2.5), 20, dim, 12);
    }
  }

  public void testMost() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(0.3, 0.3, 1.5), 60, dim, 9);
      _checkCenter(Tensors.vector(0.1, 0.3, 1), 60, dim, 9);
      _checkCenter(Tensors.vector(5, 4.3, 1.5), 60, dim, 13);
      _checkCenter(Tensors.vector(5, -3.3, 2.5), 60, dim, 13);
    }
  }

  public void testAll() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(0.3, 0.3, 1.5), 160, dim, 10);
      _checkCenter(Tensors.vector(0.1, 0.3, 1), 160, dim, 10);
      _checkCenter(Tensors.vector(5, 4.3, 1.5), 160, dim, 14);
      _checkCenter(Tensors.vector(5, -3.3, 2.5), 160, dim, 14);
    }
  }
}
