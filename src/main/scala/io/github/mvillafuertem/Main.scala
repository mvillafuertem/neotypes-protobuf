package io.github.mvillafuertem

import cats.effect.{ ExitCode, IO, IOApp }
import cats.syntax.option.none
import neotypes.GraphDatabase
import neotypes.cats.effect.implicits._
import neotypes.generic.implicits._
import neotypes.mappers.ResultMapper
import neotypes.syntax.all._
import org.neo4j.driver.AuthTokens
import scalapb.UnknownFieldSet

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    @scala.annotation.unused
    implicit val valueMapper: ResultMapper[UnknownFieldSet] = ResultMapper.fromMatch { case _ =>
      // only for test
      println("UnknownFieldSet")
      UnknownFieldSet.empty
    }

    GraphDatabase
      .asyncDriver[IO](
        "neo4j://localhost:30872",
        AuthTokens.basic("neo4j", "accounttest")
      )
      .use { driver =>
        """MATCH (user:User {name: "Manolo"}) RETURN user, "null" LIMIT 1"""
          .query(ResultMapper.coproductDerive[SimpleADT])
          .single(driver)
          .map { user =>
            println(user); user
          }
          .handleError(e => System.err.println(none)) *>
          """MATCH (user:User {name: "Manolo"})-[r:IsAdmin]-(admin:Admin) RETURN user, r, admin LIMIT 1"""
            .query(ResultMapper.coproductDerive[SimpleADT])
            // .query(nodeRelationshipNodeResultMapper)
            .single(driver)
            .map { user =>
              println(user); user
            }
            .handleError(e => System.err.println(none))
      }
      .as(ExitCode.Success)
  }

}
