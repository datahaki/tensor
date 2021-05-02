// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ImportTest;

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
