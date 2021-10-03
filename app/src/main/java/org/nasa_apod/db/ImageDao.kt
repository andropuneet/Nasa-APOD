package org.nasa_apod.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.nasa_apod.vo.Image

@Dao
abstract class ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(image: Image)
    @Query("SELECT * FROM Image WHERE date = :date")
    abstract fun findByDate(date: String): LiveData<Image>
    @Query("SELECT * FROM Image WHERE markFav = 1")
    abstract fun findByFav(): LiveData<List<Image>>
}