public class DeliveryCalculator {
    public static final double DEFAULT_COST = 400;
    public static final double DISTANCE_0_2_ADDING = 50;
    public static final double DISTANCE_2_10_ADDING = 100;
    public static final double DISTANCE_10_30_ADDING = 200;
    public static final double DISTANCE_OVER_30_ADDING = 300;
    public static final double OVERSIZE_ADDING = 200;
    public static final double NO_OVERSIZE_ADDING = 100;
    public static final double FRAGILE_ADDING = 300;
    public static final double FRAGILE_MAX_DISTANCE = 30;
    public static final double LOAD_NORMAL_RATIO = 1;
    public static final double LOAD_RAISED_RATIO = 1.2;
    public static final double LOAD_HIGH_RATIO = 1.4;
    public static final double LOAD_VERY_HIGH_RATIO = 1.6;

    private int distance;
    private boolean isOversize;
    private boolean isFragile;
    private double loadRatio;

    public DeliveryCalculator() {
    }

    public DeliveryCalculator(int distance, int oversize, int fragile, int load) {
        setDistance(distance);
        setOversize(oversize);
        setFragile(fragile);
        setWorkload(load);
    }

    public void setDistance(int distance) {
        if (distance < 0) {
            throw new NumberFormatException();
        }
        this.distance = distance;
    }

    public void setOversize(int oversize) {
        isOversize = oversize == 1 ? true : false;
    }

    public void setFragile(int fragile) {
        isFragile = fragile == 1 ? true : false;
        if (distance > FRAGILE_MAX_DISTANCE && isFragile) {
            System.out.println("Хрупкий груз нельзя перевозить далее, чем на 30км! Задайте другие параметры.");
            throw new NumberFormatException();
        }
    }

    public void setWorkload(int load) {
        switch (load) {
            case 1:
                loadRatio = LOAD_NORMAL_RATIO;
                break;
            case 2:
                loadRatio = LOAD_RAISED_RATIO;
                break;
            case 3:
                loadRatio = LOAD_HIGH_RATIO;
                break;
            case 4:
                loadRatio = LOAD_VERY_HIGH_RATIO;
                break;
            default:
                throw new NumberFormatException("Ожидается значение от 1 до 4!");
        }
    }

    public double getDeliveryCost() {
        double cost = 0;

        if (distance <= 2) {
            cost += DISTANCE_0_2_ADDING;
        } else if (distance <= 10) {
            cost += DISTANCE_2_10_ADDING;
        } else if (distance <= 30) {
            cost += DISTANCE_10_30_ADDING;
        } else {
            cost += DISTANCE_OVER_30_ADDING;
        }

        if (isOversize) {
            cost += OVERSIZE_ADDING;
        } else {
            cost += NO_OVERSIZE_ADDING;
        }

        if (isFragile) {
            cost += FRAGILE_ADDING;
        }

        cost *= loadRatio;

        if (cost < DEFAULT_COST) {
            cost = DEFAULT_COST;
        }

        return cost;
    }

    public String getStringDeliveryCost() {
        return String.format("%.2f", getDeliveryCost());
    }
}
