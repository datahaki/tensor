// code by jph
package ch.alpine.tensor.ext;

import java.util.List;

/** API is inspired by Mathematica */
public enum Lists {
  ;
  /** "Rest[expr] gives expr with the first element removed."
   * <p>inspired by
   * <a href="https://reference.wolfram.com/language/ref/Rest.html">Rest</a>
   * 
   * @param list
   * @return sublist of given list from element at position 1 to last element
   * @throws Exception if given list is empty */
  public static <T> List<T> rest(List<T> list) {
    return list.subList(1, list.size());
  }

  /** "Last[expr] gives the last element in expr."
   * <p>inspired by
   * <a href="https://reference.wolfram.com/language/ref/Last.html">Last</a>
   * 
   * @param list
   * @return last element in given list
   * @throws Exception list is empty */
  public static <T> T last(List<T> list) {
    return list.get(list.size() - 1);
  }
}
