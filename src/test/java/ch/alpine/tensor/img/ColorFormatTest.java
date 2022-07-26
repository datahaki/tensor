// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;

class ColorFormatTest {
  @Test
  void testRandom() {
    Random random = new SecureRandom();
    for (int index = 0; index < 10; ++index) {
      int red = random.nextInt(256);
      int green = random.nextInt(256);
      int blue = random.nextInt(256);
      int alpha = random.nextInt(256);
      Color color = new Color(red, green, blue, alpha);
      Tensor vector = ColorFormat.toVector(color);
      assertEquals(red, vector.Get(0).number());
      assertEquals(green, vector.Get(1).number());
      assertEquals(blue, vector.Get(2).number());
      assertEquals(alpha, vector.Get(3).number());
      Color color2 = ColorFormat.toColor(vector);
      assertEquals(color, color2);
      Tensor vector2 = ColorFormat.toVector(color2);
      assertEquals(vector, vector2);
    }
  }

  @Test
  void testFailValue() {
    ColorFormat.toColor(Tensors.vector(0, 0, 0, 255.9));
    assertThrows(IllegalArgumentException.class, () -> ColorFormat.toColor(Tensors.vector(0, 0, 0, 256)));
  }

  @Test
  void testFailLength() {
    assertThrows(Throw.class, () -> ColorFormat.toColor(Array.zeros(3)));
    assertThrows(Throw.class, () -> ColorFormat.toColor(Array.zeros(5)));
  }
}
