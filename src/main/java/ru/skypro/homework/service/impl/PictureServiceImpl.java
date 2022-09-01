package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.PictureDto;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.PictureMapper;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.Picture;
import ru.skypro.homework.repo.AdsRepository;
import ru.skypro.homework.repo.PictureRepository;
import ru.skypro.homework.service.PictureService;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class PictureServiceImpl implements PictureService {

    private final Logger logger = LoggerFactory.getLogger(PictureServiceImpl.class);

    @Value("${path.to.pictures.folder}")
    private String picturesDir;

    private final PictureRepository pictureRepository;
    private final AdsRepository adsRepository;
    private final PictureMapper pictureMapper;

    public PictureServiceImpl(PictureRepository pictureRepository, AdsRepository adsRepository, PictureMapper pictureMapper) {
        this.pictureRepository = pictureRepository;
        this.adsRepository = adsRepository;
        this.pictureMapper = pictureMapper;
    }

    public PictureDto uploadAdsPicture(Long idAds, MultipartFile pictureFile) throws IOException, NotFoundException {
        logger.info("Method was called - uploadPhoto");
        Ads ads = adsRepository.getAdsById(idAds);

        return savePicture(ads,pictureFile);
    }

    public PictureDto updateAdsPicture(Long idAds, MultipartFile pictureFile) throws IOException, NotFoundException {
        logger.info("Method was called - updateAdsPicture");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) auth.getPrincipal();
        Ads ads = adsRepository.findById(idAds).orElseThrow(NotFoundException::new);
        if (currentUser.getAuthorities().toString().contains("ADMIN") || auth.getName().equals(ads.getUser().getUsername())) {

           return savePicture(ads,pictureFile);
        } else {
            throw new AccessDeniedException("You don't have enough right to change picture");
        }
    }

    public Picture findPictureById(Long idPicture) throws NotFoundException {
        return pictureRepository.findById(idPicture).orElseThrow(NotFoundException::new);
    }

    private PictureDto savePicture (Ads ads, MultipartFile pictureFile) throws IOException {
        logger.info("Method save picture started:");
        Path filepath = Path.of(picturesDir, ads.getId().toString());
        Files.createDirectories(filepath.getParent());

        Files.deleteIfExists(filepath);

        try (
                InputStream is = pictureFile.getInputStream();
                OutputStream os = Files.newOutputStream(filepath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {

            bis.transferTo(bos);
        }
        Picture picture = findPicture(ads.getId());
        picture.setAds(ads);
        picture.setFilePath(filepath.toString());
        picture.setMediaType(pictureFile.getContentType());
        picture.setData(pictureFile.getBytes());
        picture.setFileSize(pictureFile.getSize());
        pictureRepository.save(picture);

        ads.setImage(filepath.toString());
        adsRepository.save(ads);

        return pictureMapper.pictureToPictureDto(picture, ads);
    }

    private Picture findPicture(Long adsId) {
        logger.info("Method for finding picture was invoked");
        return pictureRepository.findById(adsId).orElse(new Picture());
    }

}
