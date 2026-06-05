package com.bluestaq.notes.service;

import com.bluestaq.notes.model.Note;
import com.bluestaq.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public Note createNote(String content) {
        if(content == null || content.trim().isEmpty()){
            throw new IllegalArgumentException("Note content cannot be empty");
        }
        Note note = new Note(content);
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    public void deleteNoteById(Long id) {
        if(!noteRepository.existsById(id)){
            throw new IllegalArgumentException("Note with ID" + id + "could not be found");
        }
        noteRepository.deleteById(id);
    }
}
