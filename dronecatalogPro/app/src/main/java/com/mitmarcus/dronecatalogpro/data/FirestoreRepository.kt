package com.mitmarcus.dronecatalogpro.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Builds and flying spots, stored per user:
//   users/{uid}/builds/{buildId}  { name, parts: { "<partId>": quantity }, createdAt }
//   users/{uid}/spots/{spotId}    { name, latitude, longitude, createdAt }
object FirestoreRepository {
    private val db get() = FirebaseFirestore.getInstance()

    private fun userDoc() =
        db.collection("users").document(AuthRepository.currentUser?.uid ?: error("Not signed in"))

    private fun builds() = userDoc().collection("builds")
    private fun spots() = userDoc().collection("spots")

    // Builds

    fun buildsFlow(): Flow<List<DroneBuild>> =
        queryFlow(builds().orderBy("createdAt", Query.Direction.DESCENDING)) { it.toBuild() }

    fun buildFlow(buildId: String): Flow<DroneBuild?> =
        documentFlow(buildId, isBuild = true) { it.toBuild() }

    suspend fun createBuild(name: String): String {
        val doc = builds().add(
            mapOf(
                "name" to name,
                "parts" to emptyMap<String, Int>(),
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
        return doc.id
    }

    suspend fun addPart(buildId: String, partId: Int) {
        builds().document(buildId).update("parts.$partId", FieldValue.increment(1)).await()
    }

    // Lowers the quantity by one, or removes the part when it was the last one
    suspend fun removePart(buildId: String, partId: Int, currentQuantity: Int) {
        val value = if (currentQuantity <= 1) FieldValue.delete() else FieldValue.increment(-1)
        builds().document(buildId).update("parts.$partId", value).await()
    }

    suspend fun deleteBuild(buildId: String) {
        builds().document(buildId).delete().await()
    }

    // Flying spots

    fun spotsFlow(): Flow<List<FlyingSpot>> =
        queryFlow(spots().orderBy("createdAt", Query.Direction.DESCENDING)) { it.toSpot() }

    fun spotFlow(spotId: String): Flow<FlyingSpot?> =
        documentFlow(spotId, isBuild = false) { it.toSpot() }

    suspend fun addSpot(name: String, latitude: Double, longitude: Double) {
        spots().add(
            mapOf(
                "name" to name,
                "latitude" to latitude,
                "longitude" to longitude,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun deleteSpot(spotId: String) {
        spots().document(spotId).delete().await()
    }

    // Snapshot listeners wrapped as flows, so screens update when Firestore changes

    private fun <T> queryFlow(query: Query, map: (DocumentSnapshot) -> T): Flow<List<T>> =
        callbackFlow {
            val registration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.map(map) ?: emptyList())
            }
            awaitClose { registration.remove() }
        }

    private fun <T> documentFlow(id: String, isBuild: Boolean, map: (DocumentSnapshot) -> T): Flow<T?> =
        callbackFlow {
            val collection = if (isBuild) builds() else spots()
            val registration = collection.document(id).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(if (snapshot != null && snapshot.exists()) map(snapshot) else null)
            }
            awaitClose { registration.remove() }
        }

    private fun DocumentSnapshot.toBuild(): DroneBuild {
        val rawParts = get("parts") as? Map<*, *> ?: emptyMap<String, Long>()
        val parts = rawParts.mapNotNull { (key, value) ->
            val partId = (key as? String)?.toIntOrNull() ?: return@mapNotNull null
            val quantity = (value as? Number)?.toInt() ?: return@mapNotNull null
            partId to quantity
        }.toMap()
        return DroneBuild(id = id, name = getString("name") ?: "Unnamed build", parts = parts)
    }

    private fun DocumentSnapshot.toSpot() = FlyingSpot(
        id = id,
        name = getString("name") ?: "Unnamed spot",
        latitude = getDouble("latitude") ?: 0.0,
        longitude = getDouble("longitude") ?: 0.0
    )
}
