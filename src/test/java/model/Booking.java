package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Booking {

    public String firstname;
    public String lastname;
    public int totalprice;
    public Boolean depositpaid;
    public BookingDates bookingdates;
    public String additionalneeds;
}
