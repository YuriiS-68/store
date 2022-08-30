package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class PictureDto {
    @Min((1))
    private Long pk;

    @Min((1))
    private Long idAds;

    @Min((1))
    private Integer fileSize;

    @NotBlank
    private String mediaType;
    @NotBlank
    private String filePath;
}
