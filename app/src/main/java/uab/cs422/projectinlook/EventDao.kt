package uab.cs422.projectinlook

import androidx.room.*
import uab.cs422.projectinlook.entities.CalEvent

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(vararg event: CalEvent)

    @Delete(CalEvent::class)
    suspend fun deleteEvent(vararg event: CalEvent)

    @Update
    suspend fun updateEvent(event: CalEvent)

    @Transaction
    @Query("SELECT * FROM CalEvent")
    suspend fun getAllEvents(): List<CalEvent>

    // TODO: Add queries for specific events
}