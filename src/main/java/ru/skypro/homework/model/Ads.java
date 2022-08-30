package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "advertisements")
public class Ads {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ads", unique = true)
    private Long id;
    private String title;
    private String description;
    private Integer price;
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @OneToOne(mappedBy = "ads", cascade=CascadeType.ALL)
//    @JsonIgnore
    @PrimaryKeyJoinColumn
    @ToString.Exclude
    private Picture picture;

    @OneToMany(mappedBy = "ads", cascade=CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private Collection<AdsComment> adsComments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ads ads = (Ads) o;
        return id != null && Objects.equals(id, ads.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
