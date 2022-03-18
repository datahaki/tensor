// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ext.Serialization;

public class PivotsTest {
  @ParameterizedTest
  @EnumSource(Pivots.class)
  public void testSerializable(Pivot pivot) throws ClassNotFoundException, IOException {
    Serialization.copy(pivot);
  }

  @ParameterizedTest
  @EnumSource(Pivots.class)
  public void testValueOf(Pivots pivots) {
    assertEquals(Enum.valueOf(Pivots.class, pivots.name()), pivots);
  }

  @Test
  public void testClassEnumConstants() {
    Class<?> cls = Pivots.class;
    assertTrue(Enum.class.isAssignableFrom(cls));
    Object[] enumConstants = cls.getEnumConstants();
    Object result = null;
    for (Object obj : enumConstants) {
      if (obj.toString().equals("ARGMAX_ABS")) {
        result = obj;
      }
    }
    assertEquals(result, Pivots.ARGMAX_ABS);
  }

  @Test
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Pivots.class.getModifiers()));
  }
}
