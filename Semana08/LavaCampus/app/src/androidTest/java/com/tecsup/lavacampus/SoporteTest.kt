package com.tecsup.lavacampus
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.lavacampus.viewmodel.SesionViewModel
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.data.local.DatosIniciales
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SoporteTest : MejorasTestBase() {
    @Test fun registroSoporteRequiereConfirmacionParaIngresar() {
        val modelo = vm(SesionViewModel::class.java) { SesionViewModel(repo, prefs) { Result.success(Unit) } }
        esperar(modelo.restaurandoSesion) { !it }
        main { modelo.registrarSoporte("Oscar", "Eneque", "987654", "soporte@test.com", "Soporte1", "Soporte1") }
        esperar(modelo.registroCompletado) { it }
        esperar(modelo.cargando) { !it }
        assertNull(modelo.usuarioActual.value)
        val cuenta = io { repo.login("soporte@test.com", "Soporte1") }!!
        assertEquals("SOPORTE", cuenta.rol)
        assertEquals("Oscar Eneque", cuenta.nombre)
        assertEquals(Rol.SOPORTE, modelo.rolRegistrado.value)
        main { modelo.login(cuenta.correo, "Soporte1", Rol.SOPORTE, "distinta") }
        assertNull(modelo.usuarioActual.value)
        assertNotNull(modelo.error.value)
        main { modelo.login(cuenta.correo, "Soporte1", Rol.SOPORTE, "Soporte1") }
        assertEquals(cuenta.id, esperar(modelo.usuarioActual) { it != null }!!.id)
        assertEquals("SOPORTE", io { prefs.sesionGuardada.first() }.perfil)
    }
    @Test fun agregaOtrosServiciosUnaVezSinReinsertarUsuarios() {
        io { DatosIniciales.sembrar(db); DatosIniciales.sembrar(db) }
        val otros = io { repo.observarServiciosActivos().first().filter { it.nombre == "Otros Servicios" } }
        assertEquals(1, otros.size)
        assertEquals(5.0, otros.single().precio, .001)
        assertEquals(2, io { db.usuarioDao().contar() })
    }
}
