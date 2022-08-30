package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class AdsDto {
    @Min((1))
    private Long idAuthor;

    @NotBlank
    private String title;

    private String filePath;

    /*private Collection<Picture> image;*/

    private String image;

    @Min((1))
    private Long pk;

    @Min(1)
    private Integer price;
}
