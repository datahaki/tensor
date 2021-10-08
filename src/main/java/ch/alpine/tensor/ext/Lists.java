// code by jph
package ch.alpine.tensor.ext;

import java.util.List;

public enum Lists {
  ;
  /** @param list
   * @return sublist of given list from element at position 1 to last element
   * @throws Exception if given list is empty */
  public static <T> List<T> withoutHead(List<T> list) {
    return list.subList(1, list.size());
  }

  /** @param list
   * @return last element in given list
   * @throws Exception list is empty */
  public static <T> T last(List<T> list) {
    return list.get(list.size() - 1);
  }
}
