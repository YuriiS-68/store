package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public Collection<AdsDto> getAllAdsByTitle(String input) {
        Collection<Ads> adsCollection = adsRepository.findAllByTitleContainsIgnoreCase(input);
        return adsMapper.entitiesToDto(adsCollection);
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
    public Collection<AdsCommentDto> getAdsComments(Long id) {
        Collection<AdsComment> adsComments = commentRepository.findAdsCommentsByAds_IdOrderByCreatedAtDesc(id);
        return commentMapper.entitiesToDto(adsComments);
    }

    @Override
    @Transactional
    public void deleteCommentToAds(Long idAds, Long id) throws NotFoundException {
        if(commentRepository.deleteCommentToAdsById(idAds, id) != 1){
            throw new NotFoundException();
        }
    }

    @Override
    public AdsCommentDto getAdsComment(Long idAds, Long id) throws NotFoundException {
        AdsComment foundComment = commentRepository.getCommentToAdsById(idAds, id).orElseThrow(NotFoundException::new);
        return commentMapper.entityToDto(foundComment);
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
            Picture picture = pictureRepository.findById(id).orElseThrow(NotFoundException::new);
            updateAds.setId(id);
            updateAds.setPicture(picture);
            adsRepository.save(updateAds);
            return adsDto;
        }
        throw new NotFoundException();
    }

    @Override
    public AdsCommentDto addAdsComment(Long idAds, AdsCommentDto adsComment) throws NotFoundException {
        Ads foundAds = adsRepository.findById(idAds).orElseThrow(NotFoundException::new);

        AdsComment newComment = commentMapper.adsCommentDtoToEntity(adsComment, foundAds);
        newComment.setAds(foundAds);
        commentRepository.save(newComment);

        return commentMapper.entityToDto(newComment);
    }

    @Override
    public AdsCommentDto updateAdsComment(AdsCommentDto adsCommentDto, Long idAds, Long id) throws NotFoundException {
        if (commentRepository.existsAdsCommentById(id)) {
            Ads ads = adsRepository.findById(idAds).orElseThrow(NotFoundException::new);
            AdsComment commentUpdate = commentMapper.adsCommentDtoToEntity(adsCommentDto, ads);
            commentRepository.save(commentUpdate);
            return commentMapper.entityToDto(commentUpdate);
        }
        throw new NotFoundException();
    }
}
