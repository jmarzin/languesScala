package models

/**
 * Created by jacques on 21/03/15.
 */
object StringUtils {
  implicit class StringImprovements(val s: String) {
    def pluralize(nb: Int) = if(nb > 1) s+"s" else s
  }
}
