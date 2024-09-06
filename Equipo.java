import java.util.*;
import java.time.*;

public class Equipo {
    private static int contadorId = 0;
    private int id, idSucursal, tipo;
    private String nombre;
    private boolean prestado = false;
    private List<Arriendo> arriendos = new ArrayList<>();

    public Equipo(int tipo, int idSucursal, String nombre) {
        this.id = ++contadorId;
        this.idSucursal = idSucursal;
        this.tipo = tipo;
        this.nombre = nombre;
    }

    public void arrendar(String rut) {
        if (this.prestado) {
            System.out.println("El equipo ya está arrendado");
            return;
        }
        this.arriendos.add(new Arriendo(rut, this.id, LocalDate.now(), LocalDate.now().plusDays(7)));
        this.prestado = true;
    }

    public void devolver() {
        if (!this.prestado) {
            System.out.println("El equipo no está arrendado");
            return;
        }
        for (int i = this.arriendos.size() - 1; i >= 0; i--) {
            Arriendo arriendo = this.arriendos.get(i);
            if (arriendo.isActive()) {
                arriendo.setActive(false);
                this.prestado = false;
                return;
            }
        }
    }

    public Arriendo arriendo() {
        return this.arriendos.get(this.arriendos.size() - 1);
    }

    public Arriendo arriendo(int id) {
        return this.arriendos.stream().filter(arriendo -> arriendo.getId() == id).findFirst().orElse(null);
    }


    // Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIdSucursal() {
        return idSucursal;
    }
    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }
    public int getTipo() {
        return tipo;
    }
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public boolean isPrestado() {
        return prestado;
    }
    public void setPrestado(boolean prestado) {
        this.prestado = prestado;
    }
    public List<Arriendo> getArriendos() {
        return arriendos;
    }
    public void setArriendos(List<Arriendo> arriendos) {
        this.arriendos = arriendos;
    }
    public static int getContadorId() {
        return contadorId;
    }
    public static void setContadorId(int contadorId) {
        Equipo.contadorId = contadorId;
    }
}