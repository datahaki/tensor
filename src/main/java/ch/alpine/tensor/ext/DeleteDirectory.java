// code by jph
package ch.alpine.tensor.ext;

import java.io.File;
import java.io.IOException;

/** recursive file/directory deletion
 * 
 * safety from erroneous use is enhanced by several criteria
 * 1) checking the depth of the directory tree T to be deleted
 * against a permitted upper bound "max_nested"
 * 2) checking the number of files to be deleted #F
 * against a permitted upper bound "max_delete"
 * 3) all files are checked for write permission using {@link File#canWrite()}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/DeleteDirectory.html">DeleteDirectory</a> */
public class DeleteDirectory {
  /** use the mask bit DELETE_FAIL_ABORTS for an additional abort criteria:
   * 4) if deletion of a file or directory fails, the process aborts */
  public static final int DELETE_FAIL_ABORTS = 1;

  /** Example: The command
   * DeleteDirectory.of(new File("/user/name/myapp/recordings/log20171024"), 2, 1000);
   * deletes given directory with sub directories of depth of at most 2,
   * and max number of total files and directories less equals than 1000.
   * No files are deleted if the directory tree exceeds 2, or total of files exceeds 1000.
   * 
   * The abort criteria are described at top of class
   * 
   * @param file
   * @param max_nested
   * @param max_delete
   * @return
   * @throws Exception if given directory does not exist, or criteria are not met */
  public static DeleteDirectory of(File file, int max_nested, long max_delete) throws IOException {
    return of(file, max_nested, max_delete, 0);
  }

  /** @param file
   * @param max_nested
   * @param max_delete
   * @param mask
   * @return
   * @throws IOException */
  public static DeleteDirectory of(File file, int max_nested, long max_delete, int mask) throws IOException {
    DeleteDirectory deleteDirectory = new DeleteDirectory(max_nested, mask);
    deleteDirectory.visitRecursively(file, 0, false);
    if (deleteDirectory.fileCount <= max_delete) { // else abort criteria 2)
      deleteDirectory.fileCount = 0; // reset counter
      deleteDirectory.visitRecursively(file, 0, true);
      return deleteDirectory;
    }
    throw new IOException(String.format("more files to be deleted than allowed (%d <= %d) in %s", //
        max_delete, //
        deleteDirectory.fileCount(), file));
  }

  // ---
  private final int max_nested;
  private final boolean delete_fail_aborts;
  // ---
  private long fileCount = 0;
  private int reachedDepth = 0;

  private DeleteDirectory(int max_nested, int mask) {
    this.max_nested = max_nested;
    this.delete_fail_aborts = (mask & DELETE_FAIL_ABORTS) == DELETE_FAIL_ABORTS;
  }

  private void visitRecursively(File file, int depth, boolean delete) throws IOException {
    if (max_nested < depth) // enforce depth limit, abort criteria 1)
      throw new IOException("directory tree exceeds permitted depth");
    ++fileCount;
    reachedDepth = Math.max(reachedDepth, depth);
    if (file.isDirectory()) // if file is a directory, recur
      for (File entry : file.listFiles())
        visitRecursively(entry, depth + 1, delete);
    if (delete) {
      boolean file_delete = file.delete();
      if (!file_delete && delete_fail_aborts) // abort criteria 4)
        throw new IOException("cannot delete " + file.getAbsolutePath());
    } else //
    if (!file.canWrite()) // abort criteria 3)
      throw new IOException("cannot write " + file.getAbsolutePath());
  }

  /** @return number of deleted files including directories */
  public long fileCount() {
    return fileCount;
  }

  /** @return greatest directory depth of file that is not a directory */
  public int reachedDepth() {
    return reachedDepth;
  }
}
