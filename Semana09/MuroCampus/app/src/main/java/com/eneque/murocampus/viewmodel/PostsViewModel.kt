package com.eneque.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eneque.murocampus.data.PostRepository
import com.eneque.murocampus.data.mensajeDe
import com.eneque.murocampus.model.Post
import com.eneque.murocampus.model.NuevoPost
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class PostsViewModel(private val repo: PostRepository) : ViewModel() {
    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista = _lista.asStateFlow()
    private var cargaLista: Job? = null
    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()
    val listaFiltrada = combine(lista, busqueda) { estado, consulta ->
        when (estado) {
            is UiState.Exito -> UiState.Exito(
                estado.datos.filter { it.title.contains(consulta.trim(), ignoreCase = true) }
            )
            else -> estado
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    fun buscar(texto: String) { _busqueda.value = texto }
    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro = _usuarioFiltro.asStateFlow()

    fun filtrar(userId: Int?) {
        require(userId == null || userId in 1..10)
        _usuarioFiltro.value = userId
        cargar()
    }

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle = _detalle.asStateFlow()
    private var cargaDetalle: Job? = null
    private var detalleId: Int? = null

    fun abrir(id: Int) {
        if (detalleId == id && (_detalle.value is UiState.Exito || cargaDetalle?.isActive == true)) return
        detalleId = id
        cargaDetalle?.cancel()
        cargaDetalle = viewModelScope.launch {
            _detalle.value = UiState.Cargando
            coroutineScope {
                val postPendiente = async { repo.detalle(id) }
                val comentariosPendientes = async { repo.comentarios(id) }
                val post = postPendiente.await()
                val comentarios = comentariosPendientes.await()
                val error = post.exceptionOrNull() ?: comentarios.exceptionOrNull()
                _detalle.value = if (error != null) {
                    UiState.Error(mensajeDe(error))
                } else {
                    UiState.Exito(DetalleUi(post.getOrThrow(), comentarios.getOrThrow()))
                }
            }
        }
    }

    fun cerrarDetalle() {
        cargaDetalle?.cancel()
        detalleId = null
        _detalle.value = UiState.Cargando
    }

    private val _envio = MutableStateFlow<UiState<Post>?>(null)
    val envio = _envio.asStateFlow()
    private var tareaEnvio: Job? = null

    fun reiniciarCreacion() {
        tareaEnvio?.cancel()
        _envio.value = null
    }

    fun publicar(titulo: String, texto: String) {
        if (titulo.isBlank() || texto.isBlank() || tareaEnvio?.isActive == true) return
        tareaEnvio = viewModelScope.launch {
            _envio.value = UiState.Cargando
            repo.crear(NuevoPost(userId = 1, title = titulo.trim(), body = texto.trim()))
                .onSuccess { _envio.value = UiState.Exito(it) }
                .onFailure { _envio.value = UiState.Error(mensajeDe(it)) }
        }
    }

    init { cargar() }

    fun cargar() {
        cargaLista?.cancel()
        cargaLista = viewModelScope.launch {
            _lista.value = UiState.Cargando
            val userId = _usuarioFiltro.value
            val resultado = if (userId == null) repo.listar() else repo.porUsuario(userId)
            resultado
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }
}
