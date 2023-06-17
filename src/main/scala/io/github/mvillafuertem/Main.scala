package io.github.mvillafuertem

import cats.effect.{ExitCode, IO, IOApp}
import neotypes.GraphDatabase
import neotypes.cats.effect.implicits._
import neotypes.generic.implicits.deriveSealedTraitCoproductInstances
import neotypes.mappers.ResultMapper
import neotypes.mappers.ResultMapper.CoproductDiscriminatorStrategy
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
        """MATCH (user:User {name: "Manolo"}) RETURN user, null LIMIT 1"""
          .query(ResultMapper.list(SimpleADT.generatedMessage))
          .single(driver)
          .map { user =>
            println("useruseruseruseruseruseruser");
            println(user)
            user
          }
          .handleError(e => System.err.println(e)) // *>
//          """MATCH (user:User {name: "Manolo"})-[r:IsAdmin]-(admin:Admin) RETURN user, r, admin LIMIT 1"""
//            .query(ResultMapper.list(SimpleADT.generatedMessage))
//            // .query(nodeRelationshipNodeResultMapper)
//            .single(driver)
//            .map { user =>
//              user match {
//                case a :: Nil           =>
//                  println("a :: Nil")
//                case a :: b :: Nil      =>
//                  println("a :: b")
//                case a :: b :: c :: Nil =>
//                  println("a :: b :: c")
//                  println(a.companion.scalaDescriptor.name)
//                  println(b.companion.scalaDescriptor.name)
//                  println(c.companion.scalaDescriptor.name)
//                  println(b)
//                  println(c)
//              }
//              println(user); user
//            }
//            .handleError(e => System.err.println(e))
      }
      .as(ExitCode.Success)
  }

}
