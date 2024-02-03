package com.study.usecase

import io.kotest.common.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FetchUserDataTest {

    @Test
    fun `should construct user`() = runBlocking {
        // given
        val repo = FakeUserDataRepository()
        val useCase = FetchUserUseCase(repo)

        // when
        val result = useCase.fetchUserData()

        // then
        val expectedUser = User(
            name = "Ben",
            friends = listOf(Friend("some-friend-id-1")),
            profile = Profile("Example Description")
        )

        Assertions.assertEquals(expectedUser, result)
    }
}

class FakeUserDataRepository : UserDataRepository {
    override fun getName(): String = "Ben"

    override fun getFriends(): List<Friend> = listOf(Friend("some-friend-id-1"))

    override fun getProfile(): Profile = Profile("Example Description")
}
