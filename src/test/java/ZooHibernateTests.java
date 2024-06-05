import database.DatabaseUtils;
import database.hibernate.DBHibernateService;

import model.Animal;
import model.Places;
import model.Workman;
import jakarta.persistence.PersistenceException;
import model.ZooAnimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

class ZooHibernateTests {
    DBHibernateService dbHibernateService = new DBHibernateService();

    @BeforeAll
    static void init() {
        DatabaseUtils.createData();
    }

    /**
     * В таблице public.animal ровно 10 записей
     */
    @Test
    void countRowAnimal() {
        Assertions.assertEquals(10, dbHibernateService.getCountRowAnimal());
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
        assertThrows(PersistenceException.class, () -> dbHibernateService.insertAnimal(animal));
    }

    /**
     * В таблицу public.workman нельзя добавить строку с name = null
     */
    @Test
    void insertNullToWorkman() {
        Workman workman = new Workman();
        workman.setId(88);
        workman.setName(null);
        workman.setAge(12);
        workman.setPosition(1);
        assertThrows(PersistenceException.class, () -> dbHibernateService.insertWorkman(workman));
    }

    /**
     * Если в таблицу public.places добавить еще одну строку, то в ней будет 6 строк
     */
    @Test
    void insertPlacesCountRow() {
        int sizeBefore = dbHibernateService.getCountRowPlaces();
        Places places = new Places();
        places.setId(6);
        places.setRow(1);
        places.setPlace_num(185);
        places.setName("Загон 1");
        dbHibernateService.insertPlaces(places);
        Assertions.assertEquals(sizeBefore + 1, dbHibernateService.getCountRowPlaces());
    }

    /**
     * В таблице public.zoo всего три записи с name 'Центральный', 'Северный', 'Западный'
     */
    @Test
    void countRowZoo() {
        List<String> expectedNames = Arrays.asList("Центральный", "Северный", "Западный");

        int actualZooCountRow = dbHibernateService.getCountRowZoo();
        Assertions.assertEquals(3, actualZooCountRow);

        List<String> actualNames = dbHibernateService.getZooNameData();
        assertThat(actualNames, containsInAnyOrder(expectedNames.toArray()));
    }

    /**
     * ДЗ - успешное добавление в таблицу zoo_animal (значения всех полей с FOREIGN KEY есть в соответствующих таблицах)
     */
    @Test
    void insertRowZooAnimal() {
        int initZooAnimalCountRow = dbHibernateService.getRowCountByTable("zoo_animal");
        int zooId = dbHibernateService.getIdByTable("zoo");
        int animalId = dbHibernateService.getIdByTable("animal");
        int workmanId = dbHibernateService.getIdByTable("workman");

        assertTrue(dbHibernateService.insertZooAnimalData(zooId, animalId, workmanId));
        int actualZooAnimalCountRow = dbHibernateService.getRowCountByTable("zoo_animal");
        assertEquals(actualZooAnimalCountRow, initZooAnimalCountRow + 1);
    }

    /**
     * ДЗ - неуспешное добавление в таблицу zoo_animal (значения поля с FOREIGN KEY нет в таблице animal)
     */
    @Test
    void cannotInsertRowZooAnimal() {
        int initZooAnimalCountRow = dbHibernateService.getRowCountByTable("zoo_animal");
        int zooId = dbHibernateService.getIdByTable("zoo");
        int animalMaxId = dbHibernateService.getMaxIdByTable("animal");
        int workmanId = dbHibernateService.getIdByTable("workman");

        Throwable exception = assertThrows(Exception.class, () -> dbHibernateService.insertZooAnimalData(zooId, animalMaxId + 1, workmanId));
        org.assertj.core.api.Assertions.assertThat(exception.getMessage()).contains("PUBLIC.ZOO_ANIMAL FOREIGN KEY(ANIMAL_ID)");
        int actualZooAnimalCountRow = dbHibernateService.getRowCountByTable("zoo_animal");
        assertEquals(actualZooAnimalCountRow, initZooAnimalCountRow);
    }

    /**
     * ДЗ - успешное обновление записи таблицы zoo_animal (значение поля time_apperance меняем на текущую дату-время)
     */
    @Test
    void updateRowZooAnimal() {
        Optional<ZooAnimal> rowToUpdate = dbHibernateService.getZooAnimalData().stream().findFirst();
        if (rowToUpdate.isPresent()) {
            Timestamp newTime = dbHibernateService.updateZooAnimalData(
                    rowToUpdate.get().getZoo_id(),
                    rowToUpdate.get().getAnimal_id(),
                    rowToUpdate.get().getWorkman_id());
            Optional<ZooAnimal> updatedRow = dbHibernateService.getZooAnimalData().stream().findFirst();
            if (updatedRow.isPresent()) {
                assertEquals(updatedRow.get().getTime_apperance(), newTime);
            } else {
                fail(String.format(DBHibernateService.NO_ROWS_MESSAGE, "zoo_animal"));
            }
        } else {
            fail(String.format(DBHibernateService.NO_ROWS_MESSAGE, "zoo_animal"));
        }
    }

    /**
     * ДЗ - успешное удаление записи из таблицы zoo_animal
     */
    @Test
    void deleteRowZooAnimal() {
        int initZooAnimalCountRow = dbHibernateService.getRowCountByTable("zoo_animal");
        Optional<ZooAnimal> rowToDelete = dbHibernateService.getZooAnimalData().stream().findFirst();
        if (rowToDelete.isPresent()) {
            assertTrue(dbHibernateService.deleteZooAnimalData(
                    rowToDelete.get().getZoo_id(),
                    rowToDelete.get().getAnimal_id(),
                    rowToDelete.get().getWorkman_id()));
            int actualZooAnimalCountRow = dbHibernateService.getRowCountByTable("zoo_animal");
            assertEquals(actualZooAnimalCountRow, initZooAnimalCountRow - 1);
        } else {
            fail(String.format(DBHibernateService.NO_ROWS_MESSAGE, "zoo_animal"));
        }
    }
}
