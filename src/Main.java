import java.sql.*;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal que demuestra operaciones básicas CRUD sobre una base de datos
 * SQLite utilizando JDBC. Permite crear una tabla de alumnos, insertar registros,
 * consultarlos, actualizarlos y eliminarlos.
 *
 * La tabla utilizada es Alumno con los campos:
 * id, nombre, email y telefono.
 */
public class Main {

    /**
     * URL de conexión a la base de datos SQLite.
     */
    static final String URL_SQLITE = "jdbc:sqlite:alumnos.db";

    /**
     * Sentencia SQL para crear la tabla Alumno en SQLite si no existe.
     */
    static final String DB_SQLITE = "CREATE TABLE IF NOT EXISTS Alumno (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL," +
            "email TEXT," +
            "telefono TEXT)";

    /**
     * Crea la tabla Alumno en la base de datos si no existe.
     *
     * @param conexion conexión activa a la base de datos
     * @throws SQLException si ocurre un error al ejecutar la sentencia SQL
     */
    private static void crearTabla(Connection conexion) throws SQLException {
        try (Statement statement = conexion.createStatement()) {
            statement.executeUpdate(DB_SQLITE);
            System.out.println("Tabla Alumno creada correctamente.");
        }
    }

    /**
     * Actualiza los datos de un alumno existente.
     *
     * @param conexion conexión a la base de datos
     * @param id identificador del alumno a actualizar
     * @param nombre nuevo nombre del alumno
     * @param email nuevo email del alumno
     * @param telefono nuevo teléfono del alumno
     * @throws SQLException si ocurre un error durante la actualización
     */
    private static void actualizarAlumno(Connection conexion, int id, String nombre, String email, String telefono) throws SQLException {

        String query = "UPDATE Alumno SET nombre = ?, email = ?, telefono = ? WHERE id = ?";

        try (PreparedStatement statement = conexion.prepareStatement(query)) {

            statement.setString(1, nombre);
            statement.setString(2, email);
            statement.setString(3, telefono);
            statement.setInt(4, id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Registro de alumno actualizado correctamente.");
            } else {
                System.out.println("No se encontró ningún registro de alumno con el ID proporcionado.");
            }
        }
    }

    /**
     * Obtiene una lista con todos los alumnos almacenados en la base de datos.
     *
     * @param conexion conexión activa a la base de datos
     * @return lista de objetos {@link Alumno}
     * @throws SQLException si ocurre un error durante la consulta
     */
    private static List<Alumno> listaAlumnos(Connection conexion) throws SQLException {

        List<Alumno> listaAlumnos = new ArrayList<>();

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Alumno")) {

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String email = resultSet.getString("email");
                String telefono = resultSet.getString("telefono");

                Alumno alumno = new Alumno(id, nombre, email, telefono);
                listaAlumnos.add(alumno);
            }
        }

        return listaAlumnos;
    }

    /**
     * Elimina un alumno de la base de datos utilizando su ID.
     *
     * @param conexion conexión a la base de datos
     * @param id identificador del alumno a eliminar
     * @throws SQLException si ocurre un error durante la eliminación
     */
    private static void borrarAlumno(Connection conexion, int id) throws SQLException {

        String query = "DELETE FROM Alumno WHERE id = ?";

        try (PreparedStatement statement = conexion.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();

            System.out.println("Alumno con ID " + id + " eliminado correctamente.");
        }
    }
    /**
     * Elimina todos los alumnos de la tabla Alumno.
     *
     * @param conexion conexión activa a la base de datos
     * @throws SQLException si ocurre un error durante la ejecución de la sentencia SQL
     */
    private static void borrarTodosAlumnos(Connection conexion) throws SQLException {
        String query = "DELETE FROM Alumno";

        try (Statement statement = conexion.createStatement()) {
            int filasBorradas = statement.executeUpdate(query);

            System.out.println("Se han eliminado " + filasBorradas + " alumnos de la base de datos.");
        }
    }
    /**
     * Consulta todos los alumnos de la base de datos y muestra sus datos por consola.
     *
     * @param conexion conexión activa a la base de datos
     * @throws SQLException si ocurre un error durante la consulta
     */
    private static void consultarAlumnos(Connection conexion) throws SQLException {

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Alumno")) {

            while (resultSet.next()) {

                System.out.println(
                        "ID: " + resultSet.getInt("id") +
                                ", Nombre: " + resultSet.getString("nombre") +
                                ", Email: " + resultSet.getString("email") +
                                ", Teléfono: " + resultSet.getString("telefono")
                );
            }
        }
    }

    /**
     * Consulta alumnos cuyo nombre contenga una cadena determinada.
     *
     * @param conexion conexión a la base de datos
     * @param parteNombre cadena que debe contener el nombre del alumno
     * @throws SQLException si ocurre un error durante la consulta
     */
    private static void consultarAlumnosPorNombre(Connection conexion, String parteNombre) throws SQLException {

        String query = "SELECT * FROM Alumno WHERE nombre LIKE ?";

        try (PreparedStatement statement = conexion.prepareStatement(query)) {

            statement.setString(1, "%" + parteNombre + "%");

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    System.out.println(
                            "ID: " + resultSet.getInt("id") +
                                    ", Nombre: " + resultSet.getString("nombre") +
                                    ", Email: " + resultSet.getString("email") +
                                    ", Teléfono: " + resultSet.getString("telefono")
                    );
                }
            }
        }
    }

    /**
     * Inserta un nuevo alumno en la base de datos.
     *
     * @param conexion conexión activa a la base de datos
     * @param nombre nombre del alumno
     * @param email correo electrónico del alumno
     * @param telefono teléfono del alumno
     * @throws SQLException si ocurre un error durante la inserción
     */
    private static void insertarAlumno(Connection conexion, String nombre, String email, String telefono) throws SQLException {

        String sqlInsert = "INSERT INTO Alumno (nombre, email, telefono) VALUES (?, ?, ?)";

        try (PreparedStatement statement = conexion.prepareStatement(sqlInsert)) {

            statement.setString(1, nombre);
            statement.setString(2, email);
            statement.setString(3, telefono);

            statement.executeUpdate();

            System.out.println("Alumno '" + nombre + "' insertado correctamente.");
        }
    }
    /**
     * Obtiene una lista de alumnos cuyo nombre comienza por una letra determinada.
     *
     * Realiza una consulta SQL sobre la tabla {@code Alumno} utilizando la cláusula
     * {@code LIKE} para filtrar los nombres que comienzan por la letra indicada.
     * El resultado se transforma en objetos {@link Alumno} que se almacenan en una lista.
     *
     * @param conexion conexión activa a la base de datos
     * @param letra letra inicial por la que debe comenzar el nombre del alumno
     * @return una {@link ArrayList} con los alumnos cuyo nombre empieza por la letra indicada.
     *         Si no hay coincidencias, devuelve una lista vacía.
     * @throws SQLException si ocurre algún error durante la ejecución de la consulta SQL
     */
    private static ArrayList<Alumno> obtenerAlumnosPorInicial(Connection conexion, String letra) throws SQLException {

        ArrayList<Alumno> listaAlumnos = new ArrayList<>();

        String query = "SELECT * FROM Alumno WHERE nombre LIKE ?";

        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            // la letra indicada al inicio
            statement.setString(1, letra + "%");

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    int id = resultSet.getInt("id");
                    String nombre = resultSet.getString("nombre");
                    String email = resultSet.getString("email");
                    String telefono = resultSet.getString("telefono");

                    Alumno alumno = new Alumno(id, nombre, email, telefono);
                    listaAlumnos.add(alumno);
                }
            }
        }

        return listaAlumnos;
    }
    /**
     * Método principal del programa.
     * Establece la conexión con la base de datos, crea la tabla si no existe
     * y ejecuta varias operaciones de ejemplo: inserción, consulta y actualización.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {

        try (
                Connection conexion = DriverManager.getConnection(URL_SQLITE);
                Scanner sc = new Scanner(System.in)
        ) {

            // Crear tabla si no existe
            crearTabla(conexion);

            // Preguntar si se quieren borrar todos los alumnos
            System.out.print("¿Quieres borrar todos los alumnos? (s/n): ");
            String borrar = sc.nextLine();

            if (borrar.equalsIgnoreCase("s")) {
                borrarTodosAlumnos(conexion);
            }

            // Preguntar si se quieren insertar alumnos de ejemplo
            System.out.print("¿Quieres insertar alumnos de ejemplo? (s/n): ");
            String insertar = sc.nextLine();

            if (insertar.equalsIgnoreCase("s")) {
                insertarAlumno(conexion, "Juan Pérez", "juan@example.com", "123456789");
                insertarAlumno(conexion, "María López", "maria@example.com", "987654321");
                insertarAlumno(conexion, "Carlos García", "carlos@example.com", "555555555");
                insertarAlumno(conexion, "Laura Martínez", "laura@example.com", "777777777");
                insertarAlumno(conexion, "Andrea Pérez", "andrea@example.com", "999999999");
            }

            // Mostrar alumnos actuales
            System.out.println("\nLista de alumnos:");
            consultarAlumnos(conexion);

            // Buscar alumnos por nombre
            System.out.println("\nAlumnos con nombre que contiene 'Pérez':");
            consultarAlumnosPorNombre(conexion, "Pérez");

            // Recuperar alumnos en una lista
            System.out.println("\nLista de alumnos:");
            List<Alumno> alumnos = listaAlumnos(conexion);
            alumnos.forEach(System.out::println);

            // Buscar alumnos cuya inicial sea "A"
            System.out.println("\nAlumnos cuyo nombre empieza por 'A':");

            ArrayList<Alumno> alumnosA = obtenerAlumnosPorInicial(conexion, "A");

            // Mostrar resultados
            if (alumnosA.isEmpty()) {
                System.out.println("No se encontraron alumnos con esa inicial.");
            } else {
                alumnosA.forEach(System.out::println);
            }

        } catch (SQLException e) {
            System.err.println("Error al trabajar con la base de datos:");
            e.printStackTrace();
        }
    }
}