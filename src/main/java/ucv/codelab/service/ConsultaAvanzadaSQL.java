package ucv.codelab.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ucv.codelab.model.auxiliar.MayorVenta;
import ucv.codelab.model.auxiliar.VentaInfo;
import ucv.codelab.repository.OrdenRepository;
import ucv.codelab.util.SQLiteConexion;

/**
 * Servicio para la ejecución de consultas SQL avanzadas y complejas.
 * 
 * <p>
 * Esta clase proporciona métodos especializados para realizar consultas SQL
 * que van más allá de las operaciones CRUD básicas proporcionadas por los
 * repositorios. Se enfoca en consultas de análisis, reportes y búsquedas
 * complejas que involucran múltiples tablas y agregaciones.
 * </p>
 * 
 * <p>
 * Las consultas implementadas incluyen funcionalidades de:
 * </p>
 * <ul>
 * <li>Análisis de productos más vendidos con filtros dinámicos</li>
 * <li>Búsquedas avanzadas de información de ventas</li>
 * <li>Agregaciones y agrupaciones de datos de múltiples tablas</li>
 * <li>Filtrado flexible por fechas, trabajadores y otros criterios</li>
 * </ul>
 * 
 * <p>
 * <strong>Tablas principales involucradas:</strong>
 * </p>
 * <ul>
 * <li>{@code orden} - Información principal de las órdenes de venta</li>
 * <li>{@code sub_orden} - Detalles de productos y cantidades por orden</li>
 * <li>{@code producto} - Catálogo de productos con precios</li>
 * <li>{@code cliente} - Información de clientes</li>
 * <li>{@code trabajador} - Datos de empleados</li>
 * </ul>
 * 
 * <p>
 * Todos los métodos son estáticos y manejan automáticamente la conexión
 * a la base de datos a través de {@link SQLiteConexion}.
 * </p>
 * 
 * @see MayorVenta
 * @see VentaInfo
 * @see OrdenRepository
 * @see SQLiteConexion
 */
public class ConsultaAvanzadaSQL {

    /**
     * Obtiene los productos más vendidos con filtros dinámicos y opcionales.
     * 
     * <p>
     * Ejecuta una consulta compleja que agrupa las ventas por producto,
     * sumando las cantidades vendidas y calculando el monto total generado
     * por cada producto. Los resultados se ordenan por cantidad vendida
     * de forma descendente.
     * </p>
     * 
     * <p>
     * <strong>Consulta SQL base:</strong>
     * </p>
     * 
     * <pre>
     * SELECT p.nombre_producto, SUM(s.cantidad) as cantidad_vendida,
     *        SUM(s.cantidad * p.precio) as monto_vendido
     * FROM orden o
     * JOIN sub_orden s ON o.id_orden = s.id_orden
     * JOIN producto p ON s.id_producto = p.id_producto
     * WHERE o.id_empresa = ?
     * GROUP BY p.id_producto, p.nombre_producto
     * ORDER BY cantidad_vendida DESC
     * </pre>
     * 
     * <p>
     * <strong>Filtros aplicables:</strong>
     * </p>
     * <ul>
     * <li>Trabajador específico - filtra órdenes por ID de trabajador</li>
     * <li>Rango de fechas - permite análisis de períodos específicos</li>
     * <li>Límite de resultados - controla el número de productos retornados</li>
     * <li>Empresa - siempre requerido para multi-tenancy</li>
     * </ul>
     * 
     * <p>
     * Este método es especialmente útil para generar reportes de productos
     * top, análisis de inventario y estrategias de marketing basadas en
     * datos de ventas reales.
     * </p>
     * 
     * @param idTrabajador     ID específico del trabajador para filtrar ventas
     *                         (null = todos los trabajadores)
     * @param fechaDesde       Fecha de inicio del rango de búsqueda (null = sin
     *                         límite inferior)
     * @param fechaHasta       Fecha de fin del rango de búsqueda (null = sin límite
     *                         superior)
     * @param limiteResultados Número máximo de productos a retornar (null = sin
     *                         límite)
     * @param idEmpresa        ID de la empresa (obligatorio para filtrado
     *                         multi-tenant)
     * @return Lista ordenada de produtos más vendidos con cantidad total y monto
     *         total por producto
     * @throws SQLException si ocurre un error al ejecutar la consulta o conectar
     *                      con la base de datos
     * 
     * @see MayorVenta
     * @see OrdenRepository#DATE_FORMATTER
     */
    public static List<MayorVenta> mayorVentaCantidad(Integer idTrabajador, LocalDate fechaDesde, LocalDate fechaHasta,
            Integer limiteResultados,
            int idEmpresa) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.nombre_producto, SUM(s.cantidad) as cantidad_vendida, ");
        sql.append("SUM(s.cantidad * p.precio) as monto_vendido ");
        sql.append("FROM orden o ");
        sql.append("JOIN sub_orden s ON o.id_orden = s.id_orden ");
        sql.append("JOIN producto p ON s.id_producto = p.id_producto ");
        sql.append("WHERE o.id_empresa = ?");

        // Siempre filtra con la empresa vigente actualmente
        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpresa);

        // Filtrar por ID de trabajador (si se proporciona)
        if (idTrabajador != null) {
            sql.append(" AND o.id_trabajador = ?");
            parametros.add(idTrabajador);
        }

        if (fechaDesde != null) {
            sql.append(" AND o.fecha_orden >= ?");
            parametros.add(fechaDesde.format(OrdenRepository.DATE_FORMATTER));
        }

        if (fechaHasta != null) {
            sql.append(" AND o.fecha_orden <= ?");
            parametros.add(fechaHasta.format(OrdenRepository.DATE_FORMATTER));
        }

        sql.append(" GROUP BY p.id_producto, p.nombre_producto");
        sql.append(" ORDER BY cantidad_vendida DESC");

        if (limiteResultados != null) {
            sql.append(" LIMIT ?");
            parametros.add(limiteResultados);
        }

        List<MayorVenta> topVentas = new ArrayList<>();

        try (PreparedStatement stmt = SQLiteConexion.getInstance().getConexion().prepareStatement(sql.toString())) {
            // Asignar parámetros dinámicamente
            for (int i = 0; i < parametros.size(); i++) {
                Object param = parametros.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MayorVenta registro = new MayorVenta(
                            rs.getString("nombre_producto"),
                            rs.getInt("cantidad_vendida"),
                            rs.getDouble("monto_vendido"));
                    topVentas.add(registro);
                }
            }
        }
        return topVentas;
    }

    /**
     * Realiza búsquedas avanzadas de información de ventas con múltiples filtros
     * opcionales.
     * 
     * <p>
     * Ejecuta una consulta que combina información de órdenes, clientes y
     * trabajadores
     * para proporcionar una vista completa de las transacciones de venta. Soporta
     * filtrado flexible por diversos criterios para facilitar la búsqueda y
     * análisis
     * de datos históricos.
     * </p>
     * 
     * <p>
     * <strong>Consulta SQL base:</strong>
     * </p>
     * 
     * <pre>
     * SELECT o.id_orden, c.nombre_cliente, t.nombre_trabajador, o.fecha_orden
     * FROM orden o
     * INNER JOIN cliente c ON o.id_cliente = c.id_cliente
     * INNER JOIN trabajador t ON o.id_trabajador = t.id_trabajador
     * WHERE o.id_empresa = ?
     * ORDER BY o.fecha_orden DESC
     * </pre>
     * 
     * <p>
     * <strong>Tipos de filtros soportados:</strong>
     * </p>
     * <ul>
     * <li><strong>ID específico</strong> - búsqueda exacta por número de orden</li>
     * <li><strong>Cliente</strong> - búsqueda parcial por nombre (LIKE)</li>
     * <li><strong>Trabajador</strong> - búsqueda parcial por nombre (LIKE)</li>
     * <li><strong>Rango de fechas</strong> - filtrado por período específico</li>
     * <li><strong>Empresa</strong> - filtrado obligatorio para multi-tenancy</li>
     * </ul>
     * 
     * <p>
     * Los resultados se ordenan por fecha de orden de forma descendente,
     * mostrando las ventas más recientes primero. Este método es ideal
     * para interfaces de búsqueda, reportes de ventas y auditoría de transacciones.
     * </p>
     * 
     * @param idOrden    ID específico de orden para búsqueda exacta (null = no
     *                   filtrar por ID)
     * @param cliente    Nombre del cliente para filtrado parcial case-sensitive
     *                   (null o vacío = no filtrar)
     * @param trabajador Nombre del trabajador para filtrado parcial case-sensitive
     *                   (null o vacío = no filtrar)
     * @param fechaDesde Fecha de inicio del rango de búsqueda inclusive (null = sin
     *                   límite inferior)
     * @param fechaHasta Fecha de fin del rango de búsqueda inclusive (null = sin
     *                   límite superior)
     * @param idEmpresa  ID de la empresa para filtrado multi-tenant (obligatorio)
     * @return Lista de información de ventas ordenada por fecha descendente que
     *         coincide con los filtros aplicados
     * @throws SQLException si ocurre un error al ejecutar la consulta o conectar
     *                      con la base de datos
     * 
     * @see VentaInfo
     * @see OrdenRepository#DATE_FORMATTER
     */
    public static List<VentaInfo> buscarVentas(Integer idOrden, String cliente, String trabajador,
            LocalDate fechaDesde, LocalDate fechaHasta, int idEmpresa) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.id_orden, c.nombre_cliente, t.nombre_trabajador, o.fecha_orden FROM orden o ");
        sql.append("INNER JOIN cliente c ON o.id_cliente = c.id_cliente ");
        sql.append("INNER JOIN trabajador t ON o.id_trabajador = t.id_trabajador ");
        sql.append("WHERE o.id_empresa = ?");

        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpresa);

        // Filtro por ID de orden (si se proporciona)
        if (idOrden != null) {
            sql.append(" AND o.id_orden = ?");
            parametros.add(idOrden);
        }

        // Filtro por nombre de cliente
        if (cliente != null && !cliente.trim().isEmpty()) {
            sql.append(" AND c.nombre_cliente LIKE ?");
            parametros.add("%" + cliente.trim() + "%");
        }

        // Filtro por nombre de trabajador
        if (trabajador != null && !trabajador.trim().isEmpty()) {
            sql.append(" AND t.nombre_trabajador LIKE ?");
            parametros.add("%" + trabajador.trim() + "%");
        }

        // Filtros de fecha
        if (fechaDesde != null) {
            sql.append(" AND DATE(o.fecha_orden) >= ?");
            parametros.add(fechaDesde.format(OrdenRepository.DATE_FORMATTER));
        }

        if (fechaHasta != null) {
            sql.append(" AND DATE(o.fecha_orden) <= ?");
            parametros.add(fechaHasta.format(OrdenRepository.DATE_FORMATTER));
        }

        sql.append(" ORDER BY o.fecha_orden DESC");

        List<VentaInfo> ventas = new ArrayList<>();

        try (PreparedStatement stmt = SQLiteConexion.getInstance().getConexion().prepareStatement(sql.toString())) {
            // Asignar parámetros dinámicamente
            for (int i = 0; i < parametros.size(); i++) {
                Object param = parametros.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VentaInfo venta = new VentaInfo(
                            rs.getInt("id_orden"),
                            rs.getString("nombre_cliente"),
                            rs.getString("nombre_trabajador"),
                            LocalDate.parse(rs.getString("fecha_orden"), OrdenRepository.DATE_FORMATTER));
                    ventas.add(venta);
                }
            }
        }
        return ventas;
    }
}