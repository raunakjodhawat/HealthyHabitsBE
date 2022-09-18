package com.raunakjodhawat.models

import com.raunakjodhawat.utilties.token.TokenUtility

// refrences: https://developers.facebook.com/docs/graph-api/reference/v14.0/debug_token
final case class DebugTokenError(
    code: Int,
    message: String
)

final case class DebugTokenData(
    error: Option[DebugTokenError],
    is_valid: Option[Boolean],
    user_id: Option[String]
)
final case class DebugToken(data: DebugTokenData)
object FBRegistry {
  def getSecretKey(
      userId: Option[String],
      access_token: Option[String]
  ): Option[String] = TokenUtility.generateSecretKey(userId, access_token)
}
