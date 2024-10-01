import java.io.*;
import java.util.*;

public class Main  {
    public static List<String> regiones = List.of("Metropolitana", "Valparaíso", "Biobío");
    public static List<String> comunas = List.of("Santiago", "Providencia", "Las Condes", "Viña del Mar", "Concepción", "Talcahuano");
    public static Map<Integer, Sucursal> sucursales = new HashMap<>();
    public static Map<Integer, Equipo> equipos = new HashMap<>();
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String args[]) throws IOException {
        // Crear mapa de sucursales
        sucursales.put(1, new Sucursal("Metropolitana", "Santiago", "Av. Providencia 1234"));
        sucursales.put(2, new Sucursal("Metropolitana", "Santiago", "Av. Providencia 5678"));
        sucursales.put(3, new Sucursal("Metropolitana", "Santiago", "Av. Providencia 91011"));
        sucursales.put(4, new Sucursal("Metropolitana", "Santiago", "Av. Providencia 121314"));
        sucursales.put(5, new Sucursal("Metropolitana", "Santiago", "Av. Providencia 151617"));

        // Crear equipos
        equipos.put(1, new Equipo(1, 1,"Equipo 1"));
        equipos.put(2, new Equipo(1, 1,"Equipo 2"));
        equipos.put(3, new Equipo(1, 2,"Equipo 3"));
        equipos.put(4, new Equipo(1, 2,"Equipo 4"));
        equipos.put(5, new Equipo(1, 3,"Equipo 5"));

        // Añadir equipos a sucursales
        sucursales.get(1).añadirEquipo(equipos.get(1));
        sucursales.get(1).añadirEquipo(equipos.get(2));
        sucursales.get(2).añadirEquipo(equipos.get(3));
        sucursales.get(2).añadirEquipo(equipos.get(4));
        sucursales.get(3).añadirEquipo(equipos.get(5));


        // Menu
        int opcion = 0;
        while (opcion != 3) {
            System.out.println("\n1. Sucursales");
            System.out.println("2. Equipos");
            System.out.println("3. Salir\n");
            System.out.print("Ingrese una opción: ");
            opcion = scanner.nextInt();
            switch (opcion) {
                case 1:
                    funcSucursal();
                    System.out.println("--------------------------------------------");
                    break;
                case 2:
                    funcEquipo();
                    System.out.println("--------------------------------------------");
                    break;
                case 3:
                    System.out.println("--------------------------------------------");
                    break;
                default:
                    System.out.println("Opción no válida");
                    System.out.println("--------------------------------------------");
                    break;
            }
        }

        scanner.close();
    }

    // Funciones de sucursal
    public static void funcSucursal() {
        while (true) {
            System.out.println("\n1. Listar sucursales");
            System.out.println("2. Listar equipos de una sucursal");
            System.out.println("3. Crear sucursal");
            System.out.println("4. Agregar equipo a sucursal");
            System.out.println("5. Volver\n");
            System.out.print("Ingrese una opción: ");

            int opcion = scanner.nextInt();
            while (opcion < 1 || opcion > 5) {
                System.out.println("Opción no válida");
                System.out.print("Ingrese una opción: ");
                opcion = scanner.nextInt();
            }
            System.out.println();
            switch (opcion) {
                case 1:
                    for(Sucursal sucursal : sucursales.values()) {
                        System.out.println("ID: " + sucursal.getId() + " - " + sucursal.getDireccion() + " - " + sucursal.getComuna());
                    }
                    break;
                case 2:
                    System.out.print("Ingrese el id de la sucursal: ");
                    int idSucursal = scanner.nextInt();
                    Sucursal sucursal = sucursales.get(idSucursal);
                    if (sucursal == null) {
                        System.out.println("Sucursal no encontrada");
                        break;
                    }
                    System.out.println();
                    if(sucursal.getEquipos().isEmpty()) {
                        System.out.println("No hay equipos en esta sucursal");
                        break;
                    }
                    for(Equipo equipo : sucursal.getEquipos()) {
                        System.out.println("ID: " + equipo.getId() + " - " + equipo.getNombre() + " - " + (equipo.isPrestado() ? "Arrendado" : "Disponible"));
                    }
                    break;
                case 3:
                    System.out.print("Regiones disponibles: ");
                    for (String reg : regiones) {
                        System.out.print(reg + " ");
                    }
                    System.out.println();
                    System.out.print("Ingrese la región: ");
                    String region = scanner.next();
                    if (!regiones.contains(region)) {
                        System.out.println("Región no válida");
                        break;
                    }
                    System.out.print("Comunas disponibles: ");
                    for (String com : comunas) {
                        System.out.print(com + " ");
                    }
                    System.out.println();
                    System.out.print("Ingrese la comuna: ");
                    String comuna = scanner.next();
                    if (!comunas.contains(comuna)) {
                        System.out.println("Comuna no válida");
                        break;
                    }
                    System.out.print("Ingrese la dirección: ");
                    scanner.nextLine(); // Consume el newline
                    String direccion = scanner.nextLine(); // Leer toda la línea
                    Sucursal nuevaSucursal = new Sucursal(region, comuna, direccion);
                    sucursales.put(sucursales.size() + 1, nuevaSucursal);
                    System.out.println("Sucursal creada: " + nuevaSucursal.getId() + "-" + nuevaSucursal.getDireccion() + " - " + nuevaSucursal.getComuna());
                    break;
                case 4:
                    System.out.print("Ingrese el id de la sucursal: ");
                    idSucursal = scanner.nextInt();
                    sucursal = sucursales.get(idSucursal);
                    if (sucursal == null) {
                        System.out.println("Sucursal no encontrada");
                        break;
                    }
                    System.out.print("Ingrese el nombre del equipo: ");
                    scanner.nextLine(); // Consume el newline
                    String nombreEquipo = scanner.nextLine(); // Leer toda la línea
                    System.out.print("Ingrese el tipo de equipo (1.- herramienta, 2.- equipo): ");
                    int tipoEquipo = scanner.nextInt();
                    Equipo nuevoEquipo = new Equipo(tipoEquipo, idSucursal, nombreEquipo);
                    equipos.put(nuevoEquipo.getId(), nuevoEquipo);
                    sucursal.añadirEquipo(nuevoEquipo);
                    System.out.println("Equipo creado y añadido a la sucursal" + idSucursal +": " + nuevoEquipo.getId() + " - " + nuevoEquipo.getNombre());
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        }
    }

    // Funciones de equipo
    public static void funcEquipo() {
        while (true) {
            System.out.println("\n1. Listar equipos");
            System.out.println("2. Arrendar equipo");
            System.out.println("3. Devolver equipo");
            System.out.println("4. Volver\n");
            System.out.print("Ingrese una opción: ");

            int opcion = scanner.nextInt();
            while (opcion < 1 || opcion > 4) {
                System.out.println("Opción no válida");
                System.out.print("Ingrese una opción: ");
                opcion = scanner.nextInt();
            }
            System.out.println();
            switch (opcion) {
                case 1:
                    for (Sucursal sucursal : sucursales.values()) {
                        List<Equipo> equiposSucursal = sucursal.getEquipos();
                        if (!equiposSucursal.isEmpty()) {
                            System.out.print(sucursal.getId() + " - " + sucursal.getDireccion() + ": ");
                            for (Equipo equipo : equiposSucursal) {
                                System.out.print(equipo.getId() + " - " + equipo.getNombre() + ", ");
                            }
                            System.out.println();
                        }
                    }
                    break;
                case 2:
                    System.out.print("Ingrese el id del equipo: ");
                    int idEquipo = scanner.nextInt();
                    Equipo equipo = equipos.get(idEquipo);
                    if (equipo == null) {
                        System.out.println("Equipo no encontrado");
                        break;
                    }
                    System.out.print("Ingrese el rut del cliente: ");
                    String rut = scanner.next();
                    equipo.arrendar(rut);
                    break;
                case 3:
                    System.out.print("Ingrese el id del equipo: ");
                    idEquipo = scanner.nextInt();
                    equipo = equipos.get(idEquipo);
                    if (equipo == null) {
                        System.out.println("Equipo no encontrado");
                        break;
                    }
                    equipo.devolver();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        }
    }
}
