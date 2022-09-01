package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.PictureDto;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.model.Picture;

import java.io.IOException;

public interface PictureService {

    PictureDto uploadAdsPicture(Long idAds, MultipartFile pictureFile) throws IOException, NotFoundException;

    PictureDto updateAdsPicture(Long idAds, MultipartFile pictureFile) throws IOException, NotFoundException;

    Picture findPictureById(Long idPicture) throws NotFoundException;
}
