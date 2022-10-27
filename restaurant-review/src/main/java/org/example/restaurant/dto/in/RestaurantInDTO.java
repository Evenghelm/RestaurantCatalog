package org.example.restaurant.dto.in;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.restaurant.validator.annotation.TelephoneNumberConstraint;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
public class RestaurantInDTO {
    @NotBlank(message = "пустое имя")
    private final String name;

    //@NotBlank(message = "пустой телефонный номер")
    @TelephoneNumberConstraint(message = "телефонный номер не соответсвует формату")
    private final String telephoneNumber;

    //@Past(message = "будущее")
    @DateTimeFormat(pattern = "YYYY-MM-DD")
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate foundationDate;

    private final String address;
}
