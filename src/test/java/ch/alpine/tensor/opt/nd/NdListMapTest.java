// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import ch.alpine.tensor.sca.Chop;

class NdListMapTest {
  @Test
  void testSimple() {
    NdMap<String> m1 = new NdListMap<>();
    m1.insert(Tensors.vector(1, 0), "p2");
    m1.insert(Tensors.vector(1, 5), "p4");
    m1.insert(Tensors.vector(0, 0), "p1");
    m1.insert(Tensors.vector(1, 1), "p3");
    Tensor center = Tensors.vector(0, 0);
    Collection<NdMatch<String>> cl = NdCollectNearest.of(m1, NdCenters.VECTOR_2_NORM.apply(center), 2);
    Set<String> res = cl.stream().map(NdMatch::value).collect(Collectors.toSet());
    assertTrue(res.contains("p1"));
    assertTrue(res.contains("p2"));
    assertEquals(res.size(), 2);
    assertEquals(m1.size(), 4);
    assertFalse(m1.isEmpty());
    m1 = new NdListMap<>();
    assertEquals(m1.size(), 0);
    assertTrue(m1.isEmpty());
  }

  private static Scalar addDistances(Collection<NdMatch<String>> cluster, NdCenterInterface d) {
    Scalar sum = RealScalar.ZERO;
    for (NdMatch<String> entry : cluster) {
      Scalar dist = d.distance(entry.location());
      assertEquals(entry.distance(), dist);
      sum = sum.add(dist);
    }
    return sum;
  }

  private static void _checkCenter(Tensor center, int n, int dim) {
    NdMap<String> m1 = new NdListMap<>();
    NdMap<String> m2 = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -1), Tensors.vector(2, 10)), dim);
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
    assertEquals(m1.size(), m2.size());
    NdCenterInterface dinf = NdCenters.VECTOR_2_NORM.apply(center);
    Collection<NdMatch<String>> c1 = NdCollectNearest.of(m1, dinf, n);
    Collection<NdMatch<String>> c2 = NdCollectNearest.of(m2, dinf, n);
    assertEquals(c1.size(), c2.size());
    assertTrue(c1.size() <= n);
    Scalar s1 = addDistances(c1, dinf);
    Scalar s2 = addDistances(c2, dinf);
    Chop._10.requireClose(s1, s2);
  }

  @RepeatedTest(4)
  void testOne(RepetitionInfo repetitionInfo) {
    int dim = repetitionInfo.getCurrentRepetition();
    _checkCenter(Tensors.vector(0.3, .3), 1, dim);
    _checkCenter(Tensors.vector(0.1, .3), 1, dim);
    _checkCenter(Tensors.vector(5, 4.3), 1, dim);
    _checkCenter(Tensors.vector(5, -3.3), 1, dim);
  }

  @RepeatedTest(4)
  void testFew(RepetitionInfo repetitionInfo) {
    int dim = repetitionInfo.getCurrentRepetition();
    _checkCenter(Tensors.vector(0.3, .3), 3, dim);
    _checkCenter(Tensors.vector(0.1, .3), 3, dim);
    _checkCenter(Tensors.vector(5, 4.3), 3, dim);
    _checkCenter(Tensors.vector(5, -3.3), 3, dim);
  }

  @RepeatedTest(4)
  void testMany(RepetitionInfo repetitionInfo) {
    int dim = repetitionInfo.getCurrentRepetition();
    _checkCenter(Tensors.vector(0.3, .3), 20, dim);
    _checkCenter(Tensors.vector(0.1, .3), 20, dim);
    _checkCenter(Tensors.vector(5, 4.3), 20, dim);
    _checkCenter(Tensors.vector(5, -3.3), 20, dim);
  }

  @RepeatedTest(4)
  void testMost(RepetitionInfo repetitionInfo) {
    int dim = repetitionInfo.getCurrentRepetition();
    _checkCenter(Tensors.vector(0.3, .3), 60, dim);
    _checkCenter(Tensors.vector(0.1, .3), 60, dim);
    _checkCenter(Tensors.vector(5, 4.3), 60, dim);
    _checkCenter(Tensors.vector(5, -3.3), 60, dim);
  }

  @RepeatedTest(4)
  void testAll(RepetitionInfo repetitionInfo) {
    int dim = repetitionInfo.getCurrentRepetition();
    _checkCenter(Tensors.vector(0.3, .3), 160, dim);
    _checkCenter(Tensors.vector(0.1, .3), 160, dim);
    _checkCenter(Tensors.vector(5, 4.3), 160, dim);
    _checkCenter(Tensors.vector(5, -3.3), 160, dim);
  }
}
