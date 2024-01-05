// code by jph
package ch.alpine.tensor.io;

/** file extensions used by the tensor library
 * 
 * @see ImportHelper
 * @see ExportHelper */
/* package */ enum Extension {
  /** uncompressed loss-less image format, no alpha channel */
  BMP,
  /** table with comma-separated values */
  CSV,
  /** image and animation format
   * when exporting a tensor to an image, any alpha value != 255
   * results in the pixel to be transparent */
  GIF,
  /** compressed version of another format, for instance csv.gz */
  GZ,
  /** compressed, lossy image format */
  JPEG,
  JPG,
  /** MATLAB m file, export only */
  M,
  /** {@link Get} and {@link Put} operate on any file extension.
   * In particular, Mathematica is <em>not</em> the official extension.
   * However, we choose the extension for import/export because of
   * the characteristic {@link MathematicaFormat}. */
  MATHEMATICA,
  /** matrix market */
  MTX,
  /** compressed image format with alpha channel */
  PNG,
  /** Tag Image File Format */
  TIF,
  TIFF,
  /** tab separated values */
  TSV,
  /** ".vector" is an extension specific to the tensor library
   * 
   * @see VectorFormat */
  VECTOR;

  /** @param string
   * @return
   * @throws IllegalArgumentException if given string does not match
   * any known file types */
  public static Extension of(String string) {
    return valueOf(string.toUpperCase());
  }
}
