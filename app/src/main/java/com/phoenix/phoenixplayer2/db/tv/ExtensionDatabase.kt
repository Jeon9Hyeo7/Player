package com.phoenix.phoenixplayer2.db.tv

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.phoenix.phoenixplayer2.model.Extension

@Database(entities = [Extension::class], version = 1)
abstract class ExtensionDatabase: RoomDatabase() {
    abstract fun extensionDao(): ExtensionDao

    companion object {
        @Volatile
        private var INSTANCE: ExtensionDatabase? = null
        private val LOCK = Any()

        fun getDatabase(context: Context): ExtensionDatabase {
            return INSTANCE ?: synchronized(LOCK) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExtensionDatabase::class.java,
                    "extension_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}