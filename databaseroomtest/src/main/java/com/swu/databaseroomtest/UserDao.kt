package com.swu.databaseroomtest

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {
    
    /**
     * 插入用户
     */
    @Insert
    suspend fun insertUser(user: User): Long
    
    /**
     * 更新用户
     */
    @Update
    suspend fun updateUser(user: User)
    
    /**
     * 删除用户
     */
    @Delete
    suspend fun deleteUser(user: User)
    
    /**
     * 根据ID查询用户
     */
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?
    
    /**
     * 查询所有用户
     */
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<User>>
    
    /**
     * 根据姓名查询用户
     */
    @Query("SELECT * FROM users WHERE name LIKE :name")
    fun getUsersByName(name: String): Flow<List<User>>
    
    /**
     * 删除所有用户
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

