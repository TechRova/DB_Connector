package `in`.techrova.dbconnectortwo.data

import androidx.room.*


@Dao
interface DeletedBillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGender(bill: DeletedBill)

    @Update
    fun updateGender(bill: DeletedBill)

    @Delete
    fun deleteGender(bill: DeletedBill)

    @Query("SELECT * FROM DeletedBill WHERE date == :date ORDER BY date DESC")
    fun getGenderByName(date: Long): List<DeletedBill>

    @Query("SELECT * FROM DeletedBill ORDER BY date DESC")
    fun getGenders(): List<DeletedBill>
}