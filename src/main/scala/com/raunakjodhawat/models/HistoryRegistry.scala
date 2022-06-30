package com.raunakjodhawat.models

import akka.actor.typed.ActorRef

import java.sql.Date

final case class history(
    history_id: Int,
    user_id: Int,
    habit_id: Int,
    spend_time: String,
    record_date: Date
)

object HistoryRegistry {

  sealed trait Command

  final case class addHistory(history: history, replyTo: ActorRef[history])
      extends Command

  final case class seeAllHistory(replyTo: ActorRef[history]) extends Command
}
