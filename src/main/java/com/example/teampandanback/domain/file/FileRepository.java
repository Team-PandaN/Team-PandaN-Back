package com.example.teampandanback.domain.file;


import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryQuerydsl {

}
