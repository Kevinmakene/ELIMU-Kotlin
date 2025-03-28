package com.kotlingdgocucb.elimu.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kotlingdgocucb.elimu.data.datasource.local.room.ElimuDao
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Mentor

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MentorRepositoryImpl(
    private val elimuDao: ElimuDao
) : MentorRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun getMentors(): LiveData<List<Mentor>> = liveData {
        // 1. Émission initiale des données locales
        val localMentors = elimuDao.getAllMentors()
        emit(localMentors)
        Log.d("ELIMUDEBUG", "Mentors locaux émis: ${localMentors.size}")

        // 2. Authentification anonyme si nécessaire
        if (firebaseAuth.currentUser == null) {
            Log.d("ELIMUDEBUG", "Aucun utilisateur authentifié. Tentative d'authentification anonyme.")
            suspendCancellableCoroutine<Unit> { cont ->
                firebaseAuth.signInAnonymously().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ELIMUDEBUG", "Authentification anonyme réussie. UID: ${firebaseAuth.currentUser?.uid}")
                    } else {
                        Log.e("ELIMUDEBUG", "Erreur d'authentification anonyme: ${task.exception?.message}", task.exception)
                    }
                    cont.resume(Unit)
                }
            }
        } else {
            Log.d("ELIMUDEBUG", "Utilisateur déjà authentifié: ${firebaseAuth.currentUser?.uid}")
        }

        // 3. Récupération des mentors depuis Firebase
        val mentorList = suspendCancellableCoroutine<MutableList<Mentor>> { continuation ->
            val ref = firebaseDatabase.getReference("mentor")
            Log.d("ELIMUDEBUG", "Accès à la référence 'mentor' dans FirebaseDatabase.")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ELIMUDEBUG", "Snapshot reçu avec ${snapshot.childrenCount} enfants.")
                    val list = snapshot.children.mapNotNull { child ->
                        val mentor = child.getValue(Mentor::class.java)
                        Log.d("ELIMUDEBUG", "Lecture du mentor: $mentor")
                        mentor
                    }.toMutableList()
                    if (continuation.isActive) {
                        Log.d("ELIMUDEBUG", "Mentors récupérés avec succès: ${list.size} mentors trouvés")
                        continuation.resume(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ELIMUDEBUG", "Erreur lors de la récupération des mentors: ${error.message} (Code: ${error.code})")
                    if (continuation.isActive) {
                        continuation.resume(mutableListOf())
                    }
                }
            })
        }

        // 4. Mise à jour de la base de données locale avec les données récupérées
        if (mentorList.isNotEmpty()) {
            // On insère en utilisant la stratégie REPLACE (définie dans le DAO)
            elimuDao.insertMentors(mentorList)
            Log.d("ELIMUDEBUG", "Mentors insérés en base locale avec succès.")
        } else {
            Log.d("ELIMUDEBUG", "Aucun mentor récupéré depuis Firebase.")
        }

        // 5. Émission des données locales mises à jour
        val updatedMentors = elimuDao.getAllMentors()
        emit(updatedMentors)
        Log.d("ELIMUDEBUG", "Mentors locaux mis à jour émis: ${updatedMentors.size}")
    }
}
