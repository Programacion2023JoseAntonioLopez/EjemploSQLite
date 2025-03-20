import java.sql.*;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // SqlLite
    static final String URL_SQLITE = "jdbc:sqlite:alumnos.db"; // Define la URL para la conexión a la base de datos SQLite

    // MySql
    static final String user = "usuario"; // Define el nombre de usuario para la conexión a MySQL
    static final String password = "12345678"; // Define la contraseña para la conexión a MySQL
    static final String URL_MYSQL = "jdbc:mysql://192.168.18.50:3366/alumnodb"; // Define la URL para la conexión a la base de datos MySQL
    static final String DB_MYSQL = "CREATE TABLE IF NOT EXISTS Alumno (\n" + // Define la sentencia SQL para crear la tabla Alumno en MySQL
            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    nombre VARCHAR(255) NOT NULL,\n" +
            "    email VARCHAR(255),\n" +
            "    telefono VARCHAR(20)\n" +
            ");";

    static final String DB_SQLITE = "CREATE TABLE IF NOT EXISTS Alumno (" + // Define la sentencia SQL para crear la tabla Alumno en SQLite
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL," +
            "email TEXT," +
            "telefono TEXT)";

    // Método para crear la tabla Alumno si no existe
    private static void crearTabla(Connection conexion) throws SQLException {
        try (Statement statement = conexion.createStatement()) { // Crea un objeto Statement para ejecutar sentencias SQL
            statement.executeUpdate(DB_SQLITE); // Ejecuta la sentencia SQL para crear la tabla (usando la definición de SQLite en este caso)
            System.out.println("Tabla Alumno creada correctamente."); // Imprime un mensaje de éxito
        }
    }

    // Método para actualizar un registro de alumno
    private static void actualizarAlumno(Connection conexion, int id, String nombre, String email, String telefono) throws SQLException {
        String query = "UPDATE Alumno SET nombre = ?, email = ?, telefono = ? WHERE id = ?"; // Define la sentencia SQL para actualizar un registro

        try (PreparedStatement statement = conexion.prepareStatement(query)) { // Crea un objeto PreparedStatement para ejecutar sentencias parametrizadas
            //incluimos los parámetros
            statement.setString(1, nombre); // Establece el valor del parámetro 1 (nombre)
            statement.setString(2, email); // Establece el valor del parámetro 2 (email)
            statement.setString(3, telefono); // Establece el valor del parámetro 3 (telefono)
            statement.setInt(4, id); // Establece el valor del parámetro 4 (id)
            //lanzamos la sentencia
            int rowsAffected = statement.executeUpdate(); // Ejecuta la sentencia SQL y obtiene el número de filas afectadas
            //podemos saber las tuplas afectadas
            if (rowsAffected > 0) { // Si se actualizó al menos una fila
                System.out.println("Registro de alumno actualizado correctamente."); // Imprime un mensaje de éxito
            } else { // Si no se encontró ningún registro con el ID proporcionado
                System.out.println("No se encontró ningún registro de alumno con el ID proporcionado."); // Imprime un mensaje de error
            }
        }
    }

    // Método para obtener una lista de todos los alumnos
    private static List<Alumno> listaAlumnos(Connection conexion) throws SQLException {
        List<Alumno> listaAlumnos = new ArrayList<>(); // Crea una lista para almacenar los objetos Alumno

        try (Statement statement = conexion.createStatement(); // Crea un objeto Statement para ejecutar sentencias SQL
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Alumno")) { // Ejecuta la sentencia SQL y obtiene un ResultSet con los resultados
            while (resultSet.next()) { // Itera sobre los resultados
                int id = resultSet.getInt("id"); // Obtiene el valor de la columna id
                String nombre = resultSet.getString("nombre"); // Obtiene el valor de la columna nombre
                String email = resultSet.getString("email"); // Obtiene el valor de la columna email
                String telefono = resultSet.getString("telefono"); // Obtiene el valor de la columna telefono

                // Creamos un objeto Alumno y lo agregamos a la lista
                Alumno alumno = new Alumno(id, nombre, email, telefono); // Crea un objeto Alumno con los valores obtenidos
                listaAlumnos.add(alumno); // Agrega el objeto Alumno a la lista
            }
        }
        return listaAlumnos; // Devuelve la lista de alumnos
    }

    // Método para eliminar un registro de alumno por ID
    private static void borrarAlumno(Connection conexion, int id) throws SQLException {
        String query = "DELETE FROM Alumno WHERE id = ?"; // Define la sentencia SQL para eliminar un registro

        try (PreparedStatement statement = conexion.prepareStatement(query)) { // Crea un objeto PreparedStatement para ejecutar sentencias parametrizadas
            statement.setInt(1, id); // Establece el valor del parámetro 1 (id)
            statement.executeUpdate(); // Ejecuta la sentencia SQL
            System.out.println("Alumno con ID " + id + " eliminado correctamente."); // Imprime un mensaje de éxito
        }
    }

    // Método para consultar todos los alumnos e imprimir sus datos
    private static void consultarAlumnos(Connection conexion) throws SQLException {
        try (Statement statement = conexion.createStatement(); // Crea un objeto Statement para ejecutar sentencias SQL
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Alumno")) { // Ejecuta la sentencia SQL y obtiene un ResultSet con los resultados
            while (resultSet.next()) { // Itera sobre los resultados
                System.out.println("ID: " + resultSet.getInt("id") + // Imprime el ID del alumno
                        ", Nombre: " + resultSet.getString("nombre") + // Imprime el nombre del alumno
                        ", Email: " + resultSet.getString("email") + // Imprime el email del alumno
                        ", Teléfono: " + resultSet.getString("telefono")); // Imprime el teléfono del alumno
            }
        }
    }

    // Método para consultar alumnos por parte del nombre
    private static void consultarAlumnosPorNombre(Connection conexion, String parteNombre) throws SQLException {
        //sentencia parametrizada
        String query = "SELECT * FROM Alumno WHERE nombre LIKE ?"; // Define la sentencia SQL para buscar alumnos por parte del nombre
        try (PreparedStatement statement = conexion.prepareStatement(query)) { // Crea un objeto PreparedStatement para ejecutar sentencias parametrizadas
            //añadimos el parámetro
            statement.setString(1, "%" + parteNombre + "%"); // Establece el valor del parámetro 1 (parte del nombre)
            try (ResultSet resultSet = statement.executeQuery()) { // Ejecuta la sentencia SQL y obtiene un ResultSet con los resultados
                while (resultSet.next()) { // Itera sobre los resultados
                    System.out.println("ID: " + resultSet.getInt("id") + // Imprime el ID del alumno
                            ", Nombre: " + resultSet.getString("nombre") + // Imprime el nombre del alumno
                            ", Email: " + resultSet.getString("email") + // Imprime el email del alumno
                            ", Teléfono: " + resultSet.getString("telefono")); // Imprime el teléfono del alumno
                }
            }
        }
    }

    // Método para insertar un nuevo alumno
    private static void insertarAlumno(Connection conexion, String nombre, String email, String telefono) throws SQLException {
        //Indicamos los parámetros mediante ?
        String sqlInsert = "INSERT INTO Alumno (nombre, email, telefono) VALUES (?, ?, ?)"; // Define la sentencia SQL para insertar un nuevo alumno
        try (PreparedStatement statement = conexion.prepareStatement(sqlInsert)) { // Crea un objeto PreparedStatement para ejecutar sentencias parametrizadas
            //Indicamos los valores a insertar
            statement.setString(1, nombre); // Establece el valor del parámetro 1 (nombre)
            statement.setString(2, email); // Establece el valor del parámetro 2 (email)
            statement.setString(3, telefono); // Establece el valor del parámetro 3 (telefono)
            //Lanzamos la sentencia SQL
            statement.executeUpdate(); // Ejecuta la sentencia SQL
            System.out.println("Alumno '" + nombre + "' insertado correctamente."); // Imprime un mensaje de éxito
        }
    }


    public static void main(String[] args) {
        Connection conexion = null;

        try {
            //SqlLite
            conexion = DriverManager.getConnection(URL_SQLITE);
            //MySql
            //conexion = DriverManager.getConnection(URL_MYSQL,user,password);
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