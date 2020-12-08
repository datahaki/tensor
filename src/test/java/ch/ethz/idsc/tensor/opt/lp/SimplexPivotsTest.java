// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import java.io.IOException;

import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class SimplexPivotsTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    for (SimplexPivot simplexPivot : SimplexPivots.values())
      Serialization.copy(simplexPivot);
  }
}