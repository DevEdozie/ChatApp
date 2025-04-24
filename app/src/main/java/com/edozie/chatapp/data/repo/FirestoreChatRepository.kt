package com.edozie.chatapp.data.repo

import com.edozie.chatapp.data.room.AppDatabase
import com.edozie.chatapp.data.room.ChatThreadEntity
import com.edozie.chatapp.data.room.MessageEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val db: AppDatabase,
    private val auth: FirebaseAuth
) : ChatRepository {

    private val threadsCol = firestore.collection("chats")
    private val usersCol = firestore.collection("users")

    @OptIn(DelicateCoroutinesApi::class)
    override fun observeThreads(): Flow<List<ChatThreadEntity>> {
        // Listen only to chats where *your* UID is a participant
        threadsCol
            .whereArrayContains("participants", auth.uid!!)
            .addSnapshotListener { snap, _ ->
                snap?.let {
                    val meEmail = auth.currentUser!!.email!!
                    val meUid = auth.uid!!
                    val entities = it.documents.map { doc ->
                        val parts = doc.get("participants") as List<String>
                        // find the *other* user's UID
                        // val otherUid = parts.first { it != auth.uid!! }
                        val otherUid = parts.first { it != meUid }
                        // pull the email list you stored
                        val emails = doc.get("participantEmails") as List<String>
//                        val otherEmail = emails.first { it != auth.currentUser!!.email }
                        val otherEmail = emails.first { it != meEmail }




                        ChatThreadEntity(
                            id = doc.id,
                            otherUserEmail = otherEmail,
                            otherUserId = otherUid,
                            lastMessage = doc.getString("lastMessage"),
                            timesStamp = doc.getLong("timestamp") ?: 0L
                        )
                    }
                    // write to Room…
                    GlobalScope.launch { db.chatThreadDao().upsertThreads(entities) }
                }
            }
        return db.chatThreadDao().getAllThreads()
    }


//    @OptIn(DelicateCoroutinesApi::class)
//    override fun observeThreads(): Flow<List<ChatThreadEntity>> {
//        // 1. Listen Firestore → write to Room
//        firestore.collection("chats")
//            .whereArrayContains("participants", auth.uid!!)
//            .addSnapshotListener { snap, _ ->
//                snap?.let {
//                    // Convert each Firestore document into a ChatThreadEntity
//                    val entities = it.documents.map { doc ->
//                        ChatThreadEntity(
//                            id = doc.id,
//                            otherUserEmail = (doc.get("participants") as List<String>)
//                                .first { it != auth.currentUser!!.email },
//                            lastMessage = doc.getString("lastMessage"),
//                            timesStamp = doc.getLong("timestamp") ?: 0L
//                        )
//                    }
//                    // Write (upsert) them into Room on a background coroutine
//                    GlobalScope.launch { db.chatThreadDao().upsertThreads(entities) }
//                }
//            }
//        // 2) Return the Room Flow so callers get data from local DB + updates
//        return db.chatThreadDao().getAllThreads()
//    }

    override fun observeMessages(chatId: String): Flow<List<MessageEntity>> = callbackFlow {
        val sub = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)  // propagate errors if you like
                    return@addSnapshotListener
                }
                val msgs = snap?.documents.orEmpty().map { doc ->
                    MessageEntity(
                        id = doc.id,
                        chatId = chatId,
                        senderId = doc.getString("senderId")!!,
                        text = doc.getString("text")!!,
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                // 1) keep your offline cache up to date
                launch { db.messageDao().upsertMessages(msgs) }
                // 2) immediately push to the UI
                trySend(msgs)
            }

        awaitClose { sub.remove() }
    }


//    @OptIn(DelicateCoroutinesApi::class)
//    override fun observeMessages(chatId: String): Flow<List<MessageEntity>> {
//        firestore.collection("chats").document(chatId)
//            .collection("messages")
//            .orderBy("timestamp", Query.Direction.ASCENDING)
//            .addSnapshotListener { snap,_ ->
////                if (err != null) {
////                    close(err)
////                    return@addSnapshotListener
////                }
//                snap?.let {
//                    val msgs = it.documents.map { doc ->
//                        MessageEntity(
//                            id = doc.id,
//                            chatId = chatId,
//                            senderId = doc.getString("senderId")!!,
//                            text = doc.getString("text")!!,
//                            timestamp = doc.getLong("timestamp") ?: 0L
//                        )
//                    }
//                    GlobalScope.launch { db.messageDao().upsertMessages(msgs) }
//                }
//            }
//        return db.messageDao().getMessagesFor(chatId)
//    }


//    override suspend fun createChatWith(otherEmail: String): String {
//        // 1) Look up the other user's UID from your 'users' collection
//        val userSnap = usersCol.whereEqualTo("email", otherEmail).get().await()
//        require(userSnap.documents.isNotEmpty()) { "User not found" }
//        val otherUid = userSnap.documents.first().id
//
//        // 2) Build your participants list of UIDs
//        val meUid = auth.uid!!
//        val data = mapOf(
//            "participants" to listOf(meUid, otherUid),
//            "timestamp" to System.currentTimeMillis(),
//            "lastMessage" to null
//        )
//        val ref = threadsCol.add(data).await()
//        val chatId = ref.id
//
//        // 3) Upsert into Room so the creator sees it immediately
//        val entity = ChatThreadEntity(
//            id = chatId,
//            otherUserEmail = otherEmail,
//            lastMessage = null,
//            timesStamp = System.currentTimeMillis()
//        )
//        db.chatThreadDao().upsertThreads(listOf(entity))
//        return chatId
//    }

    override suspend fun createChatWith(email: String): Pair<String, String> {
        val meUid = auth.uid!!
        val myEmail = auth.currentUser!!.email

        // 1) Lookup the other user’s UID
        val userSnap = usersCol
            .whereEqualTo("email", email)
            .get()
            .await()
        require(userSnap.documents.isNotEmpty()) { "User not found" }
        val otherUid = userSnap.documents.first().id


        // 2) See if we already have a chat between these two UIDs
        val existing = threadsCol
            .whereArrayContains("participants", meUid)
            .get()
            .await()
            .documents
            .firstOrNull { doc ->
                val parts = doc.get("participants") as List<String>
                parts.contains(otherUid)
            }
        if (existing != null) {
            // reuse the existing chat
            return existing.id to otherUid
        }

        // 3) No existing chat → create a fresh one
        val now = System.currentTimeMillis()
        val data = mapOf(
            "participants" to listOf(meUid, otherUid),
            "participantEmails" to listOf(myEmail, email),
            "timestamp" to now,
            "lastMessage" to null
        )
        val ref = threadsCol.add(data).await()
        val chatId = ref.id

        // 4) Upsert into Room immediately so UI sees it right away
        db.chatThreadDao().upsertThreads(
            listOf(
                ChatThreadEntity(
                    id = chatId,
                    otherUserEmail = email,
                    otherUserId = otherUid,
                    lastMessage = null,
                    timesStamp = now
                )
            )
        )
        return chatId to otherUid
    }


//    override suspend fun createChatWith(email: String): String {
//        // ensure user exists
//        val userSnap = usersCol.whereEqualTo("email", email).get().await()
//        require(userSnap.documents.isNotEmpty()) { "User not found" }
//
//        // create chat doc
//        val data = mapOf(
//            "participants" to listOf(auth.currentUser!!.email!!, email),
//            "timestamp" to System.currentTimeMillis(),
//            "lastMessage" to null
//        )
//        val ref = threadsCol.add(data).await()
//        val chatId = ref.id
//
//        val entity = ChatThreadEntity(
//            id = chatId,
//            otherUserEmail = email,
//            lastMessage = null,
//            timesStamp = System.currentTimeMillis()
//        )
//        db.chatThreadDao().upsertThreads(listOf(entity))
//        return chatId
//    }

    override suspend fun sendMessage(chatId: String, text: String) {
        val now = System.currentTimeMillis()
        // append message
        threadsCol.document(chatId)
            .collection("messages")
            .add(
                mapOf(
                    "senderId" to auth.uid,
                    "text" to text,
                    "timestamp" to now
                )
            ).await()

        // update lastMessage & timestamp
        threadsCol.document(chatId)
            .update("lastMessage", text, "timestamp", now).await()

        // 2) Immediate Room upsert for the thread

        //    so that messages always have a parent:
//        val participants = threadsCol.document(chatId)
//            .get().await().get("participants") as List<String>
//        val otherEmail = participants.first { it != auth.currentUser!!.email }
        // 3) Read back the participants and participantEmails
        val chatDoc = threadsCol.document(chatId).get().await()
        val parts = chatDoc.get("participants") as List<String>
        val emails = chatDoc.get("participantEmails") as List<String>
        val meUid = auth.uid!!
        val otherUid = parts.first { it != meUid }
        val myEmail = auth.currentUser!!.email!!
        val otherEmail = emails.first { it != myEmail }

        val entity = ChatThreadEntity(
            id = chatId,
            otherUserEmail = otherEmail,
            otherUserId = otherUid,
            lastMessage = text,
            timesStamp = now
        )
        db.chatThreadDao().upsertThreads(listOf(entity))
    }

    override fun setTyping(chatId: String, isTyping: Boolean) {
        threadsCol.document(chatId)
            .collection("typing")
            .document(auth.uid!!)
            .set(mapOf("isTyping" to isTyping))
    }

    override fun observeTyping(chatId: String, userId: String): Flow<Boolean> = callbackFlow {
        val sub = threadsCol.document(chatId)
            .collection("typing")
            .document(userId)
            .addSnapshotListener { snap, _ ->
                snap?.getBoolean("isTyping")?.let { trySend(it) }
            }
        awaitClose { sub.remove() }
    }


}