// code by jph
package ch.alpine.tensor.ext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/$HomeDirectory.html">$HomeDirectory</a> */
public enum HomeDirectory {
  ;
  private static Path user_home() {
    try {
      return Path.of(System.getProperty("user.home"));
    } catch (Exception exception) { // security exception, null pointer
      return null;
    }
  }

  private static final Path USER_HOME = user_home();

  @PackageTestAccess
  static Path join(Path start, String... parts) {
    return Arrays.stream(parts) //
        .reduce(start, Path::resolve, (p1, p2) -> p1.resolve(p2));
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/string[0]/string[1]/... */
  public static Path file(String... strings) {
    return join(USER_HOME, strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Desktop/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Desktop/string[0]/string[1]/... */
  public static Path Desktop(String... strings) {
    return subfolder("Desktop", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Documents/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Documents/string[0]/string[1]/... */
  public static Path Documents(String... strings) {
    return subfolder("Documents", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Downloads/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Downloads/string[0]/string[1]/... */
  public static Path Downloads(String... strings) {
    return subfolder("Downloads", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Pictures/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Pictures/string[0]/string[1]/... */
  public static Path Pictures(String... strings) {
    return subfolder("Pictures", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Music/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Music/string[0]/string[1]/... */
  public static Path Music(String... strings) {
    return subfolder("Music", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Videos/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Videos/string[0]/string[1]/... */
  public static Path Videos(String... strings) {
    return subfolder("Videos", strings);
  }

  /** On linux, the filename has the form
   * /home/$USERNAME/Videos/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Videos/string[0]/string[1]/... */
  public static Path Templates(String... strings) {
    return subfolder("Templates", strings);
  }

  // helper function
  private static Path subfolder(String folder, String... strings) {
    Path path = file(folder);
    if (!Files.isDirectory(path))
      try {
        Files.createDirectory(path);
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }
    return join(path, strings);
  }
}
