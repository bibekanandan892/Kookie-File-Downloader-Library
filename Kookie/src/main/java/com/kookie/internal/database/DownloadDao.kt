package com.kookie.internal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(downloadEntity: DownloadEntity)

    @Update
    suspend fun update(downloadEntity: DownloadEntity)

    @Query("SELECT * FROM downloads where id = :id")
    suspend fun get(id: String): DownloadEntity?

    @Query("DELETE FROM downloads where id = :id")
    suspend fun remove(id: String)

    @Query("DELETE FROM downloads")
    suspend fun deleteAll()

    @Query("SELECT * FROM downloads ORDER BY queueTime ASC")
    suspend fun getAllDownloadFlow(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads ORDER BY queueTime ASC")
    suspend fun getAllDownload(): List<DownloadEntity>
}