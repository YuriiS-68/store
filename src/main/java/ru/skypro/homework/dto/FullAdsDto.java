package ru.skypro.homework.dto;

import lombok.Data;
import ru.skypro.homework.model.Picture;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;

@Data
public class FullAdsDto {
    @NotBlank(message = "FirstName shouldn`t be blank")
    @Size(min = 5, message = "FirstName should be at least 5 chars")
    private String authorFirstName;

    @NotBlank(message = "FirstName shouldn`t be blank")
    @Size(min = 5, message = "FirstName should be at least 5 chars")
    private String authorLastName;

    @Size(max = 700, message = "Description should be at least 700 chars")
    private String description;

    @NotBlank
    @Email
    private String email;


    private String image;

    @Size(min = 10, message = "Phone should be at least 10 chars")
    private String phone;

    @Min((1))
    private Long pk;

    @Min((1))
    private Integer price;

    @NotBlank
    private String title;

}