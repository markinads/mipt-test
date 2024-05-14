import controller.BookingController;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import model.Booking;
import model.BookingDates;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("API Tests")
@Story("POST vs PUT vs PATCH")
@Tag("additionalApi")
public class PostPutPatchTest {
    BookingController controller = new BookingController();

    @Test
    @DisplayName("POST Test")
    void postTest() {
        Booking bookingToCreate = new Booking("FPMI", "Test",3000, true, new BookingDates("2024-06-01", "2024-06-03"), "test needs");

        int bookingId = controller.postBooking(bookingToCreate);

        assertThat(controller.getBooking(bookingId)).
                usingRecursiveComparison().
                isEqualTo(bookingToCreate);
    }

    @Test
    @DisplayName("PUT Test Full Booking")
    void putTestFullBooking() {
        Booking bookingToCreate = new Booking("FPMI", "Test",3000, true, new BookingDates("2024-06-01", "2024-06-03"), "test needs");
        Booking bookingToUpdate = new Booking("FPMI2", "Test2",10000, false, new BookingDates("2025-06-01", "2025-06-03"), "Breakfast");

        int bookingId = controller.postBooking(bookingToCreate);
        assertThat(controller.putBooking(bookingId, bookingToUpdate)).isEqualTo(200);

        assertThat(controller.getBooking(bookingId)).
                usingRecursiveComparison().
                isEqualTo(bookingToUpdate);
    }

    @Test
    @DisplayName("PUT Test one field")
    void putTestOneField() {
        Booking bookingToCreate = new Booking("FPMI", "Test",3000, true, new BookingDates("2024-06-01", "2024-06-03"), "test needs");
        int newPrice = 5000;
        Booking bookingToUpdate = new Booking(null, null, newPrice, null, null, null);

        int bookingId = controller.postBooking(bookingToCreate);

        assertThat(controller.putBooking(bookingId, bookingToUpdate)).isEqualTo(400);
    }


    @Test
    @DisplayName("PATCH Test")
    void patchTest() {
        Booking bookingToCreate = new Booking("FPMI", "Test",3000, true, new BookingDates("2024-06-01", "2024-06-03"), "test needs");
        int newPrice = 5000;
        Booking bookingToUpdate = new Booking(null, null, newPrice, null, null, null);

        int bookingId = controller.postBooking(bookingToCreate);
        Booking expectedBooking = controller.getBooking(bookingId);
        expectedBooking.setTotalprice(newPrice);

        assertThat(controller.patchBooking(bookingId, bookingToUpdate)).isEqualTo(200);

        assertThat(controller.getBooking(bookingId)).
                usingRecursiveComparison().
                isEqualTo(expectedBooking);
    }


}
