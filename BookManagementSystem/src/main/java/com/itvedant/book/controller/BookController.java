package com.itvedant.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.itvedant.book.dao.AddBookDAO;
import com.itvedant.book.dao.UpdateBookDAO;
import com.itvedant.book.service.BookService;

@Controller
@RequestMapping("/books")
public class BookController {

	@Autowired
	private BookService bookservice;
	
	@PostMapping("")
	public ResponseEntity<?> create(@RequestBody AddBookDAO addBookDAO){
		return ResponseEntity.ok(this.bookservice.createBook(addBookDAO));
	}
	
	@GetMapping("")
	public ResponseEntity<?> readAll(){
		return ResponseEntity.ok(this.bookservice.readAllBook());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> readById(@PathVariable Integer id){
		return ResponseEntity.ok(this.bookservice.readByBookId(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@RequestBody UpdateBookDAO updateBookDAO ,@PathVariable Integer id){
		return ResponseEntity.ok(this.bookservice.updateBook(updateBookDAO, id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id){
		return ResponseEntity.ok(this.bookservice.deleteBook(id));
	}
	
	@PostMapping("/{id}/upload")
	public ResponseEntity<?> upload(@PathVariable Integer id,@RequestParam("file") MultipartFile file){
		return ResponseEntity.ok(this.bookservice.storeFile(id, file));
	}
	
	@GetMapping("/download/{fileName}")
	public ResponseEntity<?> download(@PathVariable String fileName){
		Resource resource = this.bookservice.loadAsResource(fileName); 
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
				                          + fileName + "\"").body(resource);
	}
 }
