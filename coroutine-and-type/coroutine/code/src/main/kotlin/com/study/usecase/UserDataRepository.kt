package com.study.usecase

interface UserDataRepository {
    fun getName(): String
    fun getFriends(): List<Friend>
    fun getProfile(): Profile
}

data class User(
    val name: String,
    val friends: List<Friend>,
    val profile: Profile
)

data class Friend(
    val name: String
)

data class Profile(
    val name: String
)
