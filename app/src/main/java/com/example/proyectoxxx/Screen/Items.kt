package com.example.proyectoxxx.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import com.example.proyectoxxx.Model.User
import com.example.proyectoxxx.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UserApp(userRepository: UserRepository) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var deleteUserId by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    fun cargarUsuarios() {
        scope.launch {
            users = withContext(Dispatchers.IO) {
                userRepository.getAllUsers()
            }
        }
    }


    fun validarCampos(): Boolean {
        if (nombre.isBlank()) {
            Toast.makeText(context, "El campo 'Nombre' no puede estar vacío", Toast.LENGTH_SHORT).show()
            return false
        }
        if (apellido.isBlank()) {
            Toast.makeText(context, "El campo 'Apellido' no puede estar vacío", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edad.isBlank() || edad.toIntOrNull() == null) {
            Toast.makeText(context, "El campo 'Edad' no puede estar vacío y debe ser un número", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    cargarUsuarios()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "Registro de Usuario",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(text = "Nombre") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text(text = "Apellido") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text(text = "Edad") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (validarCampos()) {
                    val user = User(
                        nombre = nombre,
                        apellido = apellido,
                        edad = edad.toIntOrNull() ?: 0
                    )
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            userRepository.insert(user)
                        }
                        Toast.makeText(context, "Usuario registrado", Toast.LENGTH_SHORT).show()
                        cargarUsuarios()
                    }
                }
            }) {
                Text(text = "Registrar")
            }

            Spacer(modifier = Modifier.height(16.dp))


            TextField(
                value = deleteUserId,
                onValueChange = { deleteUserId = it },
                label = { Text(text = "ID del Usuario para eliminar") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))


            Button(onClick = {
                val userId = deleteUserId.toIntOrNull()
                if (userId != null) {
                    scope.launch {
                        val rowsDeleted = withContext(Dispatchers.IO) {
                            userRepository.deleteById(userId)
                        }
                        if (rowsDeleted > 0) {
                            Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                            cargarUsuarios()
                        } else {
                            Toast.makeText(context, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Por favor ingrese un ID válido", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(text = "Eliminar por ID")
            }

            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn {
                items(users) { user ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("ID: ${user.id}, ${user.nombre} ${user.apellido}, Edad: ${user.edad}")
                        Spacer(modifier = Modifier.height(4.dp))

                        Button(onClick = {
                            scope.launch {
                                val rowsDeleted = withContext(Dispatchers.IO) {
                                    userRepository.deleteById(user.id)
                                }
                                if (rowsDeleted > 0) {
                                    Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                                    cargarUsuarios() // Actualizar la lista de usuarios después de eliminar uno
                                } else {
                                    Toast.makeText(context, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Text(text = "Eliminar")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = {
                            nombre = user.nombre
                            apellido = user.apellido
                            edad = user.edad.toString()
                            selectedUserId = user.id
                        }) {
                            Text(text = "Modificar")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedUserId != null) {
                Button(onClick = {
                    if (validarCampos()) {
                        val updatedUser = User(
                            id = selectedUserId!!,
                            nombre = nombre,
                            apellido = apellido,
                            edad = edad.toIntOrNull() ?: 0
                        )
                        scope.launch {
                            val rowsUpdated = withContext(Dispatchers.IO) {
                                userRepository.updateById(updatedUser.id, updatedUser.nombre, updatedUser.apellido, updatedUser.edad)
                            }
                            if (rowsUpdated > 0) {
                                Toast.makeText(context, "Usuario modificado", Toast.LENGTH_SHORT).show()
                                cargarUsuarios()
                            } else {
                                Toast.makeText(context, "Error al modificar usuario", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }) {
                    Text(text = "Confirmar Modificación")
                }
            }
        }
    }
}
