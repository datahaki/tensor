// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;

/** clip to an interval of non-zero width
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
/* package */ class ClipInterval implements Clip {
  private final Scalar min;
  private final Scalar max;
  private final Scalar width;

  public ClipInterval(Scalar min, Scalar max, Scalar width) {
    this.min = min;
    this.max = max;
    this.width = width;
  }

  @Override
  public final Scalar apply(Scalar scalar) {
    if (Scalars.lessThan(scalar, min))
      return min;
    if (Scalars.lessThan(max, scalar))
      return max;
    return scalar;
  }

  @Override // from Clip
  public final boolean isInside(Scalar scalar) {
    return apply(scalar).equals(scalar);
  }

  @Override // from Clip
  public final boolean isOutside(Scalar scalar) {
    return !isInside(scalar);
  }

  @Override // from Clip
  public final Scalar requireInside(Scalar scalar) {
    if (isInside(scalar))
      return scalar;
    throw new Throw(this, scalar);
  }

  @Override // from Clip
  public Scalar rescale(Scalar scalar) {
    return apply(scalar).subtract(min).divide(width);
  }

  @Override // from Clip
  public final Scalar min() {
    return min;
  }

  @Override // from Clip
  public final Scalar max() {
    return max;
  }

  @Override // from Clip
  public final Scalar width() {
    return width;
  }

  // ---
  @Override // from Object
  public final int hashCode() {
    return min.hashCode() + 31 * max.hashCode();
  }

  @Override // from Object
  public final boolean equals(Object object) {
    return object instanceof Clip clip //
        && min.equals(clip.min()) //
        && max.equals(clip.max());
  }

  @Override // from Object
  public final String toString() {
    return MathematicaFormat.concise("Clip", min(), max());
  }
}
