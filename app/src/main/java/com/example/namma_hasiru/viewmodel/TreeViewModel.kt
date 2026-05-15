package com.example.namma_hasiru.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.example.namma_hasiru.data.AppDatabase
import com.example.namma_hasiru.data.TreeEntry
import com.example.namma_hasiru.data.TreeRepository
import com.example.namma_hasiru.worker.ReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TreeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TreeRepository
    val allTrees: LiveData<List<TreeEntry>>
    val survivalRate: LiveData<Float?>

    init {
        val treeDao = AppDatabase.getDatabase(application).treeDao()
        repository = TreeRepository(treeDao)
        allTrees = repository.allTrees.asLiveData()
        survivalRate = repository.survivalRate.asLiveData()
    }

    fun insert(tree: TreeEntry) = viewModelScope.launch {
        repository.insert(tree)
        schedule90DayReminder(tree.speciesName)
    }

    fun update(tree: TreeEntry) = viewModelScope.launch {
        repository.update(tree)
    }

    fun delete(tree: TreeEntry) = viewModelScope.launch {
        repository.delete(tree)
    }

    suspend fun getTreeById(id: Int): TreeEntry? {
        return repository.getTreeById(id)
    }

    private fun schedule90DayReminder(speciesName: String) {
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(90, TimeUnit.DAYS)
            .setInputData(workDataOf("species_name" to speciesName))
            .build()

        WorkManager.getInstance(getApplication()).enqueue(workRequest)
    }
}
