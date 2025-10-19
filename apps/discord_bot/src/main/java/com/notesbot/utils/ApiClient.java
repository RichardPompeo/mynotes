package com.notesbot.utils;

import com.notesbot.models.Note;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.time.format.DateTimeFormatter;

public class ApiClient {
    private static final String API_URL = "http://localhost:8080/notes";
    private final HttpClient client;
    private final ObjectMapper mapper;

    public ApiClient() {
        this.client = HttpClients.createDefault();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new Jdk8Module());
    }

    public List<Note> listNotes() {
        try {
            HttpGet request = new HttpGet(API_URL);
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);

            String json = EntityUtils.toString(response.getEntity());

            return Arrays.asList(mapper.readValue(json, Note[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Note createNote(Note note) {
        try {
            HttpPost request = new HttpPost(API_URL);

            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(mapper.writeValueAsString(note)));

            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);

            String json = EntityUtils.toString(response.getEntity());

            return mapper.readValue(json, Note.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Note updateNote(Long id, Note note) {
        try {
            HttpPut request = new HttpPut(API_URL + "/" + id);

            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(mapper.writeValueAsString(note)));

            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);

            String json = EntityUtils.toString(response.getEntity());

            return mapper.readValue(json, Note.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean deleteNote(Long id) {
        try {
            HttpDelete request = new HttpDelete(API_URL + "/" + id);

            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);

            if (response.getCode() != HttpStatus.SC_NO_CONTENT) {
                throw new RuntimeException("Failed to delete note");
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
