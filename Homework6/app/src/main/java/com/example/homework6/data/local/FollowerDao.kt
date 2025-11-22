package com.example.homework6.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowerDao {
    @Query("SELECT * FROM followers")
    fun getAllFollowers(): Flow<List<FollowerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollower(follower: FollowerEntity)

    @Query("DELETE FROM followers")
    suspend fun deleteAllFollowers()

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<ProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Update
    suspend fun updateFollower(follower: FollowerEntity)
}