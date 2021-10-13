// code by jph
package ch.alpine.tensor.ext;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** marker annotation to be used on code segments that have package visibility
 * for the purpose of testing, and to achieve greater test coverage of the source.
 * 
 * Regarding types: The annotation should only be used on types that are nested.
 * 
 * Inspired by Guava's "VisibleForTesting" by Google */
@Retention(SOURCE)
@Target({ TYPE, FIELD, METHOD, CONSTRUCTOR })
public @interface PackageTestAccess {
  // ---
}
