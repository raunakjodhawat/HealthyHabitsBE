package com.raunakjodhawat.models

final case class EditHistory(
    old_value: Option[Habit],
    new_value: Habit
)
