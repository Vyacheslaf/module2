package com.epam.esm.controller;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.sql.GiftCertificateDaoImpl;
import com.epam.esm.dao.sql.TagDaoImpl;
import com.epam.esm.exception.GlobalExceptionHandler;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.GiftCertificateServiceImpl;
import com.epam.esm.service.TagService;
import com.epam.esm.service.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath*:test/contextConfig.xml")
@WebAppConfiguration
public class TestGiftCertificateController {
    private MockMvc mockMvc;
    private GiftCertificateService giftCertificateService;
    private TagService tagService;

    @BeforeEach
    public void setup() throws Exception {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .setName("testDB;MODE=MySQL")
                .addScript("test/schema.sql")
                .addScript("test/data.sql")
                .build();
        GiftCertificateDao giftCertificateDao = new GiftCertificateDaoImpl(dataSource);
        giftCertificateService = new GiftCertificateServiceImpl(giftCertificateDao);
        TagDao tagDao = new TagDaoImpl(dataSource);
        tagService = new TagServiceImpl(tagDao);
        mockMvc = MockMvcBuilders.standaloneSetup(new GiftCertificateController(giftCertificateService, tagService))
                                 .setControllerAdvice(new GlobalExceptionHandler())
                                 .build();
    }

    @Test
    public void initTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/certificate"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void findByIdTest() throws Exception {
        mockMvc.perform(get("/certificate/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("name1"))
                .andExpect(jsonPath("$.description").value("description1"))
                .andExpect(jsonPath("$.price").value("111"))
                .andExpect(jsonPath("$.duration").value("11"))
                .andExpect(jsonPath("$.createDate").value("2001-01-01T01:01:01.001"))
                .andExpect(jsonPath("$.lastUpdateDate").value("2001-01-01T01:01:01.001"))
                .andReturn();
    }

    @Test
    public void findAllTest() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("search", "es");
        params.add("sort", "create_date.desc");
        params.add("sort", "name.asc");
        mockMvc.perform(get("/certificate").params(params))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andReturn();
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete("/certificate/{id}", 2))
                .andExpect(status().isOk());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("search", "es");
        params.add("sort", "create_date.desc");
        params.add("sort", "name.asc");
        mockMvc.perform(get("/certificate").params(params))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andReturn();
    }

    @Test
    public void createTest() throws Exception {
        String jsonBody = "{\"name\":\"some_name\",\"description\":\"some_description\",\"price\":33,\"duration\":3}";

        mockMvc.perform(post("/certificate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.name").value("some_name"))
                .andExpect(jsonPath("$.description").value("some_description"))
                .andExpect(jsonPath("$.price").value("33"))
                .andExpect(jsonPath("$.duration").value("3"))
                .andReturn();

    }

    @Test
    public void updateTest() throws Exception {
        String jsonBody = "{\"tags\":[{\"name\":\"new_tag\"}]}";

        mockMvc.perform(patch("/certificate/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("name1"))
                .andExpect(jsonPath("$.description").value("description1"))
                .andExpect(jsonPath("$.price").value("111"))
                .andExpect(jsonPath("$.duration").value("11"))
                .andExpect(jsonPath("$.createDate").value("2001-01-01T01:01:01.001"))
                .andReturn();
    }

    @Test
    public void wrongResourceIdTest() throws Exception {
        mockMvc.perform(get("/certificate/{id}", 3))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Requested GiftCertificate not found (id=3)"))
                .andExpect(jsonPath("$.errorCode").value(40403))
                .andReturn();
    }
}
