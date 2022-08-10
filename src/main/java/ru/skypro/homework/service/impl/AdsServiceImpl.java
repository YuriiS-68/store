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
import ru.skypro.homework.model.User;
import ru.skypro.homework.repo.AdsCommentRepository;
import ru.skypro.homework.repo.AdsRepository;
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

    private final AdsMapper adsMapper;

    private final AdsCommentMapper commentMapper;

    public AdsServiceImpl(AdsRepository adsRepository, UserRepository userRepository, AdsCommentRepository commentRepository,
                          AdsMapper adsMapper, AdsCommentMapper commentMapper) {
        this.adsRepository = adsRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.adsMapper = adsMapper;
        this.commentMapper = commentMapper;
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
    public Collection<AdsDto> getAllAds() throws NotFoundException {
        Collection<Ads> adsCollection = adsRepository.findAll();
        return adsMapper.entitiesToDto(adsCollection);
    }

    @Override
    public Collection<AdsDto> getAdsMe(Long id) throws NotFoundException {
        Collection<Ads> adsMe = adsRepository.findAllById(id)
                .orElseThrow(NotFoundException::new);
        return adsMapper.entitiesToDto(adsMe);
    }

    @Override
    public Collection<AdsCommentDto> getAdsComments(Long id) throws NotFoundException {
        Collection<AdsComment> adsComments = commentRepository
                .getAllCommentsByAdsId(id).orElseThrow(NotFoundException::new);
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

        User authorAds = commentRepository
                .getUserByCommentId(id).orElseThrow(NotFoundException::new);

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
        User user = adsRepository.getUserByAdsId(id).orElseThrow(NotFoundException::new);
        return adsMapper.adsToFullAdsDto(ads, user);
    }

    @Override
    public AdsDto updateAds(AdsDto ads, Long id) throws NotFoundException {
        User user = adsRepository.getUserByAdsId(id).orElseThrow(NotFoundException::new);
        if(adsRepository.existsById(id)){
            adsRepository.save(adsMapper.adsDtoToAds(ads, user));
            return ads;
        }
        throw new NotFoundException();
    }

    @Override
    public AdsCommentDto addAdsComment(Long ad_pk, AdsCommentDto adsComment) throws NotFoundException {
        Ads foundAds = adsRepository.findById(ad_pk).orElseThrow(NotFoundException::new);
        User author = userRepository.findById(adsComment.getIdAuthor()).orElseThrow(NotFoundException::new);

        AdsComment newComment = commentMapper.adsCommentDtoToEntity(adsComment, author, foundAds);
        newComment.setAds(foundAds);
        commentRepository.save(newComment);

        return commentMapper.entityToDto(newComment);
    }

    @Override
    public AdsCommentDto updateAdsComment(AdsCommentDto adsComment, Long ad_pk, Long id) throws NotFoundException {
        User author;
        Ads ads;
        AdsComment commentUpdate;
        if (commentRepository.existsAdsCommentsByIdAndAds_Id(ad_pk, id)) {
            author = userRepository.findById(adsComment.getIdAuthor()).orElseThrow(NotFoundException::new);
            ads = adsRepository.findById(ad_pk).orElseThrow(NotFoundException::new);
            commentUpdate = commentMapper.adsCommentDtoToEntity(adsComment, author, ads);
            commentRepository.save(commentUpdate);
            return commentMapper.entityToDto(commentUpdate);
        }
        throw new NotFoundException();
    }
}
