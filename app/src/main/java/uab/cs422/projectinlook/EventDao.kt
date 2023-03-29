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

    @Transaction
    @Query("SELECT * FROM CalEvent " +
            "WHERE startYear <= :year  AND :year <= endYear " +
            "AND startMonth <= :month AND :month<= endMonth " +
            "AND startDayOfMonth <= :dayOfMonth AND :dayOfMonth <= endDayOfMonth")
    suspend fun getEventsOfDay(dayOfMonth: Int, month: Int, year: Int): List<CalEvent>

    // TODO: Add queries for events of month, of year, of range of days
}