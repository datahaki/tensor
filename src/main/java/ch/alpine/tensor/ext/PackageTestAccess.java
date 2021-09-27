// code by jph
package ch.alpine.tensor.ext;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** marker annotation to be used on code segments that have extended visibility
 * for the purpose of testing, and to achieve greater test coverage of the source.
 * Therefore, the annotation is never used on types.
 * 
 * Inspired by Guava's VisibilityForTesting by Google */
@Retention(SOURCE)
@Target({ FIELD, METHOD, CONSTRUCTOR })
public @interface PackageTestAccess {
  // ---
}
