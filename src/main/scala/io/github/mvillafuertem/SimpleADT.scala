package io.github.mvillafuertem

import cats.syntax.option._
import io.circe.Json
import io.github.mvillafuertem.admin.Admin
import io.github.mvillafuertem.permission.Permission
import io.github.mvillafuertem.relationship.{Relationship, RelationshipType}
import io.github.mvillafuertem.user.{Info, User}
import neotypes.generic.implicits.deriveCaseClassProductMap
import neotypes.mappers.ResultMapper
import neotypes.model.types
import neotypes.model.types.Value
import scalapb.{GeneratedMessage, UnknownFieldSet}

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

  implicit val valueMapper: ResultMapper[UnknownFieldSet] = ResultMapper.fromMatch { case Value.NullValue =>
    UnknownFieldSet.empty
  }

  implicit val infoMapper: ResultMapper[Seq[Info]] = ResultMapper.fromMatch {
    case Value.NullValue  => Seq.empty[Info]
    case Value.Str(value) => Seq(Info.of(value, value.some))
  }

  @scala.annotation.unused
  implicit val generatedMessage: ResultMapper[GeneratedMessage] = ResultMapper.fromMatch {
    case value: types.Node if value.hasLabel(User.messageCompanion.scalaDescriptor.name)  =>
      ResultMapper.fromFunction(User.apply _).decode(value)
    case value: types.Node if value.hasLabel(Admin.messageCompanion.scalaDescriptor.name) =>
      ResultMapper.fromFunction(Admin.apply _).decode(value)
    case value: types.Node if value.hasLabel(Permission.messageCompanion.scalaDescriptor.name) =>
      ResultMapper.fromFunction(Permission.apply _).decode(value)
    case types.Relationship(_, _, _, _, _)  =>
      Right(Relationship())
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

  final case class NodeRelationshipMiddleNodeRelationshipNode(
    startNode: GeneratedMessage,
    fromRelationship: GeneratedMessage,
    middleNode: GeneratedMessage,
    toRelationship: GeneratedMessage,
    endNode: GeneratedMessage
  ) extends SimpleADT

  object NodeRelationshipMiddleNodeRelationshipNode {
    implicit val nodeRelationshipMiddleNodeRelationshipNodeResultMapper: ResultMapper[SimpleADT] =
      ResultMapper.fromFunction[SimpleADT, GeneratedMessage, GeneratedMessage, GeneratedMessage, GeneratedMessage, GeneratedMessage](
        NodeRelationshipMiddleNodeRelationshipNode.apply
      )(
        generatedMessage,
        generatedMessage,
        generatedMessage,
        generatedMessage,
        generatedMessage
      )

  }
}
