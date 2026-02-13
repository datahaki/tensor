// code by jph
package ch.alpine.tensor.ext;

import java.nio.file.Path;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/$UserName.html">$UserName</a> */
public enum UserName {
  ;
  private static final String USER_NAME = System.getProperty("user.name");

  /** @return system user name */
  public static String whoami() {
    return USER_NAME;
  }

  /** @param username
   * @return true if system user name equals given parameter username
   * @throws Exception if given username is null */
  public static boolean is(String username) {
    return username.equals(whoami());
  }

  public static Path home() {
    return StaticHelper.USER_HOME;
  }
}
