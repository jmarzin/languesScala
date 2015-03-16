package controllers

import org.scalatest.FunSuite

/**
 * Created by jacques on 16/03/15.
 */
class Admins$Test extends FunSuite {
  test("langue") {
    assert(Admins.langue("it") === "italien")
    assert(Admins.langue("es") === "espagnol")
    assert(Admins.langue("an") === "anglais")
    assert(Admins.langue("") === "lingvo")
  }
}
