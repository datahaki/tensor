// code by jph
package ch.ethz.idsc.tensor.io;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatFileFormatTest extends TestCase {
  public void testGet() {
    AssertFail.of(() -> MatFileFormat.parse(new byte[] { 1, 2, 3 }));
  }

  public void testPut() {
    AssertFail.of(() -> MatFileFormat.of(Tensors.vector(2, 3, 4)));
  }
}
