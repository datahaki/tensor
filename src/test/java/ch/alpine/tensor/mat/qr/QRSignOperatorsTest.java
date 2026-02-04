// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ext.Serialization;

class QRSignOperatorsTest {
  @ParameterizedTest
  @EnumSource
  void testSerializable(QRSignOperators qrSignOperators) throws ClassNotFoundException, IOException {
    Serialization.copy(qrSignOperators);
  }

  @Test
  void testIsDetExact() {
    assertTrue(QRSignOperators.STABILITY.isDetExact());
    assertFalse(QRSignOperators.ORIENTATION.isDetExact());
  }

  @Test
  void testPackageVisibility() {
    assertTrue(Modifier.isPublic(QRSignOperators.class.getModifiers()));
  }
}
