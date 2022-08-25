package ru.skypro.homework.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * from users order by first_name, last_name", nativeQuery = true)
    Optional<Collection<User>> getAll();

    User findUserByUserName(String userName);

    User getUserByUserName (String userName);
    User getUserById(Long id);

    Long getIdByUserName (String userName);

    boolean existsUserByUserName (String userName);

}
