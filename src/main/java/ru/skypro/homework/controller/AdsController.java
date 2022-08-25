package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.service.AdsService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/ads")
@Tag(name = "Контроллер Объявлений и Отзывов", description = "добавление, поиск, изменение и удаление Объявлений и Отзывов")
public class AdsController {

    private final Logger logger = LoggerFactory.getLogger(AdsController.class);

    private final AdsService adsService;

    /**
     * Получить все существующие объявления по строке содержащейся в заголовке GET <a href="http://localhost:3000/ads">...</a>
     * @param input строка для поиска объявлений по названию
     **/
    @Operation(
            summary = "Получить все объявления по названию",
            description = "Получение всех объявлений по названию",

            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявления получены"
                    )
            }
    )
    @GetMapping(params = {"input"})
    public ResponseEntity<Collection<AdsDto>> getAllAdsByTitle(@RequestParam String input) {
        logger.info("Method getAllAds is running");
        Collection<AdsDto> adsDto = adsService.getAllAdsByTitle(input);
        if (adsDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adsDto);
    }

    /**
     * Получить все существующие объявления GET <a href="http://localhost:3000/ads">...</a>
     **/

    /*@Operation(
            summary = "Получить все объявления",
            description = "Получение всех объявлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявления получены"
                    )
            }
    )
    @GetMapping()
    public ResponseEntity<Collection<AdsDto>> getAllAds() {
        logger.info("Method getAllAds is running");
        Collection<AdsDto> adsDto = adsService.getAllAds();
        if (adsDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adsDto);
    }*/

    /**
     * POST <a href="http://localhost:3000/ads">...</a>
     * Добавление объявления.
     * @param createAds объявление
     * @return добавленное объявление в формате json
     */
   @Operation(
            summary = "Добавить объявление",
            description = "Добавление нового объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявление добавлено"
                    )
            }
    )
    @PostMapping
   public ResponseEntity<AdsDto> addAds(@RequestBody @Valid CreateAdsDto createAds) {
        logger.info("Method addAds is running: {}", createAds);
        return ResponseEntity.ok(adsService.addAds(createAds));
   }

    /**
     * Получить все объявления автора по заголовку GET <a href="http://localhost:3000/ads">...</a>
     * @param input строка содержащаяся в заголовке объявления
     * @param idAuthor идентификатор автора объявления
     **/
    @Operation(
            summary = "Получить все объявления автора по заголовку",
            description = "Получение всех объявлений автора по заголовку",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявления получены"
                    )
            }
    )
    @GetMapping(value = "/me", params = {"idAuthor", "input"})
    public ResponseEntity<Collection<AdsDto>> getAdsMeByTitle(@RequestParam(value = "idAuthor") @Min(1) Long idAuthor,
                                                       String input){
        logger.info("Method getAdsMe is running: {} {}", idAuthor, input);
        Collection<AdsDto> listAdsMe;
        try {
            listAdsMe = adsService.getAdsMeByTitle(idAuthor, input);
        } catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(listAdsMe);
    }

    /**
     * Получить все объявления автора GET <a href="http://localhost:3000/ads">...</a>
     * Получить объявления автора содержащие определенную строку.
     * @param idAuthor идентификатор автора
     * @return возвращаемая коллекция объявлений
     **/
    @Operation(
            summary = "Получить все объявления автора",
            description = "Получение всех объявлений автора",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявления получены"
                    )
            }
    )
    @GetMapping(value = "/me", params = {"idAuthor"})
    public ResponseEntity<Collection<AdsDto>> getAdsMe(@RequestParam(value = "idAuthor")
                                                       @Min(1) Long idAuthor){
        logger.info("Method getAdsMe is running: {}", idAuthor);
        Collection<AdsDto> listAdsMe;
        try {
            listAdsMe = adsService.getAdsMe(idAuthor);
        } catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(listAdsMe);
    }

    /**
     * Получить все комментарии(отзывы) к объявлению GET <a href="http://localhost:3000/ads/">...</a>{ad_pk}/comment
     * Объявление должно существовать. Используется идентификатор объявления "ad_pk"
     * @param ad_pk идентификатор объявления
     **/
    @Operation(
            summary = "Получить все отзывы к объявлению",
            description = "Нахождение всех отзывов к указанному объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отзывы получены"
                    )
            }
    )
    @GetMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<Collection<AdsCommentDto>> getAdsComments(@PathVariable @Min(1) Long ad_pk) {
        logger.info("Method getAdsComments is running: {}", ad_pk);
        Collection<AdsCommentDto> listOfAdsComment;
        try{
            listOfAdsComment = adsService.getAdsComments(ad_pk);
        } catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(listOfAdsComment);
    }

    /**
     * DELETE <a href="http://localhost:3000/ads">...</a>{ad_pk}/comment/{id}
     * Удаление комментария к объявлению.
     * @param ad_pk идентификатор объявления
     * @param id идентификатор комментария к объявлению
     * @return статус ок если успешно был удален комментарий
     */
    @Operation(
            summary = "Удалить комментарий к объявлению",
            description = "Удаление комментария к объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий к объявлению удален"
                    )
            }
    )
    @DeleteMapping("/{ad_pk}/comment/{id}")
    public ResponseEntity<?> deleteAdsComment(@PathVariable @Min(1) Long ad_pk, @PathVariable @Min(1) Long id){
        logger.info("Method deleteAdsComment is running: {} {}", ad_pk, id);
        try {
            adsService.deleteCommentToAds(ad_pk, id);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * POST <a href="http://localhost:3000/ads">...</a>{ad_pk}/comment
     * Получение комментария к объявлению.
     * @param ad_pk идентификатор объявления
     * @param id идентификатор
     * @return комментарий к объявлению в формате json
     */
    @Operation(
            summary = "Получить комментарий к объявлению",
            description = "Получение нового комментария к объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий к объявлению получен"
                    )
            }
    )
    @GetMapping("/{ad_pk}/comment/{id}")
    public ResponseEntity<AdsCommentDto> getAdsComment(@PathVariable @Min(1) Long ad_pk, @PathVariable @Min(1) Long id){
        logger.info("Method getAdsComment is running: {} {}", ad_pk, id);
        AdsCommentDto foundAdsComment;
        try {
            foundAdsComment = adsService.getAdsComment(ad_pk, id);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundAdsComment);
    }

    /**
     * Удалить объявление по его идентификатору, то-есть по id. Должна одновременно
     * удаляться и соответствующая картинка (при ее наличии) в таблице картинок
     * DELETE <a href="http://localhost:3000/ads/{">...</a>id}
     * @param id идентификатор
     **/
    @Operation(
            summary = "Удалить объявление",
            description = "Удаление объявления по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявление удалено"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAds(@PathVariable @Min(1) Long id) {
        logger.info("Method removeAds is running: {}", id);
        try {
            adsService.removeAds(id);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Получить объявление по его идентификатору, то-есть по id
     * GET <a href="http://localhost:3000/ads/">...</a>{id}
     * @param id идентификатор
     **/
    @Operation(
            summary = "Найти объявление",
            description = "Найти объявление по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявление найдено"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<FullAdsDto> getAds(@PathVariable @Min(1) Long id) {
        logger.info("Method getAds is running: {}", id);
        FullAdsDto adsDto;
        try {
            adsDto = adsService.getAds(id);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adsDto);
    }

    /** Редактировать объявление по его идентификатору,
     * PUT <a href="http://localhost:3000/ads/">...</a>{id}
     * @param id идентификатор объявления
     * @param adsDto объявление
     **/
    @Operation(
            summary = "Редактировать объявление",
            description = "Редактирование объявления с указанным идентификатором",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявление отредактировано"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<AdsDto> updateAds(@RequestBody @Valid AdsDto adsDto, @PathVariable @Min(1) Long id) {
        logger.info("Method updateAds is running: {} {}", adsDto, id);
        AdsDto adsUpdatedDto;
        try {
            adsUpdatedDto = adsService.updateAds(adsDto, id);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adsUpdatedDto);
    }

    /**
     * POST <a href="http://localhost:3000/ads">...</a>{ad_pk}/comment
     * Добавление комментария к объявлению.
     * @param adsId идентификатор объявления
     * @param adsComment комментарий
     * @return добавленный комментарий к объявлению в формате json
     */
    @Operation(
            summary = "Добавить комментарий к объявлению",
            description = "Добавление нового комментария к объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий к объявлению добавлен"
                    )
            }
    )
    @PostMapping("/{adsId}/comment")
    public ResponseEntity<AdsCommentDto> addAdsComment(@PathVariable @Min(1) Long adsId,
                                                       @Valid @RequestBody AdsCommentDto adsComment){
        logger.info("Method addAdsComment is running: {} {}", adsId, adsComment);
        AdsCommentDto newCommentDto;
        try {
            newCommentDto = adsService.addAdsComment(adsId, adsComment);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(newCommentDto);
    }

    /**
     * POST <a href="http://localhost:3000/ads/">...</a>{ad_pk}/comment
     * Обновление отзыва(комментария) к объявлению. Объявление должно существовать.
     * Используется идентификатор объявления "ad_pk"
     * @param adsComment коментарий к объявлению
     * @param ad_pk идентификатор объявления
     * @param id идентификатор
     * @return обновленный комментарий в формате json
     */
    @Operation(
            summary = "Обновить комментарий",
            description = "Обновление комментария к существующему объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий обновлен"
                    )
            }
    )
    @PostMapping("/{ad_pk}/comment/{id}")
    public ResponseEntity<AdsCommentDto> updateAdsComment(@RequestBody @Valid AdsCommentDto adsComment,
                                                          @PathVariable @Min(1) Long ad_pk,
                                                          @PathVariable @Min(1) Long id) {
        logger.info("Method createAdsComment is running: {} {} {}", adsComment, ad_pk, id);
        AdsCommentDto commentDto;
        try {
            commentDto = adsService.updateAdsComment(adsComment, ad_pk, id);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commentDto);
    }
}
