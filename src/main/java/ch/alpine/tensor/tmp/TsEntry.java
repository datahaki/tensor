// code by jph
package ch.alpine.tensor.tmp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.MathematicaFormat;

/** class exists to avoid template arguments as in
 * "Entry<Scalar, Tensor>"
 * 
 * deliberately not serializable */
public record TsEntry(Scalar key, Tensor value) {
  @Override
  public String toString() {
    return MathematicaFormat.concise("TsEntry", key, value);
  }
}
