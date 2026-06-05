package com.bluestaq.notes.repository;

import com.bluestaq.notes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note,Long>{
}
