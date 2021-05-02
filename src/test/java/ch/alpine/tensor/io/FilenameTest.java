// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FilenameTest extends TestCase {
  public void testFailSpacing() {
    Filename filename = new Filename("dir/title.bmp ");
    AssertFail.of(() -> filename.extension());
  }

  public void testFailExtension() {
    Filename filename = new Filename("dir/title.ext");
    AssertFail.of(() -> filename.extension());
  }

  public void testFailNoExt() {
    Filename filename = new Filename("dir/mybmp");
    AssertFail.of(() -> filename.extension());
  }

  public void testFailTruncate() {
    Filename filename = new Filename("dir/mybmp");
    AssertFail.of(() -> filename.truncate());
  }
}
