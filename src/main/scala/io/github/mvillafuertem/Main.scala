package io.github.mvillafuertem

import cats.effect.{ ExitCode, IO, IOApp }
import io.github.mvillafuertem.user.User
import neotypes.GraphDatabase
import neotypes.cats.effect.implicits._
import neotypes.generic.DerivedResultMapper.deriveResultMapper
import neotypes.mappers.ValueMapper
import org.neo4j.driver.Value
import scalapb.UnknownFieldSet
// import neotypes.generic.auto._
import neotypes.implicits.all._
import org.neo4j.driver.AuthTokens

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    // implicit val valueMapper: ValueMapper[UnknownFieldSet] = semiauto.deriveUnwrappedValueMapper

    implicit val test = new ValueMapper[UnknownFieldSet] {
      override def to(fieldName: String, value: Option[Value]): Either[Throwable, UnknownFieldSet] =
        // only for test
        Right(UnknownFieldSet.empty)
    }

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
