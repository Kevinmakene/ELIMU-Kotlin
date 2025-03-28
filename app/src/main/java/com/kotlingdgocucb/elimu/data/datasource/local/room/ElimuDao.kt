package com.kotlingdgocucb.elimu.data.datasource.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Mentor
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Review
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.ReviewCreate
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Video

@Dao
interface ElimuDao {

 // ===== User =====
// @Insert(onConflict = OnConflictStrategy.REPLACE)
// suspend fun insertUser(user: User): Long
//
// @Insert(onConflict = OnConflictStrategy.REPLACE)
// suspend fun insertUsers(users: List<User>): List<Long>
//
// @Update
// suspend fun updateUser(user: User)
//
// @Delete
// suspend fun deleteUser(user: User)
//
// @Query("SELECT * FROM user")
// suspend fun getAllUsers(): List<User>
//
// @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
// suspend fun getUserByEmail(email: String): User?

 // ===== Mentor =====
 @Insert(onConflict = OnConflictStrategy.IGNORE)
 suspend fun insertMentor(mentor: Mentor): Long

 @Insert(onConflict = OnConflictStrategy.IGNORE)
 suspend fun insertMentors(mentors: List<Mentor>): List<Long>

 @Update
 suspend fun updateMentor(mentor: Mentor)

 @Delete
 suspend fun deleteMentor(mentor: Mentor)

 @Query("SELECT * FROM mentor")
 suspend fun getAllMentors(): List<Mentor>

 @Query("SELECT * FROM mentor WHERE email = :email LIMIT 1")
 suspend fun getMentorByEmail(email: String): Mentor?

 // ===== Video =====
 @Insert(onConflict = OnConflictStrategy.REPLACE)
 suspend fun insertVideo(video: Video): Long

 @Insert(onConflict = OnConflictStrategy.REPLACE)
 suspend fun insertVideos(videos: List<Video>): List<Long>

 @Update
 suspend fun updateVideo(video: Video)

 @Delete
 suspend fun deleteVideo(video: Video)

 @Query("SELECT * FROM video")
 suspend fun getAllVideos(): List<Video>

 @Query("SELECT * FROM video WHERE id = :id LIMIT 1")
 suspend fun getVideoById(id: Int): Video?

 // ===== Review =====
 @Insert(onConflict = OnConflictStrategy.IGNORE)
 suspend fun insertReview(review: Review): Long

 @Insert(onConflict = OnConflictStrategy.IGNORE)
 suspend fun insertReviews(reviews: List<Review>): List<Long>

 @Update
 suspend fun updateReview(review: Review)

 @Delete
 suspend fun deleteReview(review: Review)

 @Query("SELECT * FROM review")
 suspend fun getAllReviews(): List<Review>

 @Query("SELECT * FROM review WHERE videoId = :videoId")
 suspend fun getReviewsByVideoId(videoId: Int): List<Review>

 // ===== ReviewCreate =====
 @Insert(onConflict = OnConflictStrategy.IGNORE)
 suspend fun insertReviewCreate(reviewCreate: ReviewCreate): Long

 @Insert(onConflict = OnConflictStrategy.IGNORE)
 suspend fun insertReviewCreates(reviewCreates: List<ReviewCreate>): List<Long>

 @Update
 suspend fun updateReviewCreate(reviewCreate: ReviewCreate)

 @Delete
 suspend fun deleteReviewCreate(reviewCreate: ReviewCreate)

 @Query("SELECT * FROM review_create")
 suspend fun getAllReviewCreates(): List<ReviewCreate>
}
