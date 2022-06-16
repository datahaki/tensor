// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ResourceData;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ColorData.html">ColorData</a>
 * 
 * @see ColorDataGradients */
public enum ColorDataLists {
  /** 2 colors: black and white */
  _000,
  /** 16 colors */
  _001,
  /** 10 colors */
  _003,
  /** 14 colors */
  _058,
  /** 9 colors */
  _061,
  /** 16 colors */
  _063,
  /** 10 colors */
  _074,
  /** 16 colors */
  _094,
  /** 16 colors */
  _096,
  /** 16 colors, Mathematica default */
  _097,
  /** 16 colors */
  _098,
  /** 16 colors */
  _099,
  /** 16 colors */
  _100,
  /** 16 colors */
  _103,
  /** 16 colors */
  _104,
  /** 16 colors */
  _106,
  /** 16 colors */
  _108,
  /** 16 colors */
  _109,
  /** 16 colors */
  _110,
  /** 16 colors */
  _112,
  /** hue palette with 13 colors normalized according to brightness
   * tensor library default */
  _250, // luma
  /** hue palette with 13 colors */
  _251, //
  ;

  private final Tensor tensor = ResourceData.of("/ch/alpine/tensor/img/colorlist/" + name().substring(1) + ".csv");
  private final ColorDataIndexed cyclic = new CyclicColorDataIndexed(tensor);
  private final ColorDataIndexed strict = new StrictColorDataIndexed(tensor);

  /** @return */
  public ColorDataIndexed cyclic() {
    return cyclic;
  }

  /** @return */
  public ColorDataIndexed strict() {
    return strict;
  }
}
