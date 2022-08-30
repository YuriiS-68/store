package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class FullAdsDto {
    @NotBlank(message = "FirstName shouldn`t be blank")
    @Size(min = 3, message = "FirstName should be at least 3 chars")
    private String authorFirstName;

    @NotBlank(message = "FirstName shouldn`t be blank")
    @Size(min = 3, message = "FirstName should be at least 3 chars")
    private String authorLastName;

    @Size(max = 700, message = "Description should be at least 700 chars")
    private String description;

    @NotBlank
    @Email
    private String email;


    private String image;

    @Pattern(regexp = "((\\d{1,3}?|\\+\\d\\s?|\\+?|\\+\\d{3}\\s?|\\(?|-?)?\\d{2}(\\(?\\d{2}\\)?[\\- ]?)?[\\d\\- ]{7,10})")
    private String phone;

    @Min((1))
    private Long pk;

    @Min((1))
    private Integer price;

    @NotBlank
    private String title;

}