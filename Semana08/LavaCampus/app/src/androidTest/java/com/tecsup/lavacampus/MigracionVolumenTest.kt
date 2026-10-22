package com.tecsup.lavacampus

import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tecsup.lavacampus.data.local.LavaDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class MigracionVolumenTest {
    @Test fun versionDosATresConservaHistorialSinAplicarDescuentoRetroactivo() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nombre = "migracion-${UUID.randomUUID()}.db"
        val helper = FrameworkSQLiteOpenHelperFactory().create(SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(nombre).callback(object : SupportSQLiteOpenHelper.Callback(2) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE servicios (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre TEXT NOT NULL, descripcion TEXT NOT NULL, unidad TEXT NOT NULL, precio REAL NOT NULL, horasEntrega INTEGER NOT NULL, activo INTEGER NOT NULL, icono TEXT NOT NULL)")
                    LavaDatabase.MIGRATION_1_2.migrate(db)
                    db.execSQL("INSERT INTO usuarios VALUES (1,'Ana','U001','ana@test','1234','CLIENTE')")
                    db.execSQL("INSERT INTO servicios VALUES (1,'Lavado','','KG',8,24,1,'wash')")
                    db.execSQL("INSERT INTO pedidos VALUES (1,1,'2026-10-21','2026-10-23','ENTREGADO',48,'C-01',1,'original',1000)")
                    db.execSQL("INSERT INTO detalles_pedido VALUES (1,1,1,6,8,48)")
                }
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            }).build())
        helper.writableDatabase
        helper.close()
        val nueva = Room.databaseBuilder(context, LavaDatabase::class.java, nombre).addMigrations(LavaDatabase.MIGRATION_2_3).build()
        try {
            val pedido = nueva.pedidoDao().obtener(1)!!
            assertEquals(48.0, pedido.total, 0.001)
            assertEquals(48.0, pedido.subtotal, 0.001)
            assertEquals(0.0, pedido.descuento, 0.001)
            assertEquals("original", pedido.nota)
            assertEquals(1, nueva.usuarioDao().contar())
            assertEquals(1, nueva.servicioDao().contar())
        } finally { nueva.close(); context.deleteDatabase(nombre) }
    }
}
