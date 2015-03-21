package models

import models.StringUtils._
import org.scalatest.FunSuite

/**
 * Created by jacques on 21/03/15.
 */
class StringImprovementsTest extends FunSuite {
  test("pluralize") {
    assert("mot".pluralize(0) === "mot")
    assert("mot".pluralize(1) === "mot")
    assert("mot".pluralize(2) === "mots")
  }

}
