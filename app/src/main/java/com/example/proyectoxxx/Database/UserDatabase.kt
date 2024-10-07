package com.example.proyectoxxx.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectoxxx.DAO.UserDao
import com.example.proyectoxxx.Model.User


@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase()  {
    abstract fun UserDao(): UserDao

    companion object{
         @Volatile
         private var INSTANCE: UserDatabase? = null


        fun getDatabase(context: Context):UserDatabase{
            return INSTANCE ?: synchronized( this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}