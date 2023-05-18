package com.epam.esm.controller;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@RequestMapping("/data")
public class DataFillingController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataFillingController(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostMapping
    public void fillDatabase() throws IOException {
        List<String> wordsList = Files.readAllLines(Paths.get("c:/EPAM/Java/words.txt"));

        for (int i = 0; i < 1000; i++) {
            int tagId = i + 1;
            createTag("tag" + tagId);
        }

        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            GiftCertificate certificate = new GiftCertificate();
            certificate.setName(wordsList.get(random.nextInt(wordsList.size())));
            certificate.setDescription(wordsList.get(random.nextInt(wordsList.size())) + " " +
                    wordsList.get(random.nextInt(wordsList.size())) + " " +
                    wordsList.get(random.nextInt(wordsList.size())));
            certificate.setPrice(random.nextInt(1000));
            certificate.setDuration(random.nextInt(1000));
            certificate.setCreateDate(getRandomDate(random));
            certificate.setLastUpdateDate(getRandomDate(random));
            Set<Tag> tags = new HashSet<>();
            int randomTagsSize = random.nextInt(10) + 1;
            for (int j = 0; j < randomTagsSize; j++) {
                tags.add(new Tag(random.nextInt(1000) + 1, null));
            }
            certificate.setTags(tags);
            createCertificate(certificate);
        }

        for (int i = 0; i < 1000; i++) {
            int userId = i + 1;
            User user = new User();
            user.setUsername("user" + userId);
            user.setEmail("email" + userId);
            createUser(user);
        }

        for (int j = 0; j < 50000; j++) {
            int certificateId = random.nextInt(10000) + 1;
            int userId = random.nextInt(1000) + 1;
            createOrder(userId, certificateId, getRandomDate(random));
        }
    }

    private void createCertificate(GiftCertificate certificate) {
        String CREATE_QUERY = "INSERT INTO gift_certificate VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
            int k = 0;
            pstmt.setString(++k, certificate.getName());
            pstmt.setString(++k, certificate.getDescription());
            pstmt.setInt(++k, certificate.getPrice());
            pstmt.setInt(++k, certificate.getDuration());
            pstmt.setTimestamp(++k, Timestamp.valueOf(certificate.getCreateDate()));
            pstmt.setTimestamp(++k, Timestamp.valueOf(certificate.getLastUpdateDate()));
            return pstmt;
        }, keyHolder);
        certificate.setId(keyHolder.getKey().longValue());
        addTags(certificate);
    }

    private void addTags(GiftCertificate certificate) {
        String CREATE_CERTIFICATE_TAG_QUERY = "INSERT INTO gift_certificate_tag VALUES (?, ?)";
        for (Tag tag : certificate.getTags()) {
            jdbcTemplate.update(CREATE_CERTIFICATE_TAG_QUERY, certificate.getId(), tag.getId());
        }
    }

    private void createTag(String tagName) {
        String CREATE_QUERY = "INSERT INTO tag (name) VALUES (?)";
        jdbcTemplate.update(connection -> {
                PreparedStatement pstmt = connection.prepareStatement(CREATE_QUERY);
                pstmt.setString(1, tagName);
                return pstmt;
            });
    }

    private void createUser(User user) {
        String CREATE_QUERY = "INSERT INTO `user` (username, email) VALUES (?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            return pstmt;
        });
    }

    private void createOrder(int userId, int certificateId, LocalDateTime purchaseDate) {
        String CREATE_QUERY = "INSERT INTO `order` VALUES (DEFAULT, ?, ?, (SELECT price FROM gift_certificate WHERE id = ?), ?)";
        jdbcTemplate.update(connection -> {
                PreparedStatement pstmt = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                int k = 0;
                pstmt.setLong(++k, userId);
                pstmt.setLong(++k, certificateId);
                pstmt.setLong(++k, certificateId);
                pstmt.setTimestamp(++k, Timestamp.valueOf(purchaseDate));
                return pstmt;
            });
    }

    private LocalDateTime getRandomDate(Random random) {
        long minSecond = LocalDateTime.of(2010, 1, 1,0, 0).toEpochSecond(ZoneOffset.UTC);
        long maxSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long randomSecond = random.nextLong(minSecond, maxSecond);
        return LocalDateTime.ofEpochSecond(randomSecond, 0, ZoneOffset.UTC);
    }
}
