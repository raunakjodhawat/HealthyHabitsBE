package com.raunakjodhawat.models

object UtilRegistry {
  trait Command

  final case class ActionPerformed(description: String)
}
