package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;

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

    private final CommentService commentService;

    /**
     * Получить все существующие объявления GET <a href="http://localhost:3000/ads">...</a>
     **/
    @Operation(
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
    public ResponseEntity<ResponseWrapperAds> getAllAds() {
        logger.info("Method getAllAds is running");
        return ResponseEntity.ok(adsService.getAllAds());
    }

    /**
     * Получить все существующие объявления по строке содержащейся в заголовке GET <a href="http://localhost:3000/ads/title">...</a>
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
    @GetMapping(value = "/title", params = {"input"})
    public ResponseEntity<ResponseWrapperAds> getAllAdsByTitle(@RequestParam String input) {
        logger.info("Method getAllAds is running");
        return ResponseEntity.ok(adsService.getAllAdsByTitle(input));
    }

    /**
     * POST <a href="http://localhost:3000/ads">...</a>
     * Добавление объявления.
     * @param createAdsDto объявление
     * @param file картинка к объявлению
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
   @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
   public ResponseEntity<AdsDto> addAds(@RequestPart("properties") @Valid CreateAdsDto createAdsDto,
                                        @RequestPart("image") @Valid @NotNull MultipartFile file) {
        logger.info("Method addAds is running: {} {}", createAdsDto, file);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       AdsDto newAds;
        try {
            newAds = adsService.addAds(auth, createAdsDto, file);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (IOException e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(newAds);
   }

    /**
     * Получить все объявления автора по заголовку GET <a href="http://localhost:3000/ads">...</a>
     * @param input строка содержащаяся в заголовке объявления
     * @param auth данные аутентифицированного пользователя
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
    @GetMapping(value = "/me", params = {"input"})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ResponseWrapperAds> getAdsMeByTitle(Authentication auth, String input){
        logger.info("Method getAdsMe is running: {} {}", auth, input);
        ResponseWrapperAds wrapperAds = adsService.getAdsMeByTitle(auth.getName(), input);
        if (wrapperAds.getResults().isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(wrapperAds);
    }

    /**
     * Получить все объявления автора GET <a href="http://localhost:3000/ads">...</a>
     * Получить объявления автора содержащие определенную строку.
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
    @GetMapping(value = "/me")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ResponseWrapperAds> getAdsMe(){
        logger.info("Method getAdsMe is running");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ResponseWrapperAds wrapperAds = adsService.getAdsMe(auth.getName());
        return ResponseEntity.ok(wrapperAds);
    }

    /**
     * Получить все комментарии(отзывы) к объявлению GET <a href="http://localhost:3000/ads/">...</a>{idAds}/comment
     * Объявление должно существовать. Используется идентификатор объявления "ad_pk"
     * @param idAds идентификатор объявления
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
    @GetMapping(value = "/{idAds}/comment")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ResponseWrapperAdsComment> getAdsComments(@PathVariable @Min(1) Long idAds) {
        logger.info("Method getAdsComments is running: {}", idAds);
        ResponseWrapperAdsComment wrapperAdsComment = commentService.getAdsComments(idAds);
        if (wrapperAdsComment.getResults().isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(wrapperAdsComment);
    }

    /**
     * DELETE <a href="http://localhost:3000/ads">...</a>{idAds}/comment/{id}
     * Удаление комментария к объявлению.
     * @param idAds идентификатор объявления
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
    @DeleteMapping("/{idAds}/comment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> deleteAdsComment(@PathVariable @Min(1) Long idAds, @PathVariable @Min(1) Long id){
        logger.info("Method deleteAdsComment is running: {} {}", idAds, id);
        try {
            commentService.deleteCommentToAds(idAds, id);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * POST <a href="http://localhost:3000/ads">...</a>{idAds}/comment/{id}
     * Получение комментария к объявлению.
     * @param idAds идентификатор объявления
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
    @GetMapping("/{idAds}/comment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<AdsCommentDto> getAdsComment(@PathVariable @Min(1) Long idAds, @PathVariable @Min(1) Long id){
        logger.info("Method getAdsComment is running: {} {}", idAds, id);
        AdsCommentDto foundAdsComment;
        try {
            foundAdsComment = commentService.getAdsComment(idAds, id);
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> removeAds(@PathVariable @Min(1) Long id) {
        logger.info("Method removeAds is running: {}", id);
        try {
            adsService.removeAds(id);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<FullAdsDto> getAds(@PathVariable @Min(1) Long id) {
        logger.info("Method getAds is running: {}", id);
        FullAdsDto adsDto;
        try {
            adsDto = adsService.getAds(id);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<AdsDto> updateAds(@PathVariable @Min(1) Long id,
                                            @RequestBody @Valid AdsDto adsDto) {
        logger.info("Method updateAds is running: {} {}", id, adsDto);
        AdsDto adsUpdatedDto;
        try {
            adsUpdatedDto = adsService.updateTextAds(id, adsDto);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(adsUpdatedDto);
    }

    /**
     * POST <a href="http://localhost:3000/ads">...</a>{idAds}/comment
     * Добавление комментария к объявлению.
     * @param idAds идентификатор объявления
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
    @PostMapping("/{idAds}/comment")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<AdsCommentDto> addAdsComment(@PathVariable @Min(1) Long idAds,
                                                       @Valid @RequestBody AdsCommentDto adsComment){
        logger.info("Method addAdsComment is running: {} {}", idAds, adsComment);
        AdsCommentDto newCommentDto;
        try {
            newCommentDto = commentService.addAdsComment(idAds, adsComment);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(newCommentDto);
    }

    /**
     * POST <a href="http://localhost:3000/ads/">...</a>{idAds}/comment
     * Обновление отзыва(комментария) к объявлению. Объявление должно существовать.
     * Используется идентификатор объявления "ad_pk"
     * @param adsComment коментарий к объявлению
     * @param idAds идентификатор объявления
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
    @PostMapping("/{idAds}/comment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<AdsCommentDto> updateAdsComment(@RequestBody @Valid AdsCommentDto adsComment,
                                                          @PathVariable @Min(1) Long idAds,
                                                          @PathVariable @Min(1) Long id) {
        logger.info("Method createAdsComment is running: {} {} {}", adsComment, idAds, id);
        AdsCommentDto commentDto;
        try {
            commentDto = commentService.updateAdsComment(adsComment, idAds, id);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commentDto);
    }
}
