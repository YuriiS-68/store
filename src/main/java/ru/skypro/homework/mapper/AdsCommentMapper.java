package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;
import ru.skypro.homework.model.User;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface AdsCommentMapper {
    AdsCommentMapper INSTANCE = Mappers.getMapper(AdsCommentMapper.class);

    @Mapping(source = "adsComment.user.id", target = "idAuthor")
    @Mapping(source = "adsComment.createdAt", target = "createdAt")
    @Mapping(source = "adsComment.id", target = "pk")
    @Mapping(source = "adsComment.text", target = "text")
    AdsCommentDto entityToDto(AdsComment adsComment);

    @Mapping(source = "adsCommentDto.idAuthor", target = "user.id")
    @Mapping(source = "adsCommentDto.createdAt", target = "createdAt")
    @Mapping(source = "adsCommentDto.pk", target = "id")
    @Mapping(source = "adsCommentDto.text", target = "text")
    @Mapping(source = "ads", target = "ads")
    AdsComment adsCommentDtoToEntity(AdsCommentDto adsCommentDto, User user, Ads ads);

    Collection<AdsCommentDto> entitiesToDto(Collection<AdsComment> adsComments);
}
