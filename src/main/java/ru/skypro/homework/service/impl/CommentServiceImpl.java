package ru.skypro.homework.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.AdsCommentMapper;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;
import ru.skypro.homework.repo.AdsCommentRepository;
import ru.skypro.homework.repo.AdsRepository;
import ru.skypro.homework.service.CommentService;

import java.util.Collection;

public class CommentServiceImpl implements CommentService {

    private final AdsRepository adsRepository;
    private final AdsCommentRepository commentRepository;

    private final AdsCommentMapper commentMapper;

    public CommentServiceImpl(AdsRepository adsRepository, AdsCommentRepository commentRepository, AdsCommentMapper commentMapper) {
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public void deleteCommentToAds(Long idAds, Long id) throws NotFoundException, AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) auth.getPrincipal();
        Ads ads = adsRepository.findById(idAds).orElseThrow(NotFoundException::new);
        AdsComment comment = commentRepository.findById(id).orElseThrow(NotFoundException::new);
        if (currentUser.getAuthorities().toString().contains("ADMIN") || auth.getName().equals(ads.getUser().getUsername())) {
            commentRepository.delete(comment);
        }else {
            throw new AccessDeniedException("You don't have enough right delete Comment");
        }
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) auth.getPrincipal();
        Ads ads = adsRepository.findById(idAds).orElseThrow(NotFoundException::new);
        AdsComment comment = commentRepository.findById(id).orElseThrow(NotFoundException::new);
        if (currentUser.getAuthorities().toString().contains("ADMIN") || auth.getName().equals(ads.getUser().getUsername())) {
            comment = commentMapper.adsCommentDtoToEntity(adsCommentDto, ads);
            commentRepository.save(comment);
            return commentMapper.entityToDto(comment);
        }else {
            throw new AccessDeniedException("You don't have enough right delete Comment");
        }
    }

    @Override
    public Collection<AdsCommentDto> getAdsComments(Long id) {
        Collection<AdsComment> adsComments = commentRepository.findAdsCommentsByAds_IdOrderByCreatedAtDesc(id);
        return commentMapper.entitiesToDto(adsComments);
    }

    @Override
    public AdsCommentDto getAdsComment(Long idAds, Long id) throws NotFoundException {
        AdsComment foundComment = commentRepository.getCommentToAdsById(idAds, id).orElseThrow(NotFoundException::new);
        return commentMapper.entityToDto(foundComment);
    }
}
