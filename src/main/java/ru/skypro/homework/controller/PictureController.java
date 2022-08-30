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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.PictureDto;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.PictureMapper;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.Picture;
import ru.skypro.homework.repo.AdsRepository;
import ru.skypro.homework.service.impl.PictureService;

import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/** Эндпоинты для работы с таблицей картинок для объявлений (picture), в которой
 * находятся картинки для всех объявлений
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@Validated
@RequestMapping("/api/picture")
@Tag(name = "Контроллер Картинок", description = "добавление, поиск, и удаление Картинок для объявлений")
public class PictureController {

    private final Logger logger = LoggerFactory.getLogger(PictureController.class);
    private final PictureService pictureService;
    private final PictureMapper pictureMapper;
    private final AdsRepository adsRepository;

    public PictureController(PictureService pictureService, PictureMapper pictureMapper, AdsRepository adsRepository) {
        this.pictureService = pictureService;
        this.pictureMapper = pictureMapper;
        this.adsRepository = adsRepository;
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
                                                       @RequestParam MultipartFile adsPicture) {
        logger.info("Method uploadAdsPicture is running: {}", idAds);
        Ads ad = adsRepository.findById(idAds).orElseThrow(NotFoundException::new);
        Picture picture;
        try{
            picture = pictureService.uploadAdsPicture(idAds, adsPicture);
        } catch (IOException | NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pictureMapper.pictureToPictureDto(picture, ad));
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
    @GetMapping(value = "/{id}")
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

    /**
     * Получить все картинки для указанного объявления (объявление выбирается по его id (idAds))
     * @param idAds идентификатор объявления
     */
    @Operation(
            summary = "Поиск картинок",
            description = "Позволяет найти все картинки для указанного объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденные картинки"
                    )
            }
    )
    @GetMapping(value = "/all/{idAds}")
    public List<ResponseEntity<byte[]>> downloadPicturesByAds(@PathVariable @Min(1) Long idAds) {
        logger.info("Method downloadPicturesByAds is running: {}", idAds);
        return pictureService.downloadPictures(idAds);
    }

    /**
     * Запрос на удаление картинок. Если задан критерий на удаление всех картинок (all = true),
     * тогда удаляются все картинки, если (all = false) (установлено по умолчанию) и указан
     * идентификатор картинки (idPicture), тогда удаляется одна картинка с указанным id
     * @param idPicture идентификатор картинки
     * @param all флаг удаления всех картинок
     */
    @Operation(
            summary = "Удаление картинки/картинок",
            description = "Позволяет удалить картинку по её идентификатору или удалить все картинки",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Картинка/картинки удалена/удалены"
                    )
            }
    )
    @DeleteMapping
    public ResponseEntity<Picture> delete(@RequestParam(required = false) @Min(1) Long idPicture,
                                          @RequestParam(required = false) boolean all) {
        logger.info("Method delete is running");
        if (all) {
            pictureService.deleteAll();
            return ok().build();
        }
        if (idPicture != null) {
            pictureService.deletePicture(idPicture);
            return ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
