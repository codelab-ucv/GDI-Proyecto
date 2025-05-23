package ucv.codelab.model;

public class SubOrden {

    private int idSubOrden;
    private int idOrden;
    private int idProducto;
    private int cantidad;

    public SubOrden(int idSubOrden, int idOrden, int idProducto, int cantidad) {
        this.idSubOrden = idSubOrden;
        this.idOrden = idOrden;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public int getIdSubOrden() {
        return idSubOrden;
    }

    public void setIdSubOrden(int idSubOrden) {
        this.idSubOrden = idSubOrden;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
