import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryCalculatorTest {

    static Stream<Arguments> provideDistanceParameters() {
        return Stream.of(
                Arguments.arguments(0, "400,00"),
                Arguments.arguments(1, "400,00"),
                Arguments.arguments(2, "400,00"),
                Arguments.arguments(3, "480,00"),
                Arguments.arguments(9, "480,00"),
                Arguments.arguments(10, "480,00"),
                Arguments.arguments(29, "640,00"),
                Arguments.arguments(30, "640,00"),
                Arguments.arguments(31, "800,00"));
    }

    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Проверка расчета стоимости доставки/расстояние")
    @MethodSource("provideDistanceParameters")
    void deliveryCostDistanceTest(int distance, String expectedCost) {
        DeliveryCalculator calc = new DeliveryCalculator(distance, 1,0,4);
        assertEquals(expectedCost, calc.getStringDeliveryCost());
    }

    static Stream<Arguments> provideLoadParameters() {
        return Stream.of(
                Arguments.arguments(1, "600,00"),
                Arguments.arguments(2, "720,00"),
                Arguments.arguments(3, "840,00"),
                Arguments.arguments(4, "960,00"));
    }

    @ParameterizedTest
    @Tag("unit")
    @DisplayName("Проверка расчета стоимости доставки/загрузка")
    @MethodSource("provideLoadParameters")
    void loadCostDistanceTest(int load, String expectedCost) {
        DeliveryCalculator calc = new DeliveryCalculator(30, 0,1,load);
        assertEquals(expectedCost, calc.getStringDeliveryCost());
    }

    @Test
    @DisplayName("Проверка вызываемого исключения")
    @Tag("unit")
    void numberExceptionTest() {
        DeliveryCalculator calc = new DeliveryCalculator();
        assertThrows(NumberFormatException.class, () -> calc.setDistance(-1));
        assertThrows(NumberFormatException.class, () -> calc.setWorkload(-1));
        assertThrows(NumberFormatException.class, () -> calc.setWorkload(5));
        assertThrows(NumberFormatException.class, () -> new DeliveryCalculator(0,0,0,10));
    }

    static Stream<Arguments> provideAllParameters() {
        return Stream.of(
                Arguments.arguments(10, 0, 0, 1, "400,00"),
                Arguments.arguments(50, 1, 0, 1, "500,00"),
                Arguments.arguments(10, 0, 1, 3, "700,00"),
                Arguments.arguments(10, 1, 1, 2, "720,00"),
                Arguments.arguments(30, 1, 1, 4, "1120,00"));
    }

    @ParameterizedTest
    @Tag("smoke")
    @DisplayName("Проверка расчета стоимости доставки/smoke")
    @MethodSource("provideAllParameters")
    void deliveryCostAllTest(int distance, int oversize, int fragile, int load, String expectedCost) {
        DeliveryCalculator calc = new DeliveryCalculator(distance, oversize,fragile,load);
        assertEquals(expectedCost, calc.getStringDeliveryCost());
    }
}
