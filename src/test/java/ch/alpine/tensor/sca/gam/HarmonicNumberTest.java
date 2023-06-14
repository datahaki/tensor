// code by jph
package ch.alpine.tensor.sca.gam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class HarmonicNumberTest {
  @Test
  void testZero() {
    assertEquals(HarmonicNumber.unit().apply(0), RealScalar.ZERO);
  }

  @Test
  void testTen() throws ClassNotFoundException, IOException {
    HarmonicNumber harmonicNumber = Serialization.copy(HarmonicNumber.unit());
    Tensor vector = Tensor.of(IntStream.range(1, 11).mapToObj(HarmonicNumber.unit()));
    Tensor expect = Tensors.fromString("{1, 3/2, 11/6, 25/12, 137/60, 49/20, 363/140, 761/280, 7129/2520, 7381/2520}");
    assertEquals(vector, expect);
    assertTrue(harmonicNumber.toString().startsWith("HarmonicNumber["));
  }

  @Test
  void testTwoTen() {
    Tensor vector = Tensor.of(IntStream.range(1, 11).mapToObj(HarmonicNumber.of(2)));
    Tensor expect = Tensors.fromString("{1, 5/4, 49/36, 205/144, 5269/3600, 5369/3600, 266681/176400, 1077749/705600, 9778141/6350400, 1968329/1270080}");
    assertEquals(vector, expect);
  }
}
