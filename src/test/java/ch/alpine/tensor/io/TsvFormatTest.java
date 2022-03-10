// code by jph
package ch.alpine.tensor.io;

import java.io.File;
import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.usr.TestFile;
import junit.framework.TestCase;

public class TsvFormatTest extends TestCase {
  public void testSimple() throws IOException {
    File file = TestFile.withExtension("tsv");
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-10, 10), 6, 4);
    Export.of(file, matrix);
    Tensor result = Import.of(file);
    assertEquals(matrix, result);
    file.delete();
  }
}
