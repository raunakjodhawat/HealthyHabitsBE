package com.raunakjodhawat.models

object UtilRegistry {
  trait Command

  sealed abstract class ActionPerformed(description: String)
  final case class ActionPerformedWithError(description: String)
      extends ActionPerformed(description)
  final case class ActionPerformedWithSuccess(description: String)
      extends ActionPerformed(description)
}
