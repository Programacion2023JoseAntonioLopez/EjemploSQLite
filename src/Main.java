import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static final String URL = "jdbc:sqlite:alumnos.db";

    private static void crearTabla(Connection conexion) throws SQLException {
        try (Statement statement = conexion.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Alumno (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nombre TEXT NOT NULL," +
                            "email TEXT," +
                            "telefono TEXT)"
            );
            System.out.println("Tabla Alumno creada correctamente.");
        }
    }
    private static void actualizarAlumno(Connection conexion, int id, String nombre, String email, String telefono) throws SQLException {
        String query = "UPDATE Alumno SET nombre = ?, email = ?, telefono = ? WHERE id = ?";

        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            //incluimos los parámetros
            statement.setString(1, nombre);
            statement.setString(2, email);
            statement.setString(3, telefono);
            statement.setInt(4, id);
            //lanzamos la sentencia
            int rowsAffected = statement.executeUpdate();
            //podemos saber las tuplas afectadas
            if (rowsAffected > 0) {
                System.out.println("Registro de alumno actualizado correctamente.");
            } else {
                System.out.println("No se encontró ningún registro de alumno con el ID proporcionado.");
            }
        }
    }
    private static List<Alumno> listaAlumnos(Connection conexion) throws SQLException {
        List<Alumno> listaAlumnos = new ArrayList<>();

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Alumno")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String email = resultSet.getString("email");
                String telefono = resultSet.getString("telefono");

                // Creamos un objeto Alumno y lo agregamos a la lista
                Alumno alumno = new Alumno(id, nombre, email, telefono);
                listaAlumnos.add(alumno);
            }
        }
        return listaAlumnos;
    }
    private static void borrarAlumno(Connection conexion, int id) throws SQLException {
        String query = "DELETE FROM Alumno WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Alumno con ID " + id + " eliminado correctamente.");
        }
    }
    private static void consultarAlumnos(Connection conexion) throws SQLException {
        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Alumno")) {
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id") +
                        ", Nombre: " + resultSet.getString("nombre") +
                        ", Email: " + resultSet.getString("email") +
                        ", Teléfono: " + resultSet.getString("telefono"));
            }
        }
    }
    private static void consultarAlumnosPorNombre(Connection conexion, String parteNombre) throws SQLException {
        //sentencia parametrizada
        String  query = "SELECT * FROM Alumno WHERE nombre LIKE ?";
        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            //añadimos el parámetro
            statement.setString(1, "%" + parteNombre + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("ID: " + resultSet.getInt("id") +
                            ", Nombre: " + resultSet.getString("nombre") +
                            ", Email: " + resultSet.getString("email") +
                            ", Teléfono: " + resultSet.getString("telefono"));
                }
            }
        }
    }
    private static void insertarAlumno(Connection conexion, String nombre, String email, String telefono) throws SQLException {
        //Indicamos los parámetros mediante ?
        String sqlInsert = "INSERT INTO Alumno (nombre, email, telefono) VALUES (?, ?, ?)";
        try (PreparedStatement statement = conexion.prepareStatement(sqlInsert)) {
            //Indicamos los valores a insertar
            statement.setString(1, nombre);
            statement.setString(2, email);
            statement.setString(3, telefono);
            //Lanzamos la sentencia SQL
            statement.executeUpdate();
            System.out.println("Alumno '" + nombre + "' insertado correctamente.");
        }
    }
    public static void main(String[] args) {
        Connection conexion = null;

        try {
            conexion = DriverManager.getConnection(URL);
            //creamos la tabla si no existe
            crearTabla(conexion);
            // Insertamos algunos alumnos
            insertarAlumno(conexion, "Juan Pérez", "juan@example.com", "123456789");
            insertarAlumno(conexion, "María López", "maria@example.com", "987654321");
            insertarAlumno(conexion, "Carlos García", "carlos@example.com", "555555555");
            insertarAlumno(conexion, "Laura Martínez", "laura@example.com", "777777777");
            insertarAlumno(conexion, "Andrea Pérez", "andrea@example.com", "999999999");

            //Buscamos los alumnos actuales
            System.out.println("Alumnos:");
            consultarAlumnos(conexion);

            //Buscamos los alumnos con nombre que contiene 'Alumno'
            System.out.println("Alumnos con nombre que contiene Pérez:");
            consultarAlumnosPorNombre(conexion, "Pérez");

            //Actualizamos el alumno con id 1
            System.out.println("Actualizar 1");
            int idAlumno = 1;
            String nuevoNombre = "Juanito Pérez";
            String nuevoEmail = "juan@example.com";
            String nuevoTelefono = "555-1234";

            // Llamamos al método para actualizar el alumno
            actualizarAlumno(conexion, idAlumno, nuevoNombre, nuevoEmail, nuevoTelefono);
            consultarAlumnosPorNombre(conexion,"Juan");

            //Recuperamos los alumnos en una Lista
            System.out.println("Lista de alumnos:");
            List<Alumno> alumnos = listaAlumnos(conexion);
            alumnos.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {//Esta sentencia simpre se ejecuta. Hay que cerrar la conexión
            try {
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}