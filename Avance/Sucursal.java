import java.util.*;

public class Sucursal {
    private static int contadorId = 0;

    private int id;
    private String region, comuna, direccion;
    private List<Equipo> equipos = new ArrayList<>();

    public Sucursal(String region, String comuna, String direccion) {
        this.id = ++contadorId;
        this.region = region;
        this.comuna = comuna;
        this.direccion = direccion;
    }

    // Funcion añadir equipo con sobrecarga, para añadir un solo equipo o una lista de equipos
    public void añadirEquipo(Equipo equipo) {
        equipos.add(equipo);
    }

    public void añadirEquipo(ArrayList<Equipo> equipos) {
        this.equipos.addAll(equipos);
    }

    public void devolverEquipo(int id) {
        for (Equipo equipo : equipos) {
            if (equipo.getId() == id) {
                equipo.devolver();
                break;
            }
        }
    }
    public void eliminarEquipo(Equipo equipo) {
        equipos.remove(equipo);
    }

    public void eliminarEquipo(int id) {
        for (Equipo equipo : equipos) {
            if (equipo.getId() == id) {
                equipos.remove(equipo);
                break;
            }
        }
    }

    public ArrayList<Equipo> equiposDisponibles() {
        ArrayList<Equipo> disponibles = new ArrayList<>();
        for (Equipo equipo : equipos) {
            if (!equipo.isPrestado()) {
                disponibles.add(equipo);
            }
        }
        return disponibles;
    }

    public ArrayList<Equipo> equiposDisponibles(int tipo) {
        ArrayList<Equipo> disponibles = new ArrayList<>();
        for (Equipo equipo : equipos) {
            if (!equipo.isPrestado() && equipo.getTipo() == tipo) {
                disponibles.add(equipo);
            }
        }
        return disponibles;
    }

    public void arrendarEquipo(int id, String rut) {
        for (Equipo equipo : equipos) {
            if (equipo.getId() == id) {
                equipo.arrendar(rut);
                break;
            }
        }
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }
}
