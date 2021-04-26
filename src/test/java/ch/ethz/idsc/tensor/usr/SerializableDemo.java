// code by jph
package ch.ethz.idsc.tensor.usr;

import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImportTest;

/** export to binary files in test resources */
/* package */ enum SerializableDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = ImportTest.CONTENT;
    Export.object(ImportTest.IO_OBJECT_TENSOR, tensor);
    Export.object(ImportTest.IO_OBJECT_UNMODIFIABLE, tensor.unmodifiable());
    Tensor viewtensor = Unprotect.references(tensor);
    Export.object(ImportTest.IO_OBJECT_VIEWTENSOR, viewtensor);
  }
}
