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
    @Query(
        "SELECT * FROM CalEvent " +
                "WHERE startYear <= :year  AND :year <= endYear " +
                "AND startMonth <= :month AND :month<= endMonth " +
                "AND startDayOfMonth <= :dayOfMonth AND :dayOfMonth <= endDayOfMonth"
    )
    suspend fun getEventsOfDay(dayOfMonth: Int, month: Int, year: Int): List<CalEvent>

    @Transaction
    @Query(
        "SELECT * FROM CalEvent " +
                "WHERE startYear <= :year  AND :year <= endYear " +
                "AND startMonth <= :month AND :month<= endMonth "
    )
    suspend fun getEventsOfMonth(month: Int, year: Int): List<CalEvent>

    @Transaction
    @Query(
        "SELECT * FROM CalEvent " +
                "WHERE startYear <= :year  AND :year <= endYear "
    )
    suspend fun getEventsOfYear(year: Int): List<CalEvent>

    @Transaction
    @Query(
        "SELECT * FROM CalEvent " +
                "WHERE startDayOfMonth <= :startDayOfMonth  AND :endDayOfMonth <= endDayOfMonth " +
                "AND startYear <= :startYear  AND :endYear <= endYear " +
                "AND startMonth <= :startMonth AND :endMonth <= endMonth"
    )
    suspend fun getEventsInRange(
        startDayOfMonth: Int,
        startMonth: Int,
        startYear: Int,
        endDayOfMonth: Int,
        endMonth: Int,
        endYear: Int
    ): List<CalEvent>
}