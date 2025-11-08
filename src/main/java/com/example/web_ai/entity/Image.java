package com.example.web_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 36)
    UUID id;

    @Lob
    @Column(name = "image_data", columnDefinition = "LONGTEXT")
    private String imageData;

    @Column(name = "content_type", length = 50)
    private String contentType;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

