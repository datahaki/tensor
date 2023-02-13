// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;

class HaarWaveletTransformTest {
  @Test
  void test() {
    Tensor x = Range.of(0, 4);
    Tensor r = HaarWaveletTransform.of(x);
    Tensor s = Tensors.fromString("{{1,1,1,0},{1,1,-1,0},{1,-1,0,1},{1,-1,0,-1}}");
    assertEquals(r, s.dot(x));
  }
}
