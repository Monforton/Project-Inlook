package uab.cs422.projectinlook

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uab.cs422.projectinlook.entities.CalEvent

@Database(
    entities = [
        CalEvent::class
    ],
    version = 1
)
abstract class EventDatabase: RoomDatabase() {
    abstract val eventDao: EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getInstance(context: Context): EventDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                "event_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}