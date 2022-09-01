package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
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

    private String image;

    @OneToOne(mappedBy = "ads", cascade=CascadeType.ALL)

    @PrimaryKeyJoinColumn
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

    @Override
    public String toString() {
        return "Ads{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", user=" + user +
                ", image='" + image + '\'' +
                ", picture=" + picture.getId() +
                ", adsComments=" + adsComments +
                '}';
    }
}
