// code by jph
package ch.alpine.tensor.ext;

import java.io.File;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RenameDirectory.html">RenameDirectory</a> */
public enum RenameDirectory {
  ;
  /** @param origin existing directory
   * @param target directory that will be created
   * @throws Exception if origin is not an existing directory
   * @throws Exception if target already exists
   * @throws Exception if parent directory of target cannot be created
   * @throws Exception if origin cannot be renamed to target */
  public static void of(File origin, File target) {
    if (!origin.isDirectory())
      throw new IllegalArgumentException("does not exist: " + origin);
    if (target.exists())
      throw new IllegalArgumentException("already exists: " + target);
    File parent = target.getParentFile();
    if (!parent.isDirectory()) { // if parent is not an existing directory ...
      boolean mkdirs = parent.mkdirs(); // ... attempt to create parent folder
      if (!mkdirs)
        throw new RuntimeException("cannot create " + parent);
    }
    boolean renameTo = origin.renameTo(target);
    if (!renameTo)
      throw new RuntimeException("rename failed " + origin + " -> " + target);
  }
}
