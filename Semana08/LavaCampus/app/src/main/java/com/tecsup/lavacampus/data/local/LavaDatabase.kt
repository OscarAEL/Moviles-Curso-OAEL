package com.tecsup.lavacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tecsup.lavacampus.data.local.dao.DetalleDao
import com.tecsup.lavacampus.data.local.dao.PedidoDao
import com.tecsup.lavacampus.data.local.dao.ServicioDao
import com.tecsup.lavacampus.data.local.dao.UsuarioDao
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario

@Database(
    entities = [Servicio::class, Usuario::class, Pedido::class, DetallePedido::class],
    version = 2,
    exportSchema = false
)
abstract class LavaDatabase : RoomDatabase() {
    abstract fun servicioDao(): ServicioDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detalleDao(): DetalleDao

    companion object {
        @Volatile
        private var INSTANCE: LavaDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `usuarios` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `nombre` TEXT NOT NULL,
                        `codigo` TEXT NOT NULL,
                        `correo` TEXT NOT NULL,
                        `clave` TEXT NOT NULL,
                        `rol` TEXT NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `pedidos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `usuarioId` INTEGER NOT NULL,
                        `fechaRegistro` TEXT NOT NULL,
                        `fechaEntrega` TEXT NOT NULL,
                        `estado` TEXT NOT NULL,
                        `total` REAL NOT NULL,
                        `casillero` TEXT,
                        `pagado` INTEGER NOT NULL,
                        `nota` TEXT NOT NULL,
                        `fechaCreacion` INTEGER NOT NULL,
                        FOREIGN KEY(`usuarioId`) REFERENCES `usuarios`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_pedidos_usuarioId` ON `pedidos` (`usuarioId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_pedidos_estado` ON `pedidos` (`estado`)")
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `detalles_pedido` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `pedidoId` INTEGER NOT NULL,
                        `servicioId` INTEGER NOT NULL,
                        `cantidad` REAL NOT NULL,
                        `precioUnitario` REAL NOT NULL,
                        `subtotal` REAL NOT NULL,
                        FOREIGN KEY(`pedidoId`) REFERENCES `pedidos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`servicioId`) REFERENCES `servicios`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_detalles_pedido_pedidoId` ON `detalles_pedido` (`pedidoId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_detalles_pedido_servicioId` ON `detalles_pedido` (`servicioId`)")
            }
        }

        fun getInstance(context: Context): LavaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LavaDatabase::class.java,
                    "lavacampus.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
