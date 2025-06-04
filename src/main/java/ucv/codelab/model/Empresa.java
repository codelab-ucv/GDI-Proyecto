package ucv.codelab.model;

public class Empresa {

    private int idEmpresa;
    private String nombreEmpresa;
    private String ruc;
    private String emailEmpresa;
    private String ubicacion;
    private String logo;

    public Empresa(String nombreEmpresa, String ruc) {
        new Empresa(-1, nombreEmpresa, ruc, null, null, null);
    }

    public Empresa(int idEmpresa, String nombreEmpresa, String ruc, String emailEmpresa, String ubicacion,
            String logo) {
        this.idEmpresa = idEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.ruc = ruc;
        this.emailEmpresa = emailEmpresa;
        this.ubicacion = ubicacion;
        this.logo = logo;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getEmailEmpresa() {
        return emailEmpresa;
    }

    public void setEmailEmpresa(String emailEmpresa) {
        this.emailEmpresa = emailEmpresa;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
