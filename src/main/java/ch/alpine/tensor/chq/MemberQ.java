// code by jph
package ch.alpine.tensor.chq;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;

/** membership status of given tensor
 * 
 * If an enum implements this interface in a singleton pattern, then
 * the use of the enum constant INSTANCE is recommended.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MemberQ.html">MemberQ</a> */
@FunctionalInterface
public interface MemberQ {
  // TODO SOPHUS introduce requireMember . Debug with option to disable flag!
  /** @param tensor
   * @return whether given tensor is member (of a region) */
  boolean isMember(Tensor tensor);

  /** @param tensor
   * @return tensor
   * @throws Exception if given tensor does not have membership status */
  default Tensor requireMember(Tensor tensor) {
    if (isMember(tensor))
      return tensor;
    throw new Throw(tensor);
  }
}
