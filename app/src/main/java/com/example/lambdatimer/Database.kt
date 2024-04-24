package com.example.lambdatimer

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.*
import androidx.room.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SavedTimersEntity::class],
    version = 1
)
abstract class SavedTimerDatabase: RoomDatabase() {
    abstract fun getDao(): SavedTimersDao
    companion object {
        fun getDB(context: Context): SavedTimerDatabase {
            return Room.databaseBuilder(context.applicationContext, SavedTimerDatabase::class.java, "savedTimers.db").build()
        }
    }
}

@Entity
data class SavedTimersEntity (
    var tag: String,
    var workM: Int,
    var workS: Int,
    var breakM: Int,
    var breakS: Int,
    var bbM: Int,
    var bbS: Int,
    @ColumnInfo(name = "After value") var afterV: Int,
) {
    @PrimaryKey(autoGenerate = true) var id: Long? = null
}

@Dao
interface SavedTimersDao {

    @Insert
    suspend fun add(entity: SavedTimersEntity): Long

    @Query("SELECT * FROM savedtimersentity ORDER BY id ASC")
    fun getAll(): LiveData<List<SavedTimersEntity>>

    @Update
    suspend fun update(entity: SavedTimersEntity)

    @Delete
    suspend fun delete(entity: SavedTimersEntity)

    @Query("DELETE FROM savedtimersentity")
    suspend fun deleteAll(): Int
}

class SavedTimersRepository(private val dao: SavedTimersDao) {
    val readAllData: LiveData<List<SavedTimersEntity>> = dao.getAll()

    suspend fun add(entity: SavedTimersEntity) { dao.add(entity) }

    suspend fun update(entity: SavedTimersEntity) { dao.update(entity) }

    suspend fun delete(entity: SavedTimersEntity) { dao.delete(entity) }

    suspend fun deleteAll() { dao.deleteAll() }
}

class MViewModel(application: Application): AndroidViewModel(application) {
    val readAllData: LiveData<List<SavedTimersEntity>>
    private val repository: SavedTimersRepository
    init {
        val dao = SavedTimerDatabase.getDB(application).getDao()
        repository = SavedTimersRepository(dao)
        readAllData = repository.readAllData
    }
    fun add(entity: SavedTimersEntity) { viewModelScope.launch(Dispatchers.IO) { repository.add(entity) } }

    fun update(entity: SavedTimersEntity) { viewModelScope.launch(Dispatchers.IO) { repository.update(entity) } }

    fun delete(entity: SavedTimersEntity) { viewModelScope.launch(Dispatchers.IO) { repository.delete(entity) } }

    fun deleteAll() { viewModelScope.launch(Dispatchers.IO) { repository.deleteAll() } }
}