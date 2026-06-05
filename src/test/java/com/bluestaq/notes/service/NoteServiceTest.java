package com.bluestaq.notes.service;

import com.bluestaq.notes.model.Note;
import com.bluestaq.notes.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNote_Success() {
        String content = "Test note";
        Note savedNote = new Note(content);
        savedNote.setId(1L);

        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        Note result = noteService.createNote(content);

        assertNotNull(result);
        assertEquals(1L,result.getId());
        assertEquals(content,result.getContent());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void testCreateNote_EmptyContent_Throws() {
        assertThrows(IllegalArgumentException.class, () -> noteService.createNote(""));
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void testGetNoteById_Found() {
        Note note = new Note("Test");
        note.setId(1L);

        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        Optional<Note> result = noteService.getNoteById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test",result.get().getContent());
    }

    @Test
    void testDeleteNoteById_Success() {
        when(noteRepository.existsById(1L)).thenReturn(true);

        noteService.deleteNoteById(1L);
        verify(noteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNoteById_NotFound_Throws() {
        when(noteRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> noteService.deleteNoteById(1L));
        verify(noteRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteNote_NotFound() {
        assertThrows(IllegalArgumentException.class, () ->
                noteService.deleteNoteById(999L));
    }
}
