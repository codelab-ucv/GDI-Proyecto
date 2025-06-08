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
 * Permite consultas de SQL más complejas que las CRUD de las clases Repository
 */
public class ConsultaAvanzadaSQL {

    /**
     * Obtiene los productos más vendidos con filtros dinámicos, agrupando y sumando
     * las cantidades vendidas por producto desde múltiples órdenes
     * 
     * @param idTrabajador     ID específico del trabajador para filtrar (opcional)
     * @param fechaDesde       Fecha de inicio del rango de búsqueda (opcional)
     * @param fechaHasta       Fecha de fin del rango de búsqueda (opcional)
     * @param limiteResultados Número máximo de productos a retornar (opcional)
     * @param idEmpresa        ID de la empresa (requerido)
     * @return Lista de productos más vendidos con cantidad total y monto total por
     *         producto
     * @throws SQLException si ocurre un error en la consulta
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
     * Busca información de ventas con filtros dinámicos
     * 
     * @param idOrden    ID específico de orden (opcional)
     * @param cliente    Nombre del cliente para filtrar (opcional)
     * @param trabajador Nombre del trabajador para filtrar (opcional)
     * @param fechaDesde Fecha de inicio del rango (opcional)
     * @param fechaHasta Fecha de fin del rango (opcional)
     * @param idEmpresa  ID de la empresa (requerido)
     * @return Lista de información de ventas que coinciden con los filtros
     * @throws SQLException si ocurre un error en la consulta
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
