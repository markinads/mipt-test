package database.hibernate;

import lombok.SneakyThrows;
import model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DBHibernateService {

    private static final String INSERT_ANIMAL = "INSERT INTO animal (id, \"name\", age, \"type\", sex, place) VALUES (%s, '%s', %s, %s, %s, %s)";
    private static final String INSERT_WORKMAN = "INSERT INTO workman (id, \"name\", age, \"position\") VALUES (%s, %s, %s, %s)";
    private static final String INSERT_PLACES = "INSERT INTO places (id, \"row\", place_num, \"name\") VALUES (%s, %s, %s, '%s')";

    private static final String INSERT_ZOO_ANIMAL = "INSERT INTO public.zoo_animal (zoo_id, animal_id, time_apperance, workman) VALUES(%s, %s, '%s', %s);\n";
    private static final String UPDATE_ZOO_ANIMAL = "UPDATE public.zoo_animal SET time_apperance='%s' WHERE zoo_id=%s AND animal_id=%s AND workman=%s;\n";
    private static final String DELETE_ZOO_ANIMAL = "DELETE FROM public.zoo_animal WHERE zoo_id=%s AND animal_id=%s AND workman=%s;\n";

    public static final String NO_ROWS_MESSAGE = "Table public.%s has no rows\n";

    public Animal getAnimalByName() {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();

        return session.createNativeQuery("SELECT id, \"name\", age, \"type\", sex, place FROM animal WHERE \"name\" = 'Пчелка'", Animal.class).getResultList().get(0);

    }

    public Workman getWorkmanById() {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();

        return session.createNativeQuery("SELECT id, \"name\", age, \"position\" from workman where id = '88'", Workman.class).getResultList().get(0);

    }

    public int getCountRowAnimal() {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        int count = session.createNativeQuery("SELECT * from animal ", Animal.class).getResultList().size();
        System.out.printf("Table public.animal has exact %s rows%n", count);
        return count;
    }

    public  int getCountRowZoo() {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        int count = session.createNativeQuery("SELECT * from zoo ", Zoo.class).getResultList().size();
        System.out.println(count);
        return count;
    }

    public List<String> getZooNameData() {
        List<String> names = new ArrayList<>();
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        List<Zoo> zooList = session.createNativeQuery("SELECT * from zoo", Zoo.class).getResultList();
        for (Zoo zoo: zooList) {
            names.add(zoo.getName());
        }
        return names;
    }

    public int getCountRowPlaces() {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        int count = session.createNativeQuery("SELECT * from places", Places.class).getResultList().size();
        System.out.println(count);
        return count;
    }

    public void insertWorkman(Workman workman) {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createNativeQuery(String.format(INSERT_WORKMAN, workman.getId(), workman.getName(), workman.getAge(), workman.getPosition()), Workman.class).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public void insertAnimal(Animal animal) {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createNativeQuery(String.format(INSERT_ANIMAL, animal.getId(), animal.getName(), animal.getAge(), animal.getType(), animal.getSex(), animal.getPlace()), Animal.class).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public void insertPlaces(Places places) {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createNativeQuery(String.format(INSERT_PLACES, places.getId(), places.getRow(), places.getPlace_num(), places.getName()), Places.class).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @SneakyThrows
    public boolean insertZooAnimalData(int zooId, int animalId, int workmanId) {
        Timestamp time_appearence = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createNativeQuery(String.format(INSERT_ZOO_ANIMAL, zooId, animalId, time_appearence, workmanId), ZooAnimal.class).executeUpdate();
        System.out.printf("Try to insert row with zoo_id = %s, animal_id = %s, time_apperance = %s, workman_id = %s in table public.zoo_animal%n",
                zooId, animalId, time_appearence, workmanId);
        session.getTransaction().commit();
        session.close();
        System.out.printf("Successfully insert row with zoo_id = %s, animal_id = %s, time_apperance = %s, workman_id = %s in table public.zoo_animal%n",
                zooId, animalId, time_appearence, workmanId);
        return true;
    }

    @SneakyThrows
    public Timestamp updateZooAnimalData(int zooId, int animalId, int workmanId) {
        Timestamp time_appearence = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createNativeQuery(String.format(UPDATE_ZOO_ANIMAL, time_appearence, zooId, animalId, workmanId), ZooAnimal.class).executeUpdate();
        System.out.printf("Try to update row (set time_apperance to %s) with zoo_id = %s, animal_id = %s, workman_id = %s in table public.zoo_animal%n",
                time_appearence, zooId, animalId, workmanId);
        session.getTransaction().commit();
        session.close();
        System.out.printf("Successfully update row with zoo_id = %s, animal_id = %s, workman_id = %s in table public.zoo_animal%n",
                zooId, animalId, workmanId);
        return time_appearence;
    }

    @SneakyThrows
    public boolean deleteZooAnimalData(int zooId, int animalId, int workmanId) {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createNativeQuery(String.format(DELETE_ZOO_ANIMAL, zooId, animalId, workmanId), ZooAnimal.class).executeUpdate();
        System.out.printf("Try to delete row with zoo_id = %s, animal_id = %s, workman_id = %s from table public.zoo_animal%n",
                zooId, animalId, workmanId);
        session.getTransaction().commit();
        session.close();
        System.out.printf("Successfully delete row with zoo_id = %s, animal_id = %s, workman_id = %s from table public.zoo_animal%n",
                zooId, animalId, workmanId);
        return true;
    }

    public List<ZooAnimal> getZooAnimalData() {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        List<ZooAnimal> zooAnimals = session.createNativeQuery("SELECT * from zoo_animal", ZooAnimal.class).getResultList();
        session.close();
        return zooAnimals;
    }

    public int getRowCountByTable(String table) {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        int count = session.createNativeQuery("SELECT * from " + table, Object.class).getResultList().size();
        session.close();
        System.out.printf("Table public.%s has exact %s rows%n", table, count);
        return count;
    }

    public int getIdByTable(String table) {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        int id = session.createNativeQuery("SELECT id from " + table, Integer.class).getResultList().stream().findFirst().orElse(-1);
        session.close();
        if (id != -1) {
            System.out.printf("Table public.%s has row with id = %s%n", table, id);
        } else {
            System.out.printf(NO_ROWS_MESSAGE, table);
        }
        return id;
    }

    public int getMaxIdByTable(String table) {
        SessionFactory sessionFactory = HibernateSessionFactoryCreator.createSessionFactory();
        Session session = sessionFactory.openSession();
        int id = session.createNativeQuery("SELECT MAX(id) from " + table, Integer.class).getResultList().stream().findFirst().orElse(-1);
        session.close();
        if (id != -1) {
            System.out.printf("In table public.%s max id = %s%n", table, id);
        } else {
            System.out.printf(NO_ROWS_MESSAGE, table);
        }
        return id;
    }
}