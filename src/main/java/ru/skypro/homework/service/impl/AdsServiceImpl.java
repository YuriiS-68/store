package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.AdsCommentMapper;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;
import ru.skypro.homework.model.Picture;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repo.AdsCommentRepository;
import ru.skypro.homework.repo.AdsRepository;
import ru.skypro.homework.repo.PictureRepository;
import ru.skypro.homework.repo.UserRepository;
import ru.skypro.homework.service.AdsService;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Collection;

@Service
@Transactional
public class AdsServiceImpl implements AdsService {

    private final Logger logger = LoggerFactory.getLogger(AdsServiceImpl.class);

    private final AdsRepository adsRepository;

    private final UserRepository userRepository;

    private final AdsCommentRepository commentRepository;

    private final PictureRepository pictureRepository;
    private final PictureService pictureService;

    private final AdsMapper adsMapper;

    private final AdsCommentMapper commentMapper;

    public AdsServiceImpl(AdsRepository adsRepository, UserRepository userRepository, AdsCommentRepository commentRepository,
                          PictureRepository pictureRepository, PictureService pictureService, AdsMapper adsMapper, AdsCommentMapper commentMapper) {
        this.adsRepository = adsRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.pictureRepository = pictureRepository;
        this.pictureService = pictureService;
        this.adsMapper = adsMapper;
        this.commentMapper = commentMapper;
    }

/*    @Override
    public AdsDto addAds(CreateAdsDto createAdsDto)  throws NotFoundException {
        logger.info("Method addAds is running");
        User user = userRepository.findById(createAdsDto.getIdAuthor()).orElseThrow(NotFoundException::new);
        Ads newAds = adsMapper.createAdsDtoToAds(createAdsDto);
        adsRepository.save(newAds);
        return adsMapper.adsToAdsDto(newAds);
    }*/
    @Override
    public AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile pictureFile) throws NotFoundException, IOException {
        logger.info("Method createAd is running");
//        User user = userRepository.findById(createAdsDto.getIdAuthor()).orElseThrow(NotFoundException::new);
        Ads newAd = adsMapper.createAdsDtoToAds(createAdsDto);
        newAd = adsRepository.save(newAd);
        Picture picture = pictureService.uploadAdsPicture(newAd.getId(), pictureFile);
        newAd.setPicture(picture);
        return adsMapper.adsToAdsDto(adsRepository.save(newAd));
    }

    @Override
    public Collection<AdsDto> getAllAdsByTitle(String input) throws AccessDeniedException {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails currentUser = (UserDetails) auth.getPrincipal();
        Collection<Ads> adsCollection = adsRepository.findAllByTitleContainsIgnoreCase(input);
        return adsMapper.entitiesToDto(adsCollection);
//        if (currentUser.getAuthorities().toString().contains("ADMIN")) {
//            Collection<Ads> adsCollection = adsRepository.findAllByTitleContainsIgnoreCase(input);
//            return adsMapper.entitiesToDto(adsCollection);
//        }else {
//            throw new AccessDeniedException("You don't have enough right get all ads");
//        }
    }

    @Override
    public Collection<AdsDto> getAdsMeByTitle(Long id, String input) {
        Collection<Ads> adsMe = adsRepository.findAllByUserIdAndTitleContainsIgnoreCase(id, input);
        return adsMapper.entitiesToDto(adsMe);
    }

    @Override
    public Collection<AdsDto> getAllAds() {
        Collection<Ads> adsCollection = adsRepository.findAll();
        return adsMapper.entitiesToDto(adsCollection);
    }

    @Override
    public Collection<AdsDto> getAdsMe(Long id) {
        Collection<Ads> adsMe = adsRepository.findAllByUserId(id);
        return adsMapper.entitiesToDto(adsMe);
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

/*    @Override
    public AdsDto updateAds(AdsDto adsDto, Long id) throws NotFoundException {
        if(adsRepository.existsById(id)){
            Ads updateAds = adsMapper.adsDtoToAds(adsDto);
            Picture picture = pictureRepository.findById(id).orElseThrow(NotFoundException::new);
            updateAds.setId(id);
            updateAds.setPicture(picture);
            adsRepository.save(updateAds);
            return adsDto;
        }
        throw new NotFoundException();
    }   */
    @Override
    public AdsDto updateAds(CreateAdsDto createAdsDto, Long id) throws NotFoundException {
        if(adsRepository.existsById(id)){
            Ads updateAds = adsMapper.createAdsDtoToAds(createAdsDto);
            Picture picture = pictureRepository.findById(id).orElseThrow(NotFoundException::new);
            updateAds.setId(id);
            updateAds.setPicture(picture);
            adsRepository.save(updateAds);
            return adsMapper.adsToAdsDto(updateAds);
        }
        throw new NotFoundException();
    }

}
