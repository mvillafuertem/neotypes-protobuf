package io.github.mvillafuertem

import io.circe.Json
import io.circe.syntax._
import io.github.mvillafuertem.admin.Admin
import io.github.mvillafuertem.relationship.{ Relationship, RelationshipType }
import io.github.mvillafuertem.user.User
import neotypes.mappers.ResultMapper
import neotypes.model.types
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

  @scala.annotation.unused
  implicit val generatedMessage: ResultMapper[GeneratedMessage] = ResultMapper.fromMatch {
    case value: types.Node if value.hasLabel(User.messageCompanion.scalaDescriptor.name) =>
      val json: Json = value.properties.view.mapValues(map).toMap.asJson
      JsonFormat.fromJson[User](json)

    case value: types.Node if value.hasLabel(Admin.messageCompanion.scalaDescriptor.name) =>
      val json: Json = value.properties.view.mapValues(map).toMap.asJson
      JsonFormat.fromJson[Admin](json)

    case types.Relationship(_, relationshipType, _, _, _) if relationshipType.equalsIgnoreCase(RelationshipType.IsAdmin.name.toLowerCase) =>
      Relationship()

    case types.Value.NullValue =>
      com.google.protobuf.struct.Value()
  }

  final case class SingleNode(node: GeneratedMessage) extends SimpleADT

  object SingleNode {

    implicit val singleNodeResultMapper: ResultMapper[SimpleADT] = ResultMapper.fromMatch { case value =>
      generatedMessage.decode(value).map(SingleNode(_))
    }
  }

  final case class NodeRelationshipNode(startNode: GeneratedMessage, relationship: GeneratedMessage, endNode: GeneratedMessage) extends SimpleADT

  object NodeRelationshipNode {
    implicit val nodeRelationshipNodeResultMapper: ResultMapper[SimpleADT] =
      ResultMapper.fromFunction[SimpleADT, GeneratedMessage, GeneratedMessage, GeneratedMessage](NodeRelationshipNode.apply)(
        generatedMessage,
        generatedMessage,
        generatedMessage
      )

  }
}
