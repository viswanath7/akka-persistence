package com.example.akka.application

/**
  * Created by viswanathj on 06/01/2017.
  */
object Test extends App {

  val successSeriesInt = List(200,201,202,203,204,205,206,207,208,226)
  val successSeriesDouble: List[Double] = List(200.0,201.0,202.0,203.0,204.0,205.0,206.0,207.0,208.0,226.0)

  println(s"Boolean result for integer list: \n ${createBooleanResult(successSeriesInt)(_ / 100 == 2)}")
  println(s"Division result for integer list: \n ${createDivisionResult(successSeriesInt)(_ / 100)}")

  println(s"Boolean result for double list: \n ${createBooleanResult(successSeriesDouble)(_ / 100 == 2)}" )
  println(s"Division result for double list: \n ${createDivisionResult(successSeriesDouble)(_ / 100)}")


  private def createBooleanResult[T <: AnyVal](statusCodes:List[T])(op: T=> Boolean) = {
    statusCodes.map(op).mkString(", ")
  }

  private def createDivisionResult[T:Numeric](statusCodes:List[T])(op: T=> AnyVal) = {
    statusCodes.map(op).mkString(", ")
  }

}
