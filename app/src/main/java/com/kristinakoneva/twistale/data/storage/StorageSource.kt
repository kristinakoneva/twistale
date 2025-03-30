package com.kristinakoneva.twistale.data.storage

interface StorageSource {

    suspend fun uploadImage(image: ByteArray, imageName: String): String

    suspend fun deleteAllImagesForGame(gameId: Int)
}
