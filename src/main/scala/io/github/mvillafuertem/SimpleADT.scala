package io.github.mvillafuertem

import io.circe.Json
import io.circe.syntax._
import io.github.mvillafuertem.admin.Admin
import io.github.mvillafuertem.relationship.{ Relationship, RelationshipType }
import io.github.mvillafuertem.user.User
import neotypes.mappers.ResultMapper
import neotypes.model.types
import neotypes.model.types.{ NeoList, NeoType }
import scalapb.GeneratedMessage
import scalapb_circe.JsonFormat

sealed trait SimpleADT extends Product with Serializable

object SimpleADT {

  def map(value: neotypes.model.types.NeoType): Json =
    value match {
      case neotypes.model.types.Value.Bool(value)       => Json.fromBoolean(value)
      case neotypes.model.types.Value.Integer(value)    => Json.fromLong(value)
      case neotypes.model.types.Value.Decimal(value)    => Json.fromDoubleOrString(value)
      case neotypes.model.types.Value.Str(value)        => Json.fromString(value)
      case neotypes.model.types.Value.ListValue(values) => Json.fromValues(values.map(map))
      case neotypes.model.types.NeoMap(values)          => Json.fromFields(values.view.mapValues(map))
      case _                                            => Json.fromString(value.toString)
    }

  implicit val nothing: ResultMapper[Unit] = ResultMapper.fromMatch { case _ =>
    ()
  }

  @scala.annotation.unused
  implicit val generatedMessage: ResultMapper[GeneratedMessage] = ResultMapper.fromMatch {
    case value: types.Node if value.hasLabel("user") =>
      println("user")
      val json: Json = value.properties.view.mapValues(map).toMap.asJson
      val user: User = JsonFormat.fromJson[User](json)
      println(user)
      user

    case value: types.Node if value.hasLabel("admin") =>
      println("admin")
      val json: Json = value.properties.view.mapValues(map).toMap.asJson
      val admin      = JsonFormat.fromJson[Admin](json)
      println(admin)
      admin

    case types.Relationship(_, relationshipType, properties, _, _) if relationshipType.equalsIgnoreCase(RelationshipType.IsAdmin.name.toLowerCase) =>
      println("admin")
      println(properties)
      Relationship()

    case types.Value.NullValue =>
      println("null")
      com.google.protobuf.empty.Empty()

  }

  final case class SingleNode(node: GeneratedMessage) extends SimpleADT

  object SingleNode {

    implicit val singleNodeResultMapper: ResultMapper[SingleNode] = ResultMapper.fromMatch { case value =>
      generatedMessage.decode(value).map(SingleNode(_))
    }
  }

  final case class NodeRelationshipNode(startNode: GeneratedMessage, relationship: GeneratedMessage, endNode: GeneratedMessage) extends SimpleADT

  object NodeRelationshipNode {
    implicit val nodeRelationshipNodeResultMapper: ResultMapper[NodeRelationshipNode] =
      ResultMapper.fromFunction(NodeRelationshipNode.apply _)(generatedMessage, generatedMessage, generatedMessage)

  }
}
