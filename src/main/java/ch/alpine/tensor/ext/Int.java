// code by jph
package ch.alpine.tensor.ext;

/** alternative to AtomicInteger in a single thread environment
 * 
 * @apiNote not Serializable */
public class Int { // MutableInt
  private int value;

  public Int(int value) {
    this.value = value;
  }

  public Int() {
    this(0);
  }

  /** @return */
  public int intValue() {
    return value;
  }

  /** @return */
  public int getAndIncrement() {
    return value++;
  }

  /** @return */
  public int getAndDecrement() {
    return value--;
  }
}
