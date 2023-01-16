package io.github.mvillafuertem

import cats.effect.{ ExitCode, IO, IOApp }
import io.github.mvillafuertem.user.User
import neotypes.GraphDatabase
import neotypes.cats.effect.implicits._
import neotypes.generic.DerivedResultMapper.deriveResultMapper
import neotypes.implicits.all._
import org.neo4j.driver.AuthTokens

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val user = User.of(name = "Pepe", surname = "Test")
    println(user)

    GraphDatabase
      .driver[IO](
        "neo4j://localhost:7687",
        AuthTokens.basic("neo4j", "accounttest")
      )
      .use { driver =>
        "MATCH (u: User) RETURN u LIMIT 1".readOnlyQuery[User].single(driver)
      }
      .as(ExitCode.Success)
  }

}
