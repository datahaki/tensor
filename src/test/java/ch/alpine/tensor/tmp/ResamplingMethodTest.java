// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Clips;

class ResamplingMethodTest {
  @ParameterizedTest
  @EnumSource
  void testBasic(ResamplingMethods resamplingMethods) {
    ResamplingMethod resamplingMethod = resamplingMethods.get();
    Distribution v_distribution = LogNormalDistribution.standard();
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(0.3), RandomVariate.of(v_distribution, 3));
    resamplingMethod.insert(navigableMap, RealScalar.of(1), RandomVariate.of(v_distribution, 3));
    resamplingMethod.insert(navigableMap, RealScalar.of(3), RandomVariate.of(v_distribution, 3));
    resamplingMethod.insert(navigableMap, RealScalar.of(4), RandomVariate.of(v_distribution, 3));
    Tensor x = Tensor.of(navigableMap.navigableKeySet().stream());
    Tensor v1 = x.maps(s -> resamplingMethod.evaluate(navigableMap, s));
    Tensor v2 = x.maps(s -> resamplingMethod.evaluate(navigableMap.navigableKeySet(), navigableMap::get, s));
    assertEquals(v1, v2);
    assertThrows(Exception.class, () -> resamplingMethod.evaluate(navigableMap, RealScalar.of(0.2)));
    assertThrows(Exception.class, () -> resamplingMethod.evaluate(navigableMap, RealScalar.of(4.2)));
    assertThrows(Exception.class, () -> resamplingMethod.evaluate(navigableMap.navigableKeySet(), navigableMap::get, RealScalar.of(0.2)));
    assertThrows(Exception.class, () -> resamplingMethod.evaluate(navigableMap.navigableKeySet(), navigableMap::get, RealScalar.of(4.2)));
  }

  @ParameterizedTest
  @EnumSource
  void testRes(ResamplingMethods resamplingMethods) {
    ResamplingMethod resamplingMethod = resamplingMethods.get();
    Distribution v_distribution = LogNormalDistribution.standard();
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    resamplingMethod.insert(navigableMap, RealScalar.of(0.3), RandomVariate.of(v_distribution, 3));
    resamplingMethod.insert(navigableMap, RealScalar.of(1), RandomVariate.of(v_distribution, 3));
    resamplingMethod.insert(navigableMap, RealScalar.of(3), RandomVariate.of(v_distribution, 3));
    resamplingMethod.insert(navigableMap, RealScalar.of(4), RandomVariate.of(v_distribution, 3));
    assumeFalse(resamplingMethod.equals(ResamplingMethod.NONE));
    Distribution k_distribution = UniformDistribution.of(Clips.keycover(navigableMap));
    Tensor x = RandomVariate.of(k_distribution, 10);
    Tensor v1 = x.maps(s -> resamplingMethod.evaluate(navigableMap, s));
    Tensor v2 = x.maps(s -> resamplingMethod.evaluate(navigableMap.navigableKeySet(), navigableMap::get, s));
    assertEquals(v1, v2);
  }
}
