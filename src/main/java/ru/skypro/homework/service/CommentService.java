package ru.skypro.homework.service;

import org.springframework.security.access.AccessDeniedException;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.exception.NotFoundException;

import java.util.Collection;

public interface CommentService {

    void deleteCommentToAds(Long idAds, Long id) throws NotFoundException, AccessDeniedException;

    Collection<AdsCommentDto> getAdsComments(Long ad_pk);

    AdsCommentDto getAdsComment(Long ad_pk, Long id);

    AdsCommentDto addAdsComment(Long ad_pk, AdsCommentDto adsComment);

    AdsCommentDto updateAdsComment(AdsCommentDto adsComment, Long ad_pk, Long id);

}
