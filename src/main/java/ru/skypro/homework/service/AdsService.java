package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import java.io.IOException;

public interface AdsService {

    AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile pic) throws IOException;

    ResponseWrapperAds getAllAdsByTitle(String input);

    ResponseWrapperAds getAllAds();

    ResponseWrapperAds getAdsMeByTitle(String username, String input);

    ResponseWrapperAds getAdsMe(String username);

    void removeAds(Long id);

    FullAdsDto getAds(Long id);

    AdsDto updateAds(AdsDto adsDto, Long id);
}