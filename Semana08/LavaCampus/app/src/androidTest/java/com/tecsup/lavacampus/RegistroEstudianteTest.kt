package com.tecsup.lavacampus

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.viewmodel.SesionViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroEstudianteTest : MejorasTestBase() {
    @Test
    fun guardaNombreConTildesCodigoYCorreoNormalizado() {
        val usuario = io {
            repo.registrarEstudiante(
                "  Óscar Ñúñez  ",
                " 123456 ",
                " Oscar1@Correo.Test ",
                "Secreto1",
            )
        }
        assertEquals("Óscar Ñúñez", usuario.nombre)
        assertEquals("123456", usuario.codigo)
        assertEquals("oscar1@correo.test", usuario.correo)
        assertEquals("CLIENTE", usuario.rol)
        assertEquals(usuario, io { repo.obtenerUsuario(usuario.id) })
        assertEquals(usuario, io { repo.login("OSCAR1@CORREO.TEST", "Secreto1") })
        assertNull(io { repo.login(usuario.correo, "incorrecta") })
    }

    private fun rechaza(accion: suspend () -> Unit) {
        val resultado = io { runCatching { accion() } }
        assertTrue(resultado.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun rechazaNombresConNumerosSimbolosOVacios() {
        listOf("", "O", "Oscar1", "Oscar@Eneque", "Oscar_Eneque").forEach { nombre ->
            rechaza { repo.registrarEstudiante(nombre, "123456", "nuevo@test.com", "Secreto1") }
        }
        assertEquals(2, io { db.usuarioDao().contar() })
    }

    @Test
    fun exigeSeisDigitosAsciiEnCodigo() {
        listOf("", "12345", "1234567", "U12345", "12 345", "１２３４５６").forEach { codigo ->
            rechaza { repo.registrarEstudiante("Oscar", codigo, "nuevo@test.com", "Secreto1") }
        }
        assertEquals(2, io { db.usuarioDao().contar() })
    }

    @Test
    fun exigeFormatoCorreoYClaveConMayusculaMinusculaYNumero() {
        listOf("correo-invalido", "ana@", "ana @correo.test").forEach { correo ->
            rechaza { repo.registrarEstudiante("Oscar", "123456", correo, "Secreto1") }
        }
        listOf("Corta1", "secreto1", "SECRETO1", "Secretos", "12345678", "A".repeat(129) + "a1")
            .forEach { clave ->
                rechaza { repo.registrarEstudiante("Oscar", "123456", "nuevo@test.com", clave) }
            }
        assertEquals(2, io { db.usuarioDao().contar() })
    }

    @Test
    fun noPermiteCorreoNiCodigoDuplicados() {
        io { repo.registrarEstudiante("Oscar", "123456", "nuevo@test.com", "Secreto1") }
        rechaza { repo.registrarEstudiante("Otro", "654321", "NUEVO@TEST.COM", "Secreto1") }
        rechaza { repo.registrarEstudiante("Otro", "123456", "otro@test.com", "Secreto1") }
        assertEquals(3, io { db.usuarioDao().contar() })
    }

    @Test
    fun registroConcurrenteNoDuplicaCuenta() {
        val resultados = io {
            coroutineScope {
                List(2) {
                        async {
                            runCatching {
                                repo.registrarEstudiante(
                                    "Oscar",
                                    "123456",
                                    "nuevo@test.com",
                                    "Secreto1",
                                )
                            }
                        }
                    }
                    .map { it.await() }
            }
        }
        assertEquals(1, resultados.count { it.isSuccess })
        assertEquals(3, io { db.usuarioDao().contar() })
    }

    @Test
    fun crearCuentaNoIniciaSesionHastaIngresar() {
        val modelo =
            vm(SesionViewModel::class.java) {
                SesionViewModel(repo, prefs) { Result.success(Unit) }
            }
        esperar(modelo.restaurandoSesion) { !it }
        main { modelo.registrar("Oscar", "123456", "nuevo@test.com", "Secreto1", "diferente") }
        assertEquals("Las contraseñas no coinciden", modelo.error.value)
        assertEquals(2, io { db.usuarioDao().contar() })
        main { modelo.registrar("Oscar", "123456", "nuevo@test.com", "Secreto1", "Secreto1") }
        esperar(modelo.registroCompletado) { it }
        esperar(modelo.cargando) { !it }
        assertNull(modelo.usuarioActual.value)
        assertEquals(0, io { prefs.sesionGuardada.first() }.usuarioId)
        assertEquals("nuevo@test.com", modelo.correoRegistrado.value)
        main { modelo.login("nuevo@test.com", "Secreto1", Rol.CLIENTE) }
        val usuario = esperar(modelo.usuarioActual) { it != null }!!
        assertEquals("CLIENTE", usuario.rol)
        assertEquals(usuario.id, io { prefs.sesionGuardada.first() }.usuarioId)
    }
}
