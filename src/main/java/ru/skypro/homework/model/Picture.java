package ru.skypro.homework.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "pictures")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_picture", unique = true)
    private Long id;
    private Integer fileSize;
    private String filePath;
    private String mediaType;
    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "id_ads")
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
