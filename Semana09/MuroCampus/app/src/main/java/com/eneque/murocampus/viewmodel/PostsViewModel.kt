package com.eneque.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eneque.murocampus.data.PostRepository
import com.eneque.murocampus.data.mensajeDe
import com.eneque.murocampus.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostsViewModel(private val repo: PostRepository) : ViewModel() {
    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista = _lista.asStateFlow()
    private var cargaLista: Job? = null

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

    init { cargar() }

    fun cargar() {
        cargaLista?.cancel()
        cargaLista = viewModelScope.launch {
            _lista.value = UiState.Cargando
            repo.listar()
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }
}
