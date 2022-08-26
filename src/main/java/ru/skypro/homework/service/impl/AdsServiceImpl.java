package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
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
import java.util.Collection;

@Service
@Transactional
public class AdsServiceImpl implements AdsService {

    private final Logger logger = LoggerFactory.getLogger(AdsServiceImpl.class);

    private final AdsRepository adsRepository;

    private final UserRepository userRepository;

    private final AdsCommentRepository commentRepository;

    private final PictureRepository pictureRepository;

    private final AdsMapper adsMapper;

    private final AdsCommentMapper commentMapper;

    private final AuthServiceImpl authService;

    public AdsServiceImpl(AdsRepository adsRepository, UserRepository userRepository, AdsCommentRepository commentRepository,
                          PictureRepository pictureRepository, AdsMapper adsMapper, AdsCommentMapper commentMapper, AuthServiceImpl authService) {
        this.adsRepository = adsRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.pictureRepository = pictureRepository;
        this.adsMapper = adsMapper;
        this.commentMapper = commentMapper;
        this.authService = authService;
    }

    @Override
    public AdsDto addAds(CreateAdsDto createAdsDto)  throws NotFoundException {
        logger.info("Method addAds is running");
        User user = userRepository.findById(createAdsDto.getIdAuthor()).orElseThrow(NotFoundException::new);
        Ads newAds = adsMapper.createAdsDtoToAds(createAdsDto);
        adsRepository.save(newAds);
        return adsMapper.adsToAdsDto(newAds);
    }

    @Override
    public Collection<AdsDto> getAllAdsByTitle(String input) throws NotFoundException {
        Collection<Ads> adsCollection = adsRepository.findAllByTitleContainsIgnoreCase(input).get();
        return adsMapper.entitiesToDto(adsCollection);
    }

    @Override
    public Collection<AdsDto> getAllAds() {
        Collection<Ads> adsCollection = adsRepository.findAll();
        return adsMapper.entitiesToDto(adsCollection);
    }

    @Override
    public Collection<AdsDto> getAdsMeByTitle(Long id, String input) throws NotFoundException {
        Collection<Ads> adsMe = adsRepository.findAllByUserIdAndTitleContainsIgnoreCase(id, input).orElseThrow(NotFoundException::new);
        return adsMapper.entitiesToDto(adsMe);
    }


    @Override
    public Collection<AdsDto> getAdsMe(Long id) throws NotFoundException {
        Long currentId = authService.getIdCurrentUser();
        Collection<Ads> adsMe = adsRepository.findAllByUserId(currentId).orElseThrow(NotFoundException::new);

        return adsMapper.entitiesToDto(adsMe);
    }

    @Override
    public Collection<AdsCommentDto> getAdsComments(Long id) throws NotFoundException {
        Collection<AdsComment> adsComments = commentRepository.findAdsCommentsByAds_IdOrderByCreatedAtDesc(id)
                .orElseThrow(NotFoundException::new);
        return commentMapper.entitiesToDto(adsComments);
    }

    @Override
    @Transactional
    public void deleteCommentToAds(Long ad_pk, Long id) throws NotFoundException {
        if(commentRepository.deleteCommentToAdsById(ad_pk, id) != 1){
            throw new NotFoundException();
        }
    }

    @Override
    public AdsCommentDto getAdsComment(Long ad_pk, Long id) throws NotFoundException {
        AdsComment foundComment = commentRepository
                .getCommentToAdsById(ad_pk, id).orElseThrow(NotFoundException::new);

        return commentMapper.entityToDto(foundComment);
    }

    @Override
    public void removeAds(Long id) throws NotFoundException {
        if (adsRepository.deleteAdsById(id) != 1){
            throw new NotFoundException();
        }
    }

    @Override
    public FullAdsDto getAds(Long id) throws NotFoundException {
        Ads ads = adsRepository.findById(id).orElseThrow(NotFoundException::new);
        logger.info("User owner advertisement: {} {}", id, ads.getUser());
        return adsMapper.adsToFullAdsDto(ads, ads.getUser());
    }

    @Override
    public AdsDto updateAds(AdsDto adsDto, Long id) throws NotFoundException {
        if(adsRepository.existsById(id)){
            Ads updateAds = adsMapper.adsDtoToAds(adsDto);
            Collection<Picture> pictures = pictureRepository.findAllByAds_Id(id);
            updateAds.setId(id);
            updateAds.setPictures(pictures);
            adsRepository.save(updateAds);
            return adsDto;
        }
        throw new NotFoundException();
    }

    @Override
    public AdsCommentDto addAdsComment(Long ad_pk, AdsCommentDto adsComment) throws NotFoundException {
        Ads foundAds = adsRepository.findById(ad_pk).orElseThrow(NotFoundException::new);

        logger.info("User owner advertisement: {}", foundAds.getUser());

        AdsComment newComment = commentMapper.adsCommentDtoToEntity(adsComment, foundAds);
        newComment.setAds(foundAds);
        commentRepository.save(newComment);

        return commentMapper.entityToDto(newComment);
    }

    @Override
    public AdsCommentDto updateAdsComment(AdsCommentDto adsCommentDto, Long ad_pk, Long id) throws NotFoundException {
        Ads ads;
        AdsComment commentUpdate;
        logger.info("Result from commentsRepository: {}", commentRepository.existsAdsCommentById(id));
        if (commentRepository.existsAdsCommentById(id)) {
            ads = adsRepository.findById(ad_pk).orElseThrow(NotFoundException::new);
            commentUpdate = commentMapper.adsCommentDtoToEntity(adsCommentDto, ads);
            commentRepository.save(commentUpdate);
            return commentMapper.entityToDto(commentUpdate);
        }
        throw new NotFoundException();
    }
}
