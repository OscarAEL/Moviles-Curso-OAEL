package com.tecsup.lavacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.lavacampus.data.local.dao.ServicioDao
import com.tecsup.lavacampus.data.local.entity.Servicio

@Database(entities = [Servicio::class], version = 1, exportSchema = false)
abstract class LavaDatabase : RoomDatabase() {
    abstract fun servicioDao(): ServicioDao

    companion object {
        @Volatile
        private var INSTANCE: LavaDatabase? = null

        fun getInstance(context: Context): LavaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LavaDatabase::class.java,
                    "lavacampus.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
