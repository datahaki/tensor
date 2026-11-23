// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class AffineAppendTest {
  @Test
  void testSimple() {
    Tensor vector = Tensors.vector(2, 3);
    Tensor result = AffineAppend.M1.apply(vector);
    assertEquals(vector, Tensors.vector(2, 3));
    assertEquals(result, Tensors.vector(2, 3, 1));
  }

  @ParameterizedTest
  @EnumSource
  void testLength(AffineAppend affineAppend) {
    Tensor vector = affineAppend.apply(Tensors.fromString("{2[m], 2022-02-30T23:45}"));
    assertEquals(vector.length(), 3);
  }

  @Test
  void testEmptyFail() {
    assertThrows(Exception.class, () -> AffineAppend.M1.apply(Tensors.empty()));
  }

  @Test
  void testBlub() {
    Tensor v = Tensors.fromString("{2[m],1}");
    Tensor m = Tensors.fromString("{{1[m^-1],1},{1[m^-1],1}}");
    m.dot(v);
  }
}
