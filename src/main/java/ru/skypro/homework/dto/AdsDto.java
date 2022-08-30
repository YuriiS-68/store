package ru.skypro.homework.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import ru.skypro.homework.model.Picture;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Data
public class AdsDto {
    @Min((1))
    private Long idAuthor;

    @NotBlank
    private String title;

    private String image;

//    private Collection<Picture> image;

    @Min((1))
    private Long pk;

    @Min(1)
    private Integer price;
}
