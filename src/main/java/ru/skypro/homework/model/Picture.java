package ru.skypro.homework.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pictures")
public class Picture {

    @Id
    /*@GeneratedValue(strategy = GenerationType.IDENTITY)*/
    @Column(name = "id_picture", unique = true)
    private Long id;

    /*private Integer fileSize;*/

    private Long fileSize;
    private String filePath;
    private String mediaType;
    @Lob
    @Type (type = "org.hibernate.type.ImageType")
    private byte[] data;

    @OneToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "id_ads")

    @MapsId
    private Ads ads;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return Objects.equals(id, picture.id) && Objects.equals(fileSize, picture.fileSize)
                && Objects.equals(filePath, picture.filePath) && mediaType.equals(picture.mediaType)
                && Arrays.equals(data, picture.data) && ads.equals(picture.ads);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, fileSize, filePath, mediaType, ads);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }


}
