package model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class Items {
    private List<Item> items;
}