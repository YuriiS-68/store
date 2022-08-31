package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.Picture;
import ru.skypro.homework.repo.AdsRepository;
import ru.skypro.homework.repo.PictureRepository;
import ru.skypro.homework.repo.UserRepository;
import ru.skypro.homework.service.AdsService;

import java.io.IOException;
import java.util.Collection;

@Service
public class AdsServiceImpl implements AdsService {

    private final Logger logger = LoggerFactory.getLogger(AdsServiceImpl.class);

    private final AdsRepository adsRepository;

    private final UserRepository userRepository;

    private final PictureRepository pictureRepository;

    private final PictureService pictureService;

    private final AdsMapper adsMapper;

    public AdsServiceImpl(AdsRepository adsRepository, UserRepository userRepository, PictureRepository pictureRepository,
                          PictureService pictureService, AdsMapper adsMapper) {
        this.adsRepository = adsRepository;
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
        this.pictureService = pictureService;
        this.adsMapper = adsMapper;
    }

    @Override
    public AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile pic) throws NotFoundException, IOException {
        logger.info("Method addAds is running");
        /*User user = userRepository.findById(createAdsDto.getIdAuthor()).orElseThrow(NotFoundException::new);*/
        Ads newAd = adsMapper.createAdsDtoToAds(createAdsDto);
        adsRepository.save(newAd);

        Long justSavedAdsId = adsRepository.getAdsByDescription(newAd.getDescription()).getId();
        pictureService.uploadAdsPicture(justSavedAdsId, pic);

        return adsMapper.adsToAdsDto(newAd);
    }

    @Override
    public ResponseWrapperAds getAllAdsByTitle(String input) {
        Collection<AdsDto> adsDtoCollection = adsMapper.entitiesToDto(adsRepository.findAllByTitleContainsIgnoreCase(input));
        return getWrapperAds(adsDtoCollection);
    }

    @Override
    public ResponseWrapperAds getAdsMeByTitle(String username, String input) {
        Collection<AdsDto> adsDtoCollection = adsMapper.entitiesToDto(adsRepository
                .findAllByUser_UsernameAndTitleContainsIgnoreCase(username, input));
        return getWrapperAds(adsDtoCollection);
    }

    @Override
    public ResponseWrapperAds getAllAds() {
        Collection<AdsDto> adsDtoCollection = adsMapper.entitiesToDto(adsRepository.getAllAds());
        return getWrapperAds(adsDtoCollection);
    }

    @Override
    public ResponseWrapperAds getAdsMe(String username) {
        Collection<AdsDto> adsDtoCollection = adsMapper.entitiesToDto(adsRepository.findAllByUser_Username(username));
        return getWrapperAds(adsDtoCollection);
    }

    @Override
    public void removeAds(Long id) {
        adsRepository.deleteById(id);
    }

    @Override
    public FullAdsDto getAds(Long id) throws NotFoundException {
        Ads ads = adsRepository.findById(id).orElseThrow(NotFoundException::new);
        return adsMapper.adsToFullAdsDto(ads, ads.getUser());
    }

    @Override
    public AdsDto updateAds(AdsDto adsDto, Long id) throws NotFoundException {
        if(adsRepository.existsById(id)){
            Ads updateAds = adsMapper.adsDtoToAds(adsDto);
            Collection<Picture> pictures = pictureRepository.findAllByAds_Id(id);
            updateAds.setId(id);
            /*updateAds.setPictures(pictures);*/
            adsRepository.save(updateAds);
            return adsDto;
        }
        throw new NotFoundException();
    }

    private ResponseWrapperAds getWrapperAds(Collection<AdsDto> collection){
        ResponseWrapperAds wrapperAds = new ResponseWrapperAds();
        wrapperAds.setCount(collection.size());
        wrapperAds.setResults(collection);
        return wrapperAds;
    }
}
