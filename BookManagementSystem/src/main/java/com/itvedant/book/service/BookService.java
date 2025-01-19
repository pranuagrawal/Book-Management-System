package com.itvedant.book.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.itvedant.book.FileStorageProperties;
import com.itvedant.book.dao.AddBookDAO;
import com.itvedant.book.dao.UpdateBookDAO;
import com.itvedant.book.entity.Book;
import com.itvedant.book.exception.StorageException;
import com.itvedant.book.repository.BookRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
	private final Path rootlocation;
	
	public BookService(FileStorageProperties properties) {
		this.rootlocation = Paths.get(properties.getUploadDir());
		
		try {
			
			Files.createDirectories(rootlocation);
			
			
		} catch (IOException e) {
			throw new StorageException("could not initialize directory");
		}
	}
	
	public Book createBook(AddBookDAO addBookDAO) {
	Book book = new Book();
	
	book.setBook_name(addBookDAO.getBook_name());
	book.setAuthor(addBookDAO.getAuthor());
	book.setPrice(addBookDAO.getPrice());
	
	this.bookRepository.save(book);
	
	return book;
	
	}
	
	public List<Book> readAllBook(){
		
		List<Book> books = new ArrayList<Book>();
		
		books = this.bookRepository.findAll();
		
		return books;
	}
	
	public Book readByBookId(Integer id) {
		
		Book book = new Book();
		
		book = this.bookRepository.findById(id).orElse(null);
		
		return book;
	}
	
	public Book updateBook(UpdateBookDAO updateBookDAO, Integer id) {
		
		Book book = new Book();
		
	    book = this.readByBookId(id);
		
		if(updateBookDAO.getBook_name() != null) {
			book.setBook_name(updateBookDAO.getBook_name());
		}
		
		if(updateBookDAO.getAuthor() != null) {
			book.setAuthor(updateBookDAO.getAuthor());
		}
		
		if(updateBookDAO.getPrice() != null) {
			book.setPrice(updateBookDAO.getPrice());
		}
		
		this.bookRepository.save(book);
		
		return book;
		
	}
	
	public String deleteBook(Integer id) {
	
		Book book = new Book();
		
		book = this.readByBookId(id);
		
		this.bookRepository.delete(book);
		
		return "Data Deleted";
	}
	
	public String storeFile(Integer id, MultipartFile file) {
		
		try {
			
			if(file.isEmpty()) {
				throw new StorageException("Could not Store a Empty file");
			}
			
			Path destinationFile = this.rootlocation.resolve(Paths.get(file.getOriginalFilename()));
			
			try(InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
			
			Book book = this.bookRepository.findById(id).orElse(null);
			
			String fileUploadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					              .path("/books/download/")
					              .path(file.getOriginalFilename())
					              .toUriString();
			
			book.setBook_image(fileUploadUri);
			
			this.bookRepository.save(book);
			
		} catch (Exception e) {
			throw new StorageException("File could not be saved");
		}
		return "File Stored";
	}
	
	public Resource loadAsResource(String fileName) {
		
		Path file = this.rootlocation.resolve(fileName);
		
		try {
			
			Resource resource = new UrlResource(file.toUri());
			
			if(resource.exists() && resource.isReadable()) {
				return resource;
			}else {
				throw new StorageException("Could not read file");
			}
			
		} catch (Exception e) {
			throw new StorageException("Could not read file");
		}
	}
}
