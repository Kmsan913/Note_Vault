package com.bluestaq.notes.controller;


import com.bluestaq.notes.model.Note;
import com.bluestaq.notes.model.NoteResponse;
import com.bluestaq.notes.repository.NoteRepository;
import com.bluestaq.notes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;
    private NoteRepository noteRepository;

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@RequestBody NoteRequest request){
        if (request.content == null || request.content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try{
            Note note = noteService.createNote(request.content);
            return ResponseEntity.status(HttpStatus.CREATED).body(new NoteResponse(note));
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes(){
        List<Note> notes = noteService.getAllNotes();
        List<NoteResponse> responses = notes.stream()
                .map(NoteResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable long id){
        Optional<Note> note = noteService.getNoteById(id);
        return note.map(n-> ResponseEntity.ok(new NoteResponse(n)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable long id) {
        try{
            noteService.deleteNoteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public static class NoteRequest{
        private String content;

        public NoteRequest() {}

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
