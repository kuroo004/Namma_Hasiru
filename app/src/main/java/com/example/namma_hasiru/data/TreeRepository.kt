package com.example.namma_hasiru.data

import kotlinx.coroutines.flow.Flow

class TreeRepository(private val treeDao: TreeDao) {

    val allTrees: Flow<List<TreeEntry>> = treeDao.getAllTrees()
    val survivalRate: Flow<Float?> = treeDao.getSurvivalRate()

    suspend fun insert(tree: TreeEntry) {
        treeDao.insertTree(tree)
    }

    suspend fun update(tree: TreeEntry) {
        treeDao.updateTree(tree)
    }

    suspend fun delete(tree: TreeEntry) {
        treeDao.deleteTree(tree)
    }

    suspend fun getTreeById(id: Int): TreeEntry? {
        return treeDao.getTreeById(id)
    }
}
