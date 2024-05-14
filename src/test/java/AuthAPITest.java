import controller.CartController;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import model.Item;
import model.Items;
import model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("API Tests")
@Story("Интернет-магазин https://treeseed.ru/")
@Tag("additionalApi")
class AuthAPITest {
/*    Для корректной работы с корзиной необходимо использовать
      сочетание сформированных сервером токенов в cookie и header */
    public static final Product TREE_PRODUCT = new Product(15204, 1663, 5);

    CartController controller = new CartController();

    @Test
    @DisplayName("Проверка получения токенов")
    void getTokenTest() {
        assertNotNull(controller.getCookieCustomer());
        System.out.println("Cookie Customer: " + controller.getCookieCustomer());

        assertNotNull(controller.getCookieToken());
        System.out.println("Cookie Token: " + controller.getCookieToken());

        assertNotNull(controller.getHeaderToken());
        System.out.println("Header Token: " + controller.getHeaderToken());
    }

    @Test
    @DisplayName("Проверка добавления товара в корзину")
    void addToCartTest() {
        Response addResponse = controller.addToCart(TREE_PRODUCT);
        assertEquals("success", addResponse.jsonPath().get("status"));
        Integer itemId = addResponse.jsonPath().get("cartId");
        Response getResponse = controller.getCart();
        assertEquals(itemId, getResponse.jsonPath().get("CartProducts[0].ShoppingCartItemId"));
        assertEquals(TREE_PRODUCT.getProductId(), getResponse.jsonPath().get("CartProducts[0].ProductId"));
        assertEquals(TREE_PRODUCT.getAmount(), Math.round(getResponse.jsonPath().get("CartProducts[0].Amount")));
    }

    @Test
    @DisplayName("Проверка изменения количества товара")
    void updateCartTest() {
        controller.addToCart(TREE_PRODUCT);
        int itemId = controller.getCart().jsonPath().get("CartProducts[0].ShoppingCartItemId");
        int qtyBefore = Math.round(controller.getCart().jsonPath().get("CartProducts[0].Amount"));
        Item updatedItem = new Item(itemId, qtyBefore + 1);
        Items updatedItems = Items.builder()
                .items(List.of(updatedItem))
                .build();
        assertEquals("success", controller.updateCart(updatedItems).jsonPath().get("status"));
        int qtyAfter = Math.round(controller.getCart().jsonPath().get("CartProducts[0].Amount"));
        assertEquals(qtyBefore + 1, qtyAfter);
    }
}