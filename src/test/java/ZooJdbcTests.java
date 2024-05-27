import model.Animal;
import database.DatabaseUtils;
import database.CRUDUtils;
import model.Places;
import database.jdbc.DatabaseConnection;
import model.ZooAnimal;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

class ZooJdbcTests {

    @BeforeAll
    static void init() {
        DatabaseUtils.createData();
    }

    @AfterAll
    static void tearDown() {
        DatabaseConnection.closeConnection();
    }

    /**
     * В таблице public.animal ровно 10 записей
     */
    @Test
    void checkRowCountForAnimal() throws SQLException {
        int actualAnimalCountRow = CRUDUtils.getRowCountByTable("animal");
        Assertions.assertEquals(10, actualAnimalCountRow);
    }

    static Stream<Animal> animalProvider() {
        List<Animal> animals = new ArrayList<>();
        for (int id = 1; id <= 10; id++) {
            Animal animal = new Animal();
            animal.setId(id);
            animal.setName("Sharik");
            animal.setAge(10);
            animal.setType(1);
            animal.setSex(1);
            animal.setPlace(1);
            animals.add(animal);
        }
        return animals.stream();
    }

    /**
     * В таблицу public.animal нельзя добавить строку с индексом от 1 до 10 включительно
     */
    @ParameterizedTest
    @MethodSource("animalProvider")
    void insertIndexAnimal(Animal animal) {
        int countBefore = CRUDUtils.getAnimalCountRow();
        Throwable exception = assertThrows(Exception.class, () -> CRUDUtils.insertAnimalData(animal));
        org.assertj.core.api.Assertions.assertThat(exception.getMessage()).contains("PRIMARY KEY ON PUBLIC.ANIMAL(ID)");
        int countAfter = CRUDUtils.getAnimalCountRow();
        assertEquals(countBefore, countAfter);
    }

    /**
     * В таблицу public.workman нельзя добавить строку с name = null
     */
    @Test
    void insertNullToWorkman() {
        Assertions.assertFalse(CRUDUtils.insertWorkmanData());
        System.out.println(CRUDUtils.insertWorkmanData());
    }

    /**
     * Если в таблицу public.places добавить еще одну строку, то в ней будет 6 строк
     */
    @Test
    void insertPlacesCountRow() throws SQLException {
        Places place = new Places(6, 1, 185, "Загон 1");
        CRUDUtils.getPlacesCountRow();
        System.out.println(CRUDUtils.insertPlacesData(place));
        Assertions.assertEquals(6, CRUDUtils.getPlacesCountRow());
    }

    /**
     * В таблице public.zoo всего три записи с name 'Центральный', 'Северный', 'Западный'
     */
    @Test
    void countRowZoo() throws SQLException {
        List<String> expectedNames = Arrays.asList("Центральный", "Северный", "Западный");

        int actualZooCountRow = CRUDUtils.getRowCountByTable("zoo");
        Assertions.assertEquals(3, actualZooCountRow);

        List<String> actualNames = CRUDUtils.getZooNameData();
        assertThat(actualNames, containsInAnyOrder(expectedNames.toArray()));
    }

    /**
     * ДЗ - успешное добавление в таблицу zoo_animal (значения всех полей с FOREIGN KEY есть в соответствующих таблицах)
     */
    @Test
    void insertRowZooAnimal() throws SQLException {
        int initZooAnimalCountRow = CRUDUtils.getRowCountByTable("zoo_animal");
        int zooId = CRUDUtils.getIdByTable("zoo");
        int animalId = CRUDUtils.getIdByTable("animal");
        int workmanId = CRUDUtils.getIdByTable("workman");

        assertTrue(CRUDUtils.insertZooAnimalData(zooId, animalId, workmanId));
        int actualZooAnimalCountRow = CRUDUtils.getRowCountByTable("zoo_animal");
        assertEquals(actualZooAnimalCountRow, initZooAnimalCountRow + 1);
    }

    /**
     * ДЗ - неуспешное добавление в таблицу zoo_animal (значения поля с FOREIGN KEY нет в таблице animal)
     */
    @Test
    void cannotInsertRowZooAnimal() throws SQLException {
        int initZooAnimalCountRow = CRUDUtils.getRowCountByTable("zoo_animal");
        int zooId = CRUDUtils.getIdByTable("zoo");
        int animalMaxId = CRUDUtils.getMaxIdByTable("animal");
        int workmanId = CRUDUtils.getIdByTable("workman");

        Throwable exception = assertThrows(Exception.class, () -> CRUDUtils.insertZooAnimalData(zooId, animalMaxId + 1, workmanId));
        org.assertj.core.api.Assertions.assertThat(exception.getMessage()).contains("PUBLIC.ZOO_ANIMAL FOREIGN KEY(ANIMAL_ID)");
        int actualZooAnimalCountRow = CRUDUtils.getRowCountByTable("zoo_animal");
        assertEquals(actualZooAnimalCountRow, initZooAnimalCountRow);
    }

    /**
     * ДЗ - успешное обновление записи таблицы zoo_animal (значение поля time_apperance меняем на текущую дату-время)
     */
    @Test
    void updateRowZooAnimal() {
        Optional<ZooAnimal> rowToUpdate = CRUDUtils.getZooAnimalData().stream().findFirst();
        if (rowToUpdate.isPresent()) {
            Timestamp newTime = CRUDUtils.updateZooAnimalData(
                    rowToUpdate.get().getZoo_id(),
                    rowToUpdate.get().getAnimal_id(),
                    rowToUpdate.get().getWorkman_id());
            Optional<ZooAnimal> updatedRow = CRUDUtils.getZooAnimalData().stream().findFirst();
            if (updatedRow.isPresent()) {
                assertEquals(updatedRow.get().getTime_apperance(), newTime);
            } else {
                fail(String.format(CRUDUtils.NO_ROWS_MESSAGE, "zoo_animal"));
            }
        } else {
            fail(String.format(CRUDUtils.NO_ROWS_MESSAGE, "zoo_animal"));
        }
    }

    /**
     * ДЗ - успешное удаление записи из таблицы zoo_animal
     */
    @Test
    void deleteRowZooAnimal() throws SQLException {
        int initZooAnimalCountRow = CRUDUtils.getRowCountByTable("zoo_animal");
        Optional<ZooAnimal> rowToDelete = CRUDUtils.getZooAnimalData().stream().findFirst();
        if (rowToDelete.isPresent()) {
            assertTrue(CRUDUtils.deleteZooAnimalData(
                    rowToDelete.get().getZoo_id(),
                    rowToDelete.get().getAnimal_id(),
                    rowToDelete.get().getWorkman_id()));
            int actualZooAnimalCountRow = CRUDUtils.getRowCountByTable("zoo_animal");
            assertEquals(actualZooAnimalCountRow, initZooAnimalCountRow - 1);
        } else {
            fail(String.format(CRUDUtils.NO_ROWS_MESSAGE, "zoo_animal"));
        }
    }
}