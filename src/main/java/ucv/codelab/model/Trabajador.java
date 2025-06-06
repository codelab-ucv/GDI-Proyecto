package ucv.codelab.model;

public class Trabajador {

    private int idTrabajador;
    private String nombreTrabajador;
    private String dniTrabajador;
    private String puesto;
    private String tipoLetra;
    private String colorFondo;
    private transient String password;

    public Trabajador(int idTrabajador, String nombreTrabajador, String dniTrabajador, String puesto, String tipoLetra,
            String colorFondo) {
        this.idTrabajador = idTrabajador;
        this.nombreTrabajador = nombreTrabajador;
        this.dniTrabajador = dniTrabajador;
        this.puesto = puesto;
        this.tipoLetra = tipoLetra;
        this.colorFondo = colorFondo;
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    public void setNombreTrabajador(String nombreTrabajador) {
        this.nombreTrabajador = nombreTrabajador;
    }

    public String getDniTrabajador() {
        return dniTrabajador;
    }

    public void setDniTrabajador(String dniTrabajador) {
        this.dniTrabajador = dniTrabajador;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getTipoLetra() {
        return tipoLetra;
    }

    public void setTipoLetra(String tipoLetra) {
        this.tipoLetra = tipoLetra;
    }

    public String getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(String colorFondo) {
        this.colorFondo = colorFondo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
