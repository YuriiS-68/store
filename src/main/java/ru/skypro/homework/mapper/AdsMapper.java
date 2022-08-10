package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.User;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface AdsMapper {
    AdsMapper INSTANCE = Mappers.getMapper(AdsMapper.class);

    @Mapping(source = "createAdsDto.idAuthor", target = "user.id")
    @Mapping(source = "createAdsDto.title", target = "title")
    @Mapping(source = "createAdsDto.description", target = "description")
    @Mapping(source = "createAdsDto.price", target = "price")
//    @Mapping(source = "createAdsDto.image", target = "pictures")
    Ads createAdsDtoToAds(CreateAdsDto createAdsDto);

    @Mapping(source = "ads.title", target = "title")
    @Mapping(source = "ads.price", target = "price")
    @Mapping(source = "ads.user.id", target = "idAuthor")
    @Mapping(source = "ads.id", target = "pk")
    @Mapping(source = "ads.pictures", target = "image")
    AdsDto adsToAdsDto(Ads ads);

    @Mapping(source = "adsDto.title", target = "title")
    @Mapping(source = "adsDto.price", target = "price")
    @Mapping(source = "adsDto.idAuthor", target = "user.id")
    @Mapping(source = "adsDto.pk", target = "id")
    @Mapping(source = "adsDto.image", target = "pictures")
    Ads adsDtoToAds(AdsDto adsDto, User user);

    @Mapping(source = "ads.title", target = "title")
    @Mapping(source = "ads.price", target = "price")
    @Mapping(source = "ads.description", target = "description")
    @Mapping(source = "ads.id", target = "pk")
    @Mapping(source = "ads.pictures", target = "image")
    @Mapping(source = "user.firstName", target = "authorFirstName")
    @Mapping(source = "user.lastName", target = "authorLastName")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.email", target = "email")
    FullAdsDto adsToFullAdsDto(Ads ads, User user);

    Collection<AdsDto> entitiesToDto(Collection<Ads> ads);
}
