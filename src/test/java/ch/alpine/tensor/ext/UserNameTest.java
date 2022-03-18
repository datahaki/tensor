// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class UserNameTest {
  @Test
  public void testGetProperty() {
    String name = System.getProperty("user.name");
    assertFalse(name.isEmpty());
  }

  @Test
  public void testUsername() {
    assertNotNull(UserName.get());
    assertFalse(UserName.get().isEmpty());
  }

  @Test
  public void testIs() {
    assertTrue(UserName.is(UserName.get()));
    assertFalse(UserName.is(""));
    assertFalse(UserName.is("&E T H!"));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> UserName.is(null));
  }
}
