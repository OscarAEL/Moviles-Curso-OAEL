package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.LavaApp
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.Rol
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore,
    private val esperarInicializacion: suspend () -> Result<Unit>
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _restaurandoSesion = MutableStateFlow(true)
    val restaurandoSesion: StateFlow<Boolean> = _restaurandoSesion.asStateFlow()

    val modoOscuro: StateFlow<Boolean> = preferenciasStore.modoOscuro
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        restaurarSesion()
    }

    private fun restaurarSesion() {
        viewModelScope.launch {
            try {
                val resInit = esperarInicializacion()
                if (resInit.isFailure) {
                    _error.value = "Error al inicializar la base de datos: ${resInit.exceptionOrNull()?.message}"
                    _usuarioActual.value = null
                    return@launch
                }

                val (perfil, usuarioId) = preferenciasStore.sesionGuardada.first()

                if (usuarioId != 0 && perfil.isNotEmpty()) {
                    val usuarioReal = repository.obtenerUsuario(usuarioId)
                    if (usuarioReal != null && usuarioReal.rol == perfil) {
                        _usuarioActual.value = usuarioReal
                    } else {
                        // Sesión inválida en la BD, limpiar preferencias
                        preferenciasStore.cerrarSesion()
                        _usuarioActual.value = null
                    }
                } else {
                    _usuarioActual.value = null
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al restaurar sesión"
                _usuarioActual.value = null
            } finally {
                _restaurandoSesion.value = false
            }
        }
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol) {
        if (_cargando.value) return

        val correoLimpio = correo.trim()
        if (correoLimpio.isEmpty() || clave.isEmpty()) {
            _error.value = "Por favor, ingresa el correo y la contraseña"
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val usuario = repository.login(correoLimpio, clave)
                if (usuario == null) {
                    _error.value = "Correo o contraseña incorrectos"
                } else if (usuario.rol != rolSeleccionado.name) {
                    val rolTexto = if (rolSeleccionado == Rol.CLIENTE) "Estudiante" else "Administrador"
                    _error.value = "El usuario no corresponde al rol de $rolTexto"
                } else {
                    preferenciasStore.guardarSesion(rolSeleccionado, usuario.id)
                    _usuarioActual.value = usuario
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al iniciar sesión"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cerrarSesion() {
        if (_cargando.value) return
        viewModelScope.launch {
            _cargando.value = true
            try {
                preferenciasStore.cerrarSesion()
                _usuarioActual.value = null
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al cerrar sesión"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    /** Cambio de cuenta para el modo de práctica del laboratorio. */
    fun cambiarPerfil() {
        if (_cargando.value || _restaurandoSesion.value) return
        val actual = _usuarioActual.value ?: return
        val rol = if (actual.rol == Rol.ADMIN.name) Rol.CLIENTE else Rol.ADMIN
        _cargando.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                // Guarda también las sesiones restauradas anteriores a esta versión.
                preferenciasStore.guardarSesion(Rol.valueOf(actual.rol), actual.id)
                val anterior = preferenciasStore.ultimoUsuario(rol).first()
                val destino = repository.obtenerUsuario(anterior)?.takeIf { it.rol == rol.name }
                    ?: repository.obtenerUsuarioPorRol(rol.name)
                    ?: error("No existe una cuenta para este perfil")
                preferenciasStore.guardarSesion(rol, destino.id)
                _usuarioActual.value = destino
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "No se pudo cambiar de perfil"
            } finally {
                _cargando.value = false
            }
        }
    }

    class Factory(
        private val app: LavaApp
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SesionViewModel::class.java)) {
                return SesionViewModel(
                    repository = app.repository,
                    preferenciasStore = app.preferenciasStore,
                    esperarInicializacion = { app.esperarInicializacion() }
                ) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
