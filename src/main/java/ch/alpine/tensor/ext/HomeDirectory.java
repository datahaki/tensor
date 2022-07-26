// code by jph
package ch.alpine.tensor.ext;

import java.io.File;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/$HomeDirectory.html">$HomeDirectory</a> */
public enum HomeDirectory {
  ;
  private static File user_home() {
    try {
      return new File(System.getProperty("user.home"));
    } catch (Exception exception) { // security exception, null pointer
      return null;
    }
  }

  private static final File USER_HOME = user_home();

  /** On linux, the directory has the form
   * /home/$USERNAME/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/string[0]/string[1]/... */
  @SafeVarargs
  public static File file(String... strings) {
    return concat(USER_HOME, strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Desktop/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Desktop/string[0]/string[1]/... */
  @SafeVarargs
  public static File Desktop(String... strings) {
    return subfolder("Desktop", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Documents/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Documents/string[0]/string[1]/... */
  @SafeVarargs
  public static File Documents(String... strings) {
    return subfolder("Documents", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Downloads/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Downloads/string[0]/string[1]/... */
  @SafeVarargs
  public static File Downloads(String... strings) {
    return subfolder("Downloads", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Pictures/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Pictures/string[0]/string[1]/... */
  @SafeVarargs
  public static File Pictures(String... strings) {
    return subfolder("Pictures", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Music/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Music/string[0]/string[1]/... */
  @SafeVarargs
  public static File Music(String... strings) {
    return subfolder("Music", strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Videos/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Videos/string[0]/string[1]/... */
  @SafeVarargs
  public static File Videos(String... strings) {
    return subfolder("Videos", strings);
  }

  // helper function
  @SafeVarargs
  private static File subfolder(String folder, String... strings) {
    File file = file(folder);
    file.mkdir();
    return concat(file, strings);
  }

  private static File concat(File file, String[] strings) {
    for (String string : strings)
      file = new File(file, string);
    return file;
  }
}
