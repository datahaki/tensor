// code by jph
package ch.alpine.tensor.ext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** folders do not have to exists
 * base folders are created if needed only when {@link #resolve(String...)}
 * is invoked
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/$HomeDirectory.html">$HomeDirectory</a> */
public enum HomeDirectory {
  _local_share(".local", "share"),
  /** database */
  Database,
  /** Items shown on the desktop */
  Desktop,
  /** Default location for text files, Office docs, etc. */
  Documents,
  /** Where browsers save files by default */
  Downloads,
  /** auto-generated and machine-read files */
  Ephemeral,
  /** Audio files */
  Music,
  /** Image files */
  Pictures,
  /** Source code */
  Projects,
  /** Publicly shared files */
  Public,
  public_html,
  /** (possibly does not pre-exist on Windows) */
  Templates,
  /** Video files */
  Videos;

  private final Path path;

  HomeDirectory(String... strings) {
    path = join(StaticHelper.USER_HOME, strings);
  }

  HomeDirectory() {
    path = join(StaticHelper.USER_HOME, name());
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/name()/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Desktop/string[0]/string[1]/... */
  public Path resolve(String... strings) {
    if (!Files.isDirectory(path))
      try {
        // create missing base directories, e.g. .local/share
        Files.createDirectories(path);
      } catch (IOException ioException) {
        throw new UncheckedIOException(ioException);
      }
    return join(path, strings);
  }

  public Path createDirectories(String... strings) {
    Path path = resolve(strings);
    if (!Files.isDirectory(path))
      try {
        Files.createDirectories(path);
      } catch (IOException ioException) {
        throw new UncheckedIOException(ioException);
      }
    return path;
  }

  private static Path join(Path start, String... parts) {
    return Arrays.stream(parts).reduce(start, Path::resolve, Path::resolve);
  }

  @Override
  public String toString() {
    return path.toString();
  }
}
