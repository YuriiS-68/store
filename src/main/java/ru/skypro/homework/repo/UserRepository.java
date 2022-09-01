package ru.skypro.homework.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u order by u.firstName, u.lastName")
    Collection<User> getAll();

    Optional<User> getUserByUsername (String userName);

    User findUserByUsername (String username);
    boolean existsUserByUsername (String username);

    @Query("select u.id from User u where u.username = :username")
    Long getIdUserByUsername(@Param(value = "username") String username);

    @Modifying
    @Query("update User u set u.password = :newPassword where u.email = :userName")
    void updatePassword(@Param(value = "userName") String userName,
                        @Param(value = "newPassword") String newPassword);

    @Modifying
    @Query("update User u set u.email = :email, u.password = :password, u.firstName = :firstName, u.lastName = :lastName, u.phone = :phone where u.id = :idUser")
    void updateUserRegistration(@Param(value = "email") String email, @Param(value = "password") String password,
                                @Param(value = "firstName") String firstName, @Param(value = "lastName") String lastName,
                                @Param(value = "phone") String phone, @Param(value = "idUser") Long idUser);
}
