// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UserNameTest {
  @Test
  void testGetProperty() {
    String name = System.getProperty("user.name");
    assertFalse(name.isEmpty());
  }

  @Test
  void testUsername() {
    assertNotNull(UserName.get());
    assertFalse(UserName.get().isEmpty());
  }

  @Test
  void testIs() {
    assertTrue(UserName.is(UserName.get()));
    assertFalse(UserName.is(""));
    assertFalse(UserName.is("&E T H!"));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> UserName.is(null));
  }
}
