package ru.skypro.homework.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Ads;

import java.util.Collection;

@Repository
public interface AdsRepository extends JpaRepository<Ads, Long> {

    @Query("select a from Ads a order by a.id")
    Collection<Ads> getAllAds();

    Collection<Ads> findAllByUser_UsernameAndTitleContainsIgnoreCase(String username, String input);

    Collection<Ads> findAllByUser_Username(String username);

    Collection<Ads> findAllByTitleContainsIgnoreCase(String input);

    Ads getAdsById (Long id);
}
