package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

class StaticHelperTest {
  @Test
  void testUserHome() {
    assertTrue(Files.isDirectory(StaticHelper.USER_HOME));
  }

  @Test
  void testPublic() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
