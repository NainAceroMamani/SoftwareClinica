package nain.com.io.response

import nain.com.model.User

data class LoginResponse (val success: Boolean, val user: User, val jwt: String, val message: String)