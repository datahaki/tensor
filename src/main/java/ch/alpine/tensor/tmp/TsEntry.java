// code by jph
package ch.alpine.tensor.tmp;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.MathematicaFormat;

/** class exists to avoid template arguments as in
 * "Entry<Scalar, Tensor>"
 * 
 * record in java 17 */
public final class TsEntry implements Serializable {
  private final Scalar key;
  private final Tensor value;

  public TsEntry(Scalar key, Tensor value) {
    this.key = key;
    this.value = value;
  }

  public Scalar key() {
    return key;
  }

  public Tensor value() {
    return value;
  }

  @Override
  public int hashCode() {
    return key.hashCode() + 31 * value.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof TsEntry) {
      TsEntry tsEntry = (TsEntry) object;
      return tsEntry.key.equals(key) //
          && tsEntry.value.equals(value);
    }
    return false;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("TsEntry", key, value);
  }
}
