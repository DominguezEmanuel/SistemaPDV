-- =========================================================
-- SISTEMA PDV
-- data.sql
-- Archivo de datos de prueba por etapas
--
-- IMPORTANTE:
-- - Este archivo asume que el schema.sql ya fue ejecutado.
-- - Las tablas y tipos ya deben existir.
-- - Los IDs se cargan de forma explícita para que puedas
--   hacer pruebas consistentes con las relaciones.
-- =========================================================

-- =========================================================
-- BLOQUE DE LIMPIEZA
-- Objetivo:
-- Dejar la base vacía para poder volver a ejecutar este
-- archivo tantas veces como sea necesario sin borrar la
-- base de datos manualmente.
--
-- IMPORTANTE:
-- - Este bloque asume que las tablas ya existen.
-- - Si todavía no ejecutaste schema.sql, omití este bloque
--   en la primera carga o ejecutá primero el esquema.
-- =========================================================

TRUNCATE TABLE
    movimientos_stock,
    pagos,
    detalle_ventas,
    ventas,
    stock,
    productos_canales,
    variantes_producto,
    productos,
    usuarios,
    canales_venta,
    categorias
RESTART IDENTITY CASCADE;

-- =========================================================
-- FIN DEL BLOQUE DE LIMPIEZA
-- =========================================================

-- =========================================================
-- ETAPA 1: DATOS MAESTROS
-- Objetivo:
-- Cargar las tablas base que no dependen de otras tablas.
-- Con esto ya podés probar catálogos, logins y relaciones
-- iniciales de productos.
-- =========================================================

INSERT INTO categorias (id_categoria, nombre, descripcion) VALUES
(1, 'Labiales', 'Productos para color y cuidado de labios'),
(2, 'Bases', 'Bases líquidas y en polvo para maquillaje facial'),
(3, 'Delineadores', 'Delineadores para ojos y labios'),
(4, 'Sombras', 'Sombras compactas y paletas'),
(5, 'Esmaltes', 'Esmaltes y productos para uñas'),
(6, 'Accesorios', 'Brochas, esponjas y accesorios varios');

INSERT INTO canales_venta (id_canal_venta, nombre) VALUES
(1, 'Local'),
(2, 'TikTok');

INSERT INTO usuarios (id_usuario, nombre, apellido, username, password, activo, rol) VALUES
(1, 'Juan', 'Pérez', 'admin', '$2a$10$hashdeejemploadmin000000000000000000000000000000000', TRUE, 'ADMINISTRADOR'),
(2, 'María', 'Gómez', 'cajero1', '$2a$10$hashdeejemploCAJERO00000000000000000000000000000000', TRUE, 'CAJERO'),
(3, 'Lucía', 'Romero', 'cajero2', '$2a$10$hashdeejemploCAJERO2000000000000000000000000000000', TRUE, 'CAJERO');

-- Ajuste de secuencias para que los futuros INSERT automáticos
-- de la aplicación no choquen con los IDs cargados aquí.
SELECT setval(pg_get_serial_sequence('categorias', 'id_categoria'), (SELECT COALESCE(MAX(id_categoria), 1) FROM categorias), true);
SELECT setval(pg_get_serial_sequence('canales_venta', 'id_canal_venta'), (SELECT COALESCE(MAX(id_canal_venta), 1) FROM canales_venta), true);
SELECT setval(pg_get_serial_sequence('usuarios', 'id_usuario'), (SELECT COALESCE(MAX(id_usuario), 1) FROM usuarios), true);

-- =========================================================
-- ETAPA 2: PRODUCTOS Y VARIANTES
-- Objetivo:
-- Cargar productos de ejemplo con y sin variantes.
-- Esto te permite probar:
-- - productos simples (variante única)
-- - productos con variantes de color
-- - precios minoristas y mayoristas
-- - imágenes referenciadas por ruta o nombre de archivo
-- =========================================================

INSERT INTO productos (
    id_producto,
    id_categoria,
    nombre,
    descripcion,
    imagen,
    precio_minorista,
    precio_mayorista,
    cantidad_minima_mayorista,
    activo
) VALUES
(1, 1, 'Labial Mate', 'Labial de terminación mate con buena duración', 'labial_mate.jpg', 3500.00, 2800.00, 30, TRUE),
(2, 2, 'Base Líquida HD', 'Base líquida de cobertura media-alta', 'base_liquida_hd.jpg', 8900.00, 7200.00, 20, TRUE),
(3, 3, 'Delineador Negro Intenso', 'Delineador negro de alta pigmentación', 'delineador_negro.jpg', 2600.00, 1900.00, 50, TRUE),
(4, 5, 'Esmalte Brillo Glam', 'Esmalte con brillo y secado rápido', 'esmalte_brillo_glam.jpg', 3200.00, 2500.00, 40, TRUE),
(5, 4, 'Sombra Compacta Sunset', 'Sombra compacta para uso diario y eventos', 'sombra_compacta_sunset.jpg', 5400.00, 4300.00, 25, TRUE),
(6, 6, 'Brocha Difusora Pro', 'Brocha para difuminar maquillaje profesional', 'brocha_difusora_pro.jpg', 4500.00, 3600.00, 15, TRUE);

INSERT INTO variantes_producto (
    id_variante,
    id_producto,
    codigo_barras,
    nombre_variante,
    activo
) VALUES
(1, 1, 'LBM-ROJ', 'Rojo', TRUE),
(2, 1, 'LBM-ROS', 'Rosa', TRUE),
(3, 1, 'LBM-NUD', 'Nude', TRUE),
(4, 1, 'LBM-VIN', 'Vino', TRUE),

(5, 2, 'BLH-UNI', 'Única', TRUE),
(6, 3, 'DNI-UNI', 'Única', TRUE),

(7, 4, 'EBG-ROJ', 'Rojo', TRUE),
(8, 4, 'EBG-ROS', 'Rosa', TRUE),
(9, 4, 'EBG-AZU', 'Azul', TRUE),

(10, 5, 'SCS-NAT', 'Natural', TRUE),
(11, 5, 'SCS-BRO', 'Bronce', TRUE),

(12, 6, 'BDP-UNI', 'Única', TRUE);

SELECT setval(pg_get_serial_sequence('productos', 'id_producto'), (SELECT COALESCE(MAX(id_producto), 1) FROM productos), true);
SELECT setval(pg_get_serial_sequence('variantes_producto', 'id_variante'), (SELECT COALESCE(MAX(id_variante), 1) FROM variantes_producto), true);

-- =========================================================
-- ETAPA 3: CONFIGURACIÓN POR CANAL Y STOCK
-- Objetivo:
-- Cargar la relación producto-canal y el stock por variante
-- y por canal. Esto te permite validar:
-- - límite mayorista por producto
-- - stock disponible por variante
-- - stock mínimo
-- - consultas por canal de venta
-- =========================================================

INSERT INTO productos_canales (
    id_producto_canal,
    id_producto,
    id_canal_venta,
    limite_mayorista
) VALUES
(1, 1, 1, 30),
(2, 1, 2, 25),

(3, 2, 1, 20),
(4, 2, 2, 15),

(5, 3, 1, 50),
(6, 3, 2, 40),

(7, 4, 1, 40),
(8, 4, 2, 35),

(9, 5, 1, 25),
(10, 5, 2, 20),

(11, 6, 1, 15),
(12, 6, 2, 10);

INSERT INTO stock (
    id_stock,
    id_variante,
    id_canal_venta,
    cantidad_disponible,
    stock_minimo
) VALUES
-- Labial Mate (producto con variantes de color)
(1, 1, 1, 34, 10),
(2, 2, 1, 18, 8),
(3, 3, 1, 22, 8),
(4, 4, 1, 12, 5),

(5, 1, 2, 14, 5),
(6, 2, 2, 10, 5),
(7, 3, 2, 8, 4),
(8, 4, 2, 6, 4),

-- Base Líquida HD (variante única)
(9, 5, 1, 27, 8),
(10, 5, 2, 12, 4),

-- Delineador Negro Intenso (variante única)
(11, 6, 1, 60, 15),
(12, 6, 2, 25, 8),

-- Esmalte Brillo Glam (variantes de color)
(13, 7, 1, 45, 12),
(14, 8, 1, 39, 12),
(15, 9, 1, 16, 8),

(16, 7, 2, 20, 8),
(17, 8, 2, 18, 8),
(18, 9, 2, 9, 4),

-- Sombra Compacta Sunset
(19, 10, 1, 30, 10),
(20, 11, 1, 24, 8),

(21, 10, 2, 11, 4),
(22, 11, 2, 9, 4),

-- Brocha Difusora Pro
(23, 12, 1, 19, 5),
(24, 12, 2, 7, 3);

SELECT setval(pg_get_serial_sequence('productos_canales', 'id_producto_canal'), (SELECT COALESCE(MAX(id_producto_canal), 1) FROM productos_canales), true);
SELECT setval(pg_get_serial_sequence('stock', 'id_stock'), (SELECT COALESCE(MAX(id_stock), 1) FROM stock), true);

-- =========================================================
-- ETAPA 4: VENTAS DE PRUEBA
-- Objetivo:
-- Crear ventas con distintos estados, detalles y pagos.
-- Esto te permite probar:
-- - totales
-- - pagos múltiples
-- - detalle de ventas
-- - consultas por fecha, usuario y estado
-- =========================================================

INSERT INTO ventas (
    id_venta,
    id_usuario,
    fecha_hora,
    subtotal,
    descuento,
    total,
    estado
) VALUES
(1, 2, '2026-07-20 10:15:00', 10500.00, 500.00, 10000.00, 'COMPLETADA'),
(2, 2, '2026-07-20 12:40:00', 17800.00, 0.00, 17800.00, 'COMPLETADA'),
(3, 3, '2026-07-21 18:05:00', 12900.00, 900.00, 12000.00, 'COMPLETADA'),
(4, 2, '2026-07-22 09:30:00', 7500.00, 0.00, 7500.00, 'PENDIENTE'),
(5, 3, '2026-07-22 17:20:00', 22400.00, 1400.00, 21000.00, 'CANCELADA');

INSERT INTO detalle_ventas (
    id_detalle_venta,
    id_venta,
    id_variante,
    cantidad,
    precio_unitario,
    subtotal
) VALUES
-- Venta 1: Labial Mate rojo + Base Líquida
(1, 1, 1, 2, 2800.00, 5600.00),
(2, 1, 5, 1, 4900.00, 4900.00),

-- Venta 2: Esmaltes variados
(3, 2, 7, 3, 2500.00, 7500.00),
(4, 2, 8, 2, 2500.00, 5000.00),
(5, 2, 9, 1, 5300.00, 5300.00),

-- Venta 3: Delineador + Brocha
(6, 3, 6, 2, 1900.00, 3800.00),
(7, 3, 12, 2, 3600.00, 7200.00),

-- Venta 4: Sombra compacta
(8, 4, 10, 1, 5400.00, 5400.00),
(9, 4, 11, 1, 2100.00, 2100.00),

-- Venta 5: Labial + Esmalte + Base
(10, 5, 2, 2, 2800.00, 5600.00),
(11, 5, 7, 2, 2500.00, 5000.00),
(12, 5, 5, 1, 11800.00, 11800.00);

INSERT INTO pagos (
    id_pago,
    id_venta,
    medio_pago,
    importe
) VALUES
(1, 1, 'EFECTIVO', 10000.00),
(2, 2, 'TRANSFERENCIA', 17800.00),
(3, 3, 'EFECTIVO', 8000.00),
(4, 3, 'TRANSFERENCIA', 4000.00),
(5, 4, 'EFECTIVO', 7500.00),
(6, 5, 'TRANSFERENCIA', 21000.00);

SELECT setval(pg_get_serial_sequence('ventas', 'id_venta'), (SELECT COALESCE(MAX(id_venta), 1) FROM ventas), true);
SELECT setval(pg_get_serial_sequence('detalle_ventas', 'id_detalle_venta'), (SELECT COALESCE(MAX(id_detalle_venta), 1) FROM detalle_ventas), true);
SELECT setval(pg_get_serial_sequence('pagos', 'id_pago'), (SELECT COALESCE(MAX(id_pago), 1) FROM pagos), true);

-- =========================================================
-- ETAPA 5: MOVIMIENTOS DE STOCK
-- Objetivo:
-- Registrar entradas, salidas y ajustes para auditar
-- los cambios de inventario.
-- Esto te permite probar:
-- - historial de stock
-- - consultas de auditoría
-- - cálculo de stock anterior / resultante
-- =========================================================

INSERT INTO movimientos_stock (
    id_movimiento,
    id_usuario,
    id_stock,
    fecha_hora,
    tipo_movimiento,
    cantidad,
    stock_anterior,
    stock_resultante,
    motivo
) VALUES
(1, 1, 1, '2026-07-18 09:00:00', 'ENTRADA', 20, 14, 34, 'Reposición de Labial Mate Rojo'),
(2, 1, 9, '2026-07-18 09:10:00', 'ENTRADA', 7, 20, 27, 'Reposición de Base Líquida HD'),
(3, 2, 13, '2026-07-19 11:30:00', 'SALIDA', 5, 50, 45, 'Venta física de Esmalte Rojo'),
(4, 2, 19, '2026-07-19 12:00:00', 'AJUSTE', 2, 28, 30, 'Corrección por conteo físico'),
(5, 1, 23, '2026-07-20 08:45:00', 'ENTRADA', 4, 15, 19, 'Ingreso de Brochas'),
(6, 3, 6, '2026-07-21 10:20:00', 'SALIDA', 2, 12, 10, 'Venta TikTok de Labial Mate Rosa');

SELECT setval(pg_get_serial_sequence('movimientos_stock', 'id_movimiento'), (SELECT COALESCE(MAX(id_movimiento), 1) FROM movimientos_stock), true);

-- =========================================================
-- FIN DEL ARCHIVO
-- =========================================================
