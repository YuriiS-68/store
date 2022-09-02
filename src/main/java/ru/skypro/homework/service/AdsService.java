package ru.skypro.homework.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;

import java.io.IOException;
import java.util.Collection;

public interface AdsService {

    AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile pictureFile) throws IOException;
//    AdsDto createAd(CreateAdsDto createAdsDto, MultipartFile pictureFile) throws IOException;

    Collection<AdsDto> getAllAdsByTitle(String input);

    Collection<AdsDto> getAllAds();

    Collection<AdsDto> getAdsMeByTitle(Long id, String input);

    Collection<AdsDto> getAdsMe(Long id);

    void removeAds(Long id);

    FullAdsDto getAds(Long id);

//    AdsDto updateAds(AdsDto adsDto, Long id);
    AdsDto updateAds(CreateAdsDto createAdsDto, Long id);

}