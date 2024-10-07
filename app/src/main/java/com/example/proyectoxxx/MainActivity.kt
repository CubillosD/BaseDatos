package com.example.proyectoxxx
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyectoxxx.DAO.UserDao
import com.example.proyectoxxx.Database.UserDatabase
import com.example.proyectoxxx.Repository.UserRepository
import com.example.proyectoxxx.Screen.UserApp


class MainActivity : ComponentActivity() {
    private lateinit var userDao: UserDao
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = UserDatabase.getDatabase(applicationContext)
        userDao = db.UserDao()
        userRepository = UserRepository(userDao)

        enableEdgeToEdge()
        setContent{

            UserApp(userRepository)
        }
    }
}