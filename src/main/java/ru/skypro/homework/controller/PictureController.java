package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.PictureDto;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.model.Picture;
import ru.skypro.homework.service.PictureService;

import javax.validation.constraints.Min;
import java.io.IOException;

/** Эндпоинты для работы с таблицей картинок для объявлений (picture), в которой
 * находятся картинки для всех объявлений
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@Validated
@Tag(name = "Контроллер Картинок", description = "добавление, поиск, и удаление Картинок для объявлений")
public class PictureController {

    private final Logger logger = LoggerFactory.getLogger(PictureController.class);
    private final PictureService pictureService;

    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }


    /**
     * Загрузка картинки для объявления с указанием идентификатора объявления
     * @param idAds идентификатор объявления
     * @param adsPicture картинка
     */
    @Operation(
            summary = "Загрузка картинки",
            description = "Позволяет загрузить в БД картинку для указанного объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Картинка загружена"
                    )
            }
    )
    @PostMapping(value = "/{idAds}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PictureDto> uploadAdsPicture(@PathVariable @Min(1) Long idAds,
                                                       @RequestParam MultipartFile adsPicture) throws IOException {
        logger.info("Method uploadAdsPicture is running: {}", idAds);
        PictureDto pictureDto;
        try{
            pictureDto = pictureService.uploadAdsPicture(idAds, adsPicture);
        } catch (IOException | NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pictureService.uploadAdsPicture(idAds, adsPicture));
    }

    /**
     * Замена картинки для объявления с указанием идентификатора объявления
     * @param idAds идентификатор объявления
     * @param adsPicture картинка
     */
    @Operation(
            summary = "Замена картинки",
            description = "Позволяет заменить в БД картинку для указанного объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Картинка заменена"
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping(value = "api/picture/{idAds}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PictureDto> updateAdsPicture(@PathVariable @Min(1) Long idAds,
                                                       @RequestParam MultipartFile adsPicture) throws IOException {
        logger.info("Method updateAdsPicture is running: {}", idAds);
        PictureDto pictureDto;
        try{
            pictureDto = pictureService.updateAdsPicture(idAds, adsPicture);
        } catch (IOException | NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pictureService.uploadAdsPicture(idAds, adsPicture));
    }

    /**
     * Получить картинку по ее id (id)
     * @param id идентификатор картинки
     */
    @Operation(
            summary = "Поиск картинки",
            description = "Позволяет найти картинку по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденная картинка"
                    )
            }
    )
    @GetMapping(value = "api/picture/{id}")
    public ResponseEntity<byte[]> downloadPicture(@PathVariable @Min(1) Long id) {
        logger.info("Method downloadPicture is running: {}", id);
        Picture picture = pictureService.findPictureById(id);
        if (picture.getMediaType() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(picture.getMediaType()));
        headers.setContentLength(picture.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(picture.getData());
    }



}
