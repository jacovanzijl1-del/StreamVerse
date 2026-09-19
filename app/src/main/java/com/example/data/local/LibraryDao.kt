package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library_items WHERE isFavorite = 1 ORDER BY lastWatchedTimestamp DESC")
    fun getFavorites(): Flow<List<LibraryMediaEntity>>

    @Query("SELECT * FROM library_items WHERE isWatchLater = 1 ORDER BY lastWatchedTimestamp DESC")
    fun getWatchLater(): Flow<List<LibraryMediaEntity>>

    @Query("SELECT * FROM library_items WHERE progressMs > 0 AND isFinished = 0 ORDER BY lastWatchedTimestamp DESC")
    fun getContinueWatching(): Flow<List<LibraryMediaEntity>>

    @Query("SELECT * FROM library_items WHERE lastWatchedTimestamp > 0 ORDER BY lastWatchedTimestamp DESC LIMIT 50")
    fun getWatchHistory(): Flow<List<LibraryMediaEntity>>

    @Query("SELECT * FROM library_items WHERE isFinished = 1 ORDER BY lastWatchedTimestamp DESC")
    fun getFinished(): Flow<List<LibraryMediaEntity>>

    @Query("SELECT * FROM library_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: String): LibraryMediaEntity?

    @Query("SELECT * FROM library_items WHERE id = :id LIMIT 1")
    fun observeItemById(id: String): Flow<LibraryMediaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LibraryMediaEntity)

    @Update
    suspend fun update(item: LibraryMediaEntity)

    @Query("DELETE FROM library_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
