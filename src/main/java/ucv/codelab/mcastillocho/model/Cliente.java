package ucv.codelab.mcastillocho.model;

public class Cliente {
    
    private int idCliente;
    private String nombreCliente;
    private String dniCliente;
    private String telefono;
    private String emailCliente;

    public Cliente(int idCliente, String nombreCliente, String dniCliente, String telefono, String emailCliente) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.dniCliente = dniCliente;
        this.telefono = telefono;
        this.emailCliente = emailCliente;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }
}
