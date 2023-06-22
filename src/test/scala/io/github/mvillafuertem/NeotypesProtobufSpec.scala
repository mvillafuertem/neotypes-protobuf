package io.github.mvillafuertem

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.option._
import io.github.mvillafuertem.NeotypesProtobufSpec.testResource
import io.github.mvillafuertem.SimpleADT.{ NodeRelationshipNode, SingleNode }
import io.github.mvillafuertem.admin.Admin
import io.github.mvillafuertem.relationship.Relationship
import io.github.mvillafuertem.user.{ Info, User }
import neotypes.GraphDatabase
import neotypes.cats.effect.implicits._
import neotypes.generic.implicits.deriveCaseClassProductMap
import neotypes.mappers.ResultMapper
import neotypes.model.types.Value
import neotypes.model.types.Value.NullValue
import neotypes.syntax.all._
import org.apache.commons.io.FileUtils
import org.neo4j.configuration.GraphDatabaseSettings
import org.neo4j.configuration.connectors.BoltConnector
import org.neo4j.dbms.api.{ DatabaseManagementService, DatabaseManagementServiceBuilder }
import org.neo4j.graphdb.GraphDatabaseService
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import scalapb.{ GeneratedMessage, UnknownFieldSet }

import java.nio.file.Path

final class NeotypesProtobufSpec extends AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  implicit val unknownFieldSetMapper: ResultMapper[UnknownFieldSet] = ResultMapper.fromMatch { case NullValue =>
    UnknownFieldSet.empty
  }

  implicit val value1: ResultMapper[Seq[Info]] = ResultMapper
    .list(
      ResultMapper.fromMatch {
        case Value.Str(value) => Info.of(value, value.some)
        case NullValue        => Info.defaultInstance
      }
    )
    .or(
      ResultMapper.fromMatch { case NullValue =>
        Seq.empty[Info]
      }
    )

  "NeotypesProtobuf" should {

    "WIP" in {

      val query = """MATCH (user:User { name: "Manolo" }) RETURN user LIMIT 1"""

      val actual: GeneratedMessage = NeotypesProtobufSpec.execute(query, ResultMapper.productDerive[User](deriveCaseClassProductMap))

      actual shouldBe User(name = "Manolo".some, surname = "Del Bombo".some, "monolo-bombo".some)

    }

    "return an `User` node" in {

      val query = """MATCH (user:User { name: "Manolo" }) RETURN user LIMIT 1"""

      val actual: GeneratedMessage = NeotypesProtobufSpec.execute(query, SimpleADT.generatedMessage)

      actual shouldBe User(name = "Manolo".some, surname = "Del Bombo".some, "monolo-bombo".some)

    }

    "return an `Admin` node" in {

      val query = """MATCH (admin:Admin) RETURN admin LIMIT 1"""

      val actual: GeneratedMessage = NeotypesProtobufSpec.execute(query, SimpleADT.generatedMessage)

      actual shouldBe Admin.of(admin = true.some)

    }

    "return a sequence of `GeneratedMessage`" in {

      val query =
        """
          |MATCH (user:User)-[relationship:IsAdmin]-(admin:Admin) 
          |RETURN user, relationship, admin 
          |LIMIT 1
          |""".stripMargin

      val actual: Seq[GeneratedMessage] = NeotypesProtobufSpec.execute(query, ResultMapper.list(SimpleADT.generatedMessage))

      actual shouldBe Seq(
        User(name = "Manolo".some, surname = "Del Bombo".some, "monolo-bombo".some),
        Relationship.of(),
        Admin.of(admin = true.some)
      )

    }

    "return SingleNode which is a coproduct of `SimpleADT`" in {

      val query = """MATCH (user:User) RETURN user LIMIT 1""".stripMargin

      val actual: SimpleADT =
        NeotypesProtobufSpec.execute(query, SimpleADT.NodeRelationshipNode.nodeRelationshipNodeResultMapper.or(SimpleADT.SingleNode.singleNodeResultMapper))

      actual shouldBe SingleNode(User(name = "Manolo".some, surname = "Del Bombo".some, "monolo-bombo".some))

    }

    "return NodeRelationshipNode which is a coproduct of `SimpleADT`" in {

      val query =
        """
          |MATCH (user:User)-[relationship:IsAdmin]-(admin:Admin) 
          |RETURN user, relationship, admin 
          |LIMIT 1
          |""".stripMargin

      val actual: SimpleADT =
        NeotypesProtobufSpec.execute(query, SimpleADT.NodeRelationshipNode.nodeRelationshipNodeResultMapper.or(SimpleADT.SingleNode.singleNodeResultMapper))

      actual shouldBe NodeRelationshipNode(
        User(name = "Manolo".some, surname = "Del Bombo".some, "monolo-bombo".some),
        Relationship.of(),
        Admin.of(admin = true.some)
      )

    }

  }

  override protected def beforeAll(): Unit = {
    val graphDatabaseService: GraphDatabaseService = NeotypesProtobufSpec.databaseManagementService
      .database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME)

    graphDatabaseService
      .executeTransactionally("""|CREATE(user:`User` {name: 'Manolo', surname: 'Del Bombo', username: 'monolo-bombo', info: 'more information'})
                                 |CREATE(admin:`Admin` {admin: true})
                                 |CREATE(user)-[:IsAdmin]->(admin);
                                 |""".stripMargin)
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    NeotypesProtobufSpec.databaseManagementService.shutdown() shouldBe ()
    FileUtils.deleteDirectory(testResource.resolve("data").toFile) shouldBe ()
  }

  super.afterAll()

}

object NeotypesProtobufSpec {

  val testResource: Path                                   = Path.of("src/test/resources")
  val databaseManagementService: DatabaseManagementService = new DatabaseManagementServiceBuilder(testResource)
    // .setConfig(GraphDatabaseSettings.read_only_database_default, Boolean.box(true))
    .setConfig(BoltConnector.enabled, Boolean.box(true))
    .build()

  def execute[T](query: String, mapper: ResultMapper[T]) = GraphDatabase
    .asyncDriver[IO]("neo4j://localhost:7687")
    .use(query.query(mapper).single(_))
    .onError(IO.println)
    .unsafeRunSync()

}
