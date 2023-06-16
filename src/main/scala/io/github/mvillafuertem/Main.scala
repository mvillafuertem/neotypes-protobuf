package io.github.mvillafuertem

import cats.effect.{ ExitCode, IO, IOApp }
import cats.syntax.option.none
import io.github.mvillafuertem.user.User
import neotypes.GraphDatabase
import neotypes.cats.effect.implicits._
import neotypes.mappers.ResultMapper
import neotypes.model.types.Value
import neotypes.syntax.all._
import org.neo4j.driver.AuthTokens
import scalapb.UnknownFieldSet

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    @scala.annotation.unused
    implicit val valueMapper: ResultMapper[UnknownFieldSet] = ResultMapper.fromMatch { case Value.Str(value) =>
      // only for test
      println(value)
      Right(UnknownFieldSet.empty)
    }

    // implicit val valueMapper = ResultMapper.fromValueMapper[UnknownFieldSet]

    val user = User.of(name = Some("Pepe"), surname = Some("Test"), username = Some("pp"))
    println(user)

    GraphDatabase
      .asyncDriver[IO](
        "neo4j://localhost:7687",
        AuthTokens.basic("neo4j", "accounttest")
      )
      .use { driver =>
//        implicit val test = new ValueMapper[UnknownFieldSet] {
//          override def to(fieldName: String, value: Option[Value]): Either[Throwable, UnknownFieldSet] =
//            // only for test
//            Right(UnknownFieldSet.empty)
//        }

        """MATCH (u:User {name: "hola"}) RETURN u LIMIT 1"""
          .query(ResultMapper.fromFunction(User.apply _))
          .single(driver)
          .map { user =>
            println(user); user
          }
          .handleError(_ => println(none))
      }
      .as(ExitCode.Success)
  }

}
