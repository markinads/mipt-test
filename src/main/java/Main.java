import java.util.Scanner;
import static java.lang.System.in;

public class Main {
    public static void main(String[] args) {
        boolean success = false;
        DeliveryCalculator calc = new DeliveryCalculator();
        while (!success) {
            try {
                System.out.println("Введите расстояние в километрах (целое число):");
                calc.setDistance(new Scanner(in).nextInt());

                System.out.println("Негабаритный груз? 1 - да, 0 - нет");
                calc.setOversize(new Scanner(in).nextInt());

                System.out.println("Хрупкий груз? 1 - да, 0 - нет");
                calc.setFragile(new Scanner(in).nextInt());

                System.out.println("Степень загруженности (1 - обычная, 2 - повышенная, 3 - высокая, 4 - очень высокая):");
                calc.setWorkload(new Scanner(in).nextInt());

                success = true;
            } catch (NumberFormatException e) {
                System.out.println("Введено некорректное значение, стоимость доставки не может быть рассчитана. Попробуйте еще раз");
            }
        }

        System.out.println("Стоимость доставки составит " + calc.getStringDeliveryCost() +" рублей");
    }
}
