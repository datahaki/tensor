// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ImportPublic;

/** export to binary files in test resources */
/* package */ enum SerializableDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = ImportPublic.CONTENT;
    Export.object(ImportPublic.IO_OBJECT_TENSOR, tensor);
    Export.object(ImportPublic.IO_OBJECT_UNMODIFIABLE, tensor.unmodifiable());
  }
}
