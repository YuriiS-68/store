package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.dto.ResponseWrapperAds;

import java.io.IOException;

public interface AdsService {

    AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile pic) throws IOException;

    ResponseWrapperAds getAllAdsByTitle(String input);

    ResponseWrapperAds getAllAds();

    ResponseWrapperAds getAdsMeByTitle(String username, String input);

    ResponseWrapperAds getAdsMe(String username);

    void removeAds(Long id, Authentication auth);

    FullAdsDto getAds(Long id);

    AdsDto updateAds(AdsDto adsDto, Long id) throws IOException;
}