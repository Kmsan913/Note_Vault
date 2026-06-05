package com.bluestaq.notes.controller;

import com.bluestaq.notes.model.Note;
import com.bluestaq.notes.repository.NoteRepository;
import com.bluestaq.notes.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @MockBean
    private NoteRepository noteRepository;

    @Test
    void testCreateNote_success() throws Exception {
        Note note = new Note("Test note");
        note.setId(1L);

        when(noteService.createNote(any(String.class))).thenReturn(note);

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Test note\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test note"));
    }

    @Test
    void testGetAllNotes_Success() throws Exception {
        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetNoteById_Found() throws Exception {
        Note note = new Note("Test");
        note.setId(1L);

        when(noteService.getNoteById(1L)).thenReturn(Optional.of(note));

        mockMvc.perform(get("/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test"));
    }

    @Test
    void testGetNoteById_NotFound() throws Exception {
        when(noteService.getNoteById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/notes/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNote_success() throws Exception {
        mockMvc.perform(delete("/notes/1"))
                .andExpect(status().isNoContent());

        verify(noteService, times(1)).deleteNoteById(1L);
    }

    @Test
    void testCreateNote_EmptyContent() throws Exception {
        mockMvc.perform(post("/notes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"content\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteNote_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Note not found with id: 999")).when(noteService).deleteNoteById(999L);

        mockMvc.perform(delete("/notes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateNote_NullContent() throws Exception {
        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateNote_OnlyWhitespaceOnly() throws Exception {
        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"      \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllNotes_Empty() throws Exception {
        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testCreateNote_EmptyJSON() throws Exception {
        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
