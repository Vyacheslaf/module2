package com.epam.esm.controller;

import com.epam.esm.dao.Dao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.sql.GiftCertificateDaoImpl;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.GlobalExceptionHandler;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.GiftCertificateServiceImpl;
import com.epam.esm.service.Service;
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
    private GiftCertificateService service;

    @BeforeEach
    public void setup() throws Exception {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .setName("testDB;MODE=MySQL")
                .addScript("test/schema.sql")
                .addScript("test/data.sql")
                .build();
        GiftCertificateDao dao = new GiftCertificateDaoImpl(dataSource);
        service = new GiftCertificateServiceImpl(dao);
        mockMvc = MockMvcBuilders.standaloneSetup(new GiftCertificateController(service))
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
                .andExpect(jsonPath("$.tags[0].id").value("1"))
                .andExpect(jsonPath("$.tags[0].tagName").value("tag1"))
                .andExpect(jsonPath("$.tags[1].id").value("3"))
                .andExpect(jsonPath("$.tags[1].tagName").value("tag3"))
                .andReturn();
    }

    @Test
    public void findAllTest() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("search", "es");
        params.add("sortBy", "DATE");
        params.add("sortDir", "DESC");
        params.add("sortBy", "NAME");
        params.add("sortDir", "ASC");
        mockMvc.perform(get("/certificate").params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[0].name").value("name2"))
                .andExpect(jsonPath("$[0].description").value("description2"))
                .andExpect(jsonPath("$[0].price").value("222"))
                .andExpect(jsonPath("$[0].duration").value("22"))
                .andExpect(jsonPath("$[0].createDate").value("2002-02-02T02:02:02.002"))
                .andExpect(jsonPath("$[0].lastUpdateDate").value("2002-02-02T02:02:02.002"))
                .andExpect(jsonPath("$[0].tags[0].id").value("2"))
                .andExpect(jsonPath("$[0].tags[0].tagName").value("tag2"))
                .andExpect(jsonPath("$[0].tags[1].id").value("3"))
                .andExpect(jsonPath("$[0].tags[1].tagName").value("tag3"))
                .andExpect(jsonPath("$[1].id").value("1"))
                .andExpect(jsonPath("$[1].name").value("name1"))
                .andExpect(jsonPath("$[1].description").value("description1"))
                .andExpect(jsonPath("$[1].price").value("111"))
                .andExpect(jsonPath("$[1].duration").value("11"))
                .andExpect(jsonPath("$[1].createDate").value("2001-01-01T01:01:01.001"))
                .andExpect(jsonPath("$[1].lastUpdateDate").value("2001-01-01T01:01:01.001"))
                .andExpect(jsonPath("$[1].tags[0].id").value("1"))
                .andExpect(jsonPath("$[1].tags[0].tagName").value("tag1"))
                .andExpect(jsonPath("$[1].tags[1].id").value("3"))
                .andExpect(jsonPath("$[1].tags[1].tagName").value("tag3"))
                .andReturn();
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete("/certificate/{id}", 2))
                .andExpect(status().isNoContent());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("search", "es");
        params.add("sortBy", "DATE");
        params.add("sortDir", "DESC");
        params.add("sortBy", "NAME");
        params.add("sortDir", "ASC");
        mockMvc.perform(get("/certificate").params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[0].description").value("description1"))
                .andExpect(jsonPath("$[0].price").value("111"))
                .andExpect(jsonPath("$[0].duration").value("11"))
                .andExpect(jsonPath("$[0].createDate").value("2001-01-01T01:01:01.001"))
                .andExpect(jsonPath("$[0].lastUpdateDate").value("2001-01-01T01:01:01.001"))
                .andExpect(jsonPath("$[0].tags[0].id").value("1"))
                .andExpect(jsonPath("$[0].tags[0].tagName").value("tag1"))
                .andExpect(jsonPath("$[0].tags[1].id").value("3"))
                .andExpect(jsonPath("$[0].tags[1].tagName").value("tag3"))
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
        String jsonBody = "{\"tags\":[{\"tagName\":\"new_tag\"}]}";

        mockMvc.perform(put("/certificate/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("name1"))
                .andExpect(jsonPath("$.description").value("description1"))
                .andExpect(jsonPath("$.price").value("111"))
                .andExpect(jsonPath("$.duration").value("11"))
                .andExpect(jsonPath("$.createDate").value("2001-01-01T01:01:01.001"))
                .andExpect(jsonPath("$.tags[0].id").value("4"))
                .andExpect(jsonPath("$.tags[0].tagName").value("new_tag"))
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
