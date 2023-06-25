// code by jph
package ch.alpine.tensor.io;

import java.util.Objects;

import ch.alpine.tensor.ext.FileExtension;

/* package */ class Filename {
  private static final char DOT = '.';
  // ---
  /** name of file */
  private final String string;

  /** @param string */
  public Filename(String string) {
    this.string = Objects.requireNonNull(string);
  }

  /** @return
   * @throws Exception if this filename does not contain the character `.` */
  public Filename truncate() {
    return new Filename(string.substring(0, string.lastIndexOf(DOT)));
  }

  /** Example:
   * "title.csv.gz" gives {@link Extension#GZ}
   * 
   * @return ultimate extension of file derived from the characters after the last '.'
   * @throws IllegalArgumentException if extension is not listed in {@link Extension} */
  public Extension extension() {
    return Extension.of(FileExtension.of(string));
  }
}
