package com.eneque.murocampus

import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eneque.murocampus.data.PostRepository
import com.eneque.murocampus.model.Comentario
import com.eneque.murocampus.model.NuevoPost
import com.eneque.murocampus.model.Post
import com.eneque.murocampus.viewmodel.PostsViewModel
import com.eneque.murocampus.viewmodel.UiState
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostsViewModelTest {
    private val almacen = ViewModelStore()
    private val instrumentacion = InstrumentationRegistry.getInstrumentation()

    private class RepoFalso(
        val bloquearDetalle: Boolean = false,
        val fallarComentarios: Boolean = false
    ) : PostRepository {
        val inicio = CompletableDeferred<Unit>()
        val cancelado = CompletableDeferred<Unit>()
        val post = Post(1, 1, "Título", "Contenido")
        override suspend fun listar() = Result.success(listOf(post))
        override suspend fun porUsuario(userId: Int) = Result.success(listOf(post.copy(userId = userId)))
        override suspend fun crear(nuevo: NuevoPost) =
            Result.success(Post(nuevo.userId, 101, nuevo.title, nuevo.body))
        override suspend fun detalle(id: Int): Result<Post> {
            if (bloquearDetalle) {
                inicio.complete(Unit)
                try { awaitCancellation() } finally { cancelado.complete(Unit) }
            }
            return Result.success(post)
        }
        override suspend fun comentarios(id: Int): Result<List<Comentario>> =
            if (fallarComentarios) Result.failure(IOException("Fallo de comentarios"))
            else Result.success(emptyList())
    }

    private fun crear(repo: PostRepository): PostsViewModel {
        lateinit var vm: PostsViewModel
        instrumentacion.runOnMainSync {
            vm = PostsViewModel(repo)
            almacen.put("posts", vm)
        }
        return vm
    }

    @After fun limpiar() { instrumentacion.runOnMainSync { almacen.clear() } }

    @Test fun volverCancelaDetalleYConservaLaLista() = runBlocking {
        val repo = RepoFalso(bloquearDetalle = true)
        val vm = crear(repo)
        instrumentacion.runOnMainSync { vm.abrir(1) }
        withTimeout(5000) { repo.inicio.await() }
        instrumentacion.runOnMainSync { vm.cerrarDetalle() }
        withTimeout(5000) { repo.cancelado.await() }
        assertTrue(vm.lista.value is UiState.Exito)
        assertEquals(UiState.Cargando, vm.detalle.value)
    }

    @Test fun falloDeComentariosImpideMostrarExitoParcial() {
        val vm = crear(RepoFalso(fallarComentarios = true))
        instrumentacion.runOnMainSync { vm.abrir(1) }
        instrumentacion.waitForIdleSync()
        assertTrue(vm.detalle.value is UiState.Error)
    }
}
