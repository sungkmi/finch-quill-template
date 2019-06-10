package db

import scala.concurrent.ExecutionContext

class DbTestFramework extends utest.runner.Framework {

  implicit val ec = ExecutionContext.global

  override def setup() = {
    TestPostgres.setup
    println(s"Setting up DbTestFramework: ${TestPostgres.testClient}")
  }

  override def teardown() = {
    TestPostgres.teardown
    println(s"Tearing down DbTestFramework: ${TestPostgres.testClient}")
  }
}
