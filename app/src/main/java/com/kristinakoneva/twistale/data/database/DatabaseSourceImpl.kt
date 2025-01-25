package com.kristinakoneva.twistale.data.database

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class DatabaseSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DatabaseSource {
    companion object {
        private const val COLLECTION_USERS = "users"
        private const val FIELD_NAME = "name"
    }

    override suspend fun addNewUser(userId: String, name: String) {
        firestore.collection(COLLECTION_USERS).document(userId).get().addOnSuccessListener {
            if (!it.exists()) {
                firestore.collection(COLLECTION_USERS).document(userId).set(hashMapOf(FIELD_NAME to name))
            }
        }
    }
}
