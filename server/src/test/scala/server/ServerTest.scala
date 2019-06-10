package server

import io.finch._
import utest._

object ServerTest extends TestSuite {

  val tests = Tests {
    "healthcheck" - {
      assert(Server.healthcheck(Input.get("/")).awaitValueUnsafe() == Some("OK"))
    }

    "helloWorld" - {
      assert(Server.helloWorld(Input.get("/hello")).awaitValueUnsafe() == Some(Server.Message("World")))
    }

    "hello" - {
      assert(Server.hello(Input.get("/hello/foo")).awaitValueUnsafe() == Some(Server.Message("foo")))
    }
  }
}
