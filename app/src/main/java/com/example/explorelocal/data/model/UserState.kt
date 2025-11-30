package com.example.explorelocal.data.model

sealed class UserState {
    object Loading: UserState()
    data class Success(val message: String): UserState()
    data class Error(val message: String): UserState()
}

// Once user register will get access token. Access token will have expiration date and time