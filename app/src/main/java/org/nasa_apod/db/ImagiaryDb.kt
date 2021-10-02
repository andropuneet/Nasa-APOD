package org.nasa_apod.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.nasa_apod.vo.Image

@Database(entities = [Image::class], version = 1, exportSchema = false)
abstract class ImagiaryDb : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}