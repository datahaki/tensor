// adapted from gemini
package ch.alpine.tensor.ext;

import ch.alpine.tensor.fft.FourierDCT;
import ch.alpine.tensor.fft.FourierDST;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RomanNumeral.html">RomanNumeral</a>
 * 
 * @see FourierDCT
 * @see FourierDST */
public enum RomanNumeral {
  ;
  private enum Hook {
    M(1000),
    CM(900),
    D(500),
    CD(400),
    C(100),
    XC(90),
    L(50),
    XL(40),
    X(10),
    IX(9),
    V(5),
    IV(4),
    I(1);

    private final int value;

    Hook(int value) {
      this.value = value;
    }
  }

  /** @param num
   * @return */
  public static String of(int num) {
    StringBuilder stringBuilder = new StringBuilder();
    for (Hook hook : Hook.values())
      while (num >= hook.value) {
        num -= hook.value;
        stringBuilder.append(hook);
      }
    return stringBuilder.toString();
  }
}
