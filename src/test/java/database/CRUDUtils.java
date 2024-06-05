package database;

import database.jdbc.DatabaseConnection;
import lombok.SneakyThrows;
import model.*;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CRUDUtils {
    private static final String insertAnimal = "INSERT INTO public.animal (id, \"name\", age, \"type\", sex, place) VALUES(?, ?, ?, ?, ?, ?);\n";
    private static final String insertWorkman = "INSERT INTO public.workman (id, \"name\", age, \"position\") VALUES(1, null, 23, 1);\n";

    private static final String insertPlaces = "INSERT INTO public.places (id, \"row\", place_num, \"name\") VALUES(?, ?, ?, ?);\n";

    private static final String getCountAnimalRow = "SELECT count(*) from animal";

    private static final String insertZooAnimal = "INSERT INTO public.zoo_animal (zoo_id, animal_id, time_apperance, workman) VALUES(?, ?, ?, ?);\n";
    private static final String updateZooAnimal = "UPDATE public.zoo_animal SET time_apperance=? WHERE zoo_id=? AND animal_id=? AND workman=?;\n";
    private static final String deleteZooAnimal = "DELETE FROM public.zoo_animal WHERE zoo_id=? AND animal_id=? AND workman=?;\n";

    public static final String NO_ROWS_MESSAGE = "В таблице public.%s нет записей\n";

    public static List<Animal> getAnimalData(String query) {
        List<Animal> animals = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                int type = resultSet.getInt("type");
                int sex = resultSet.getInt("sex");
                int place = resultSet.getInt("place");

                animals.add(new Animal(id, name, age, type, sex, place));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return animals;
    }

    @SneakyThrows
    public static void insertAnimalData(Animal animal) {
        Connection connection = DatabaseConnection.createConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(insertAnimal);
        preparedStatement.setInt(1, animal.getId());
        preparedStatement.setString(2, animal.getName());
        preparedStatement.setInt(3, animal.getAge());
        preparedStatement.setInt(4, animal.getType());
        preparedStatement.setInt(5, animal.getSex());
        preparedStatement.setInt(6, animal.getPlace());
        preparedStatement.executeUpdate();
    }

    public static boolean insertWorkmanData() {
        try {
            Connection connection = DatabaseConnection.createConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate(insertWorkman);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean insertPlacesData(Places place){
        try {
            Connection connection = DatabaseConnection.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertPlaces);
            preparedStatement.setInt(1, place.getId());
            preparedStatement.setInt(2, place.getRow());
            preparedStatement.setInt(3, place.getPlace_num());
            preparedStatement.setString(4, place.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public static int getCountRowByTable(String tableName) throws SQLException {
        Connection connection = DatabaseConnection.createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT count (*) from " + tableName);
        resultSet.next();
        int count = resultSet.getInt("count(*)");
        System.out.printf("В таблице public.%s ровно %s записей%n", tableName, count);
        return count;
    }

    public static int getIdByTable(String tableName) throws SQLException {
        Connection connection = DatabaseConnection.createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT id from " + tableName);
        resultSet.next();
        int id = resultSet.getInt("id");
        System.out.printf("В таблице public.%s есть запись с id = %s%n", tableName, id);
        return id;
    }

    public static int getMaxIdByTable(String tableName) throws SQLException {
        Connection connection = DatabaseConnection.createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(id) from " + tableName);
        resultSet.next();
        int id = resultSet.getInt("MAX(id)");
        System.out.printf("В таблице public.%s максимальный id = %s%n", tableName, id);
        return id;
    }

    public static int getZooCountRow() throws SQLException {
        Connection connection = DatabaseConnection.createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT count (*) from zoo");
        resultSet.next();
        int count = resultSet.getInt("count(*)");
        System.out.println(resultSet.getInt("count(*)"));
        return count;
    }

    @SneakyThrows
    public static int getAnimalCountRow() {
        Connection connection = DatabaseConnection.createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(getCountAnimalRow);
        resultSet.next();
        return resultSet.getInt("count(*)");
    }

    public static int getRowCountByTable(String tableName) throws SQLException {
        Connection connection = DatabaseConnection.createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT count (*) from " + tableName);
        resultSet.next();
        int count = resultSet.getInt("count(*)");
        System.out.printf("Table public.%s has exact %s rows%n", tableName, count);
        return count;
    }

    public static int getPlacesCountRow() throws SQLException {
        Connection connection = DatabaseConnection.createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT count (*) from places");
        resultSet.next();
        int count = resultSet.getInt("count(*)");
        System.out.println(resultSet.getInt("count(*)"));
        return count;
    }

    public static List<String> getZooNameData() {
        String name;
        List<String> zoo = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id,  \"name\" from public.zoo");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                name = resultSet.getString("name");
                System.out.println(name);
                zoo.add(name);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return zoo;
    }

    @SneakyThrows
    public static boolean insertZooAnimalData(int zooId, int animalId, int workmanId) {
        Timestamp time_appearence = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        Connection connection = DatabaseConnection.createConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(insertZooAnimal);
        preparedStatement.setInt(1, zooId);
        preparedStatement.setInt(2, animalId);
        preparedStatement.setTimestamp(3, time_appearence);
        preparedStatement.setInt(4, workmanId);
        System.out.printf("В таблицу public.zoo_animal пытаемся добавить запись с zoo_id = %s, animal_id = %s, time_apperance = %s, workman_id = %s%n",
                zooId, animalId, time_appearence, workmanId);
        preparedStatement.executeUpdate();
        System.out.printf("В таблицу public.zoo_animal добавлена запись с zoo_id = %s, animal_id = %s, time_apperance = %s, workman_id = %s%n",
                zooId, animalId, time_appearence, workmanId);
        return true;
    }

    @SneakyThrows
    public static Timestamp updateZooAnimalData(int zooId, int animalId, int workmanId) {
        Timestamp time_appearence = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        Connection connection = DatabaseConnection.createConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(updateZooAnimal);
        preparedStatement.setTimestamp(1, time_appearence);
        preparedStatement.setInt(2, zooId);
        preparedStatement.setInt(3, animalId);
        preparedStatement.setInt(4, workmanId);
        System.out.printf("В таблице public.zoo_animal пытаемся изменить time_apperance на %s для записи с zoo_id = %s, animal_id = %s, workman_id = %s%n",
                time_appearence, zooId, animalId, workmanId);
        preparedStatement.executeUpdate();
        System.out.printf("В таблице public.zoo_animal изменено time_apperance на %s для записи с zoo_id = %s, animal_id = %s, workman_id = %s%n",
                time_appearence, zooId, animalId, workmanId);
        return time_appearence;
    }

    @SneakyThrows
    public static boolean deleteZooAnimalData(int zooId, int animalId, int workmanId) {
        Connection connection = DatabaseConnection.createConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(deleteZooAnimal);
        preparedStatement.setInt(1, zooId);
        preparedStatement.setInt(2, animalId);
        preparedStatement.setInt(3, workmanId);
        System.out.printf("Из таблицы public.zoo_animal пытаемся удалить запись с zoo_id = %s, animal_id = %s, workman_id = %s%n",
                zooId, animalId, workmanId);
        preparedStatement.executeUpdate();
        System.out.printf("Из таблицы public.zoo_animal удалена запись с zoo_id = %s, animal_id = %s, workman_id = %s%n",
                zooId, animalId, workmanId);
        return true;
    }

    public static List<ZooAnimal> getZooAnimalData() {
        List<ZooAnimal> zooAnimals = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from public.zoo_animal");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                zooAnimals.add(new ZooAnimal(
                        resultSet.getInt("zoo_id"),
                        resultSet.getInt("animal_id"),
                        resultSet.getTimestamp("time_apperance"),
                        resultSet.getInt("workman")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return zooAnimals;
    }
}