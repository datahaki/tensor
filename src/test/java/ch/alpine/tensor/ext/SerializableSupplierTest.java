// code by jph
package ch.alpine.tensor.ext;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class SerializableSupplierTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    @SuppressWarnings("unchecked")
    Supplier<Tensor> serializableSupplier = (Supplier<Tensor> & Serializable) //
    () -> Tensors.empty();
    Supplier<Tensor> supplier = Serialization.copy(serializableSupplier);
    assertEquals(supplier.get(), Tensors.empty());
  }

  public void testOptional() throws ClassNotFoundException, IOException {
    @SuppressWarnings("unchecked")
    Supplier<Optional<Tensor>> serializableSupplier = (Supplier<Optional<Tensor>> & Serializable) //
    () -> Optional.of(RealScalar.ONE);
    Supplier<Optional<Tensor>> supplier = Serialization.copy(serializableSupplier);
    assertEquals(supplier.get().get(), RealScalar.ONE);
  }
}
