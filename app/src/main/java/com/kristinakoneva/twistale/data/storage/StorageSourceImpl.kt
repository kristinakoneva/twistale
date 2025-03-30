package com.kristinakoneva.twistale.data.storage

import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class StorageSourceImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
): StorageSource {

    override suspend fun uploadImage(image: ByteArray, imageName: String): String {
        val storageRef = firebaseStorage.reference.child("images/$imageName")
        val uploadTask = storageRef.putBytes(image)
        uploadTask.await()
        return storageRef.downloadUrl.await().toString()
    }

    override suspend fun deleteAllImagesForGame(gameId: Int) {
        val storageRef = firebaseStorage.reference.child("images")
        val gameData = storageRef.child("$gameId")
        val listResult = gameData.listAll().await()
        for (item in listResult.items) {
            item.delete().await()
        }
    }
}
