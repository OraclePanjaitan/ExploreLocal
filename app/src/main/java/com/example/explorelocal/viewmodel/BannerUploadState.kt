package com.example.explorelocal.viewmodel

sealed class BannerUploadState {
    object Idle : BannerUploadState()
    object Uploading : BannerUploadState()
    data class Success(val url: String) : BannerUploadState()
    data class Error(val message: String) : BannerUploadState()
}
