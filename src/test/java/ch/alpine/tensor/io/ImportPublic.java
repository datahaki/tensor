// code by jph
package ch.alpine.tensor.io;

import java.io.File;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

public enum ImportPublic {
  ;
  private static final File IO_OBJECT = new File("src/test/resources/io/object");
  public static final File IO_OBJECT_TENSOR = new File(IO_OBJECT, "tensor.object");
  public static final File IO_OBJECT_UNMODIFIABLE = new File(IO_OBJECT, "unmodifiable.object");
  public static final Tensor CONTENT = Tensors.of( //
      RealScalar.ONE, //
      RealScalar.of(3.15), //
      Pi.in(30), //
      ComplexScalar.of(2, 3), //
      Quantity.of(3, "m"), //
      GaussScalar.of(3, 17), //
      Quaternion.of(3, 4, 5, 6));
}
