package edu.asu.ser421.booktown.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.asu.ser421.booktown.api.model.Author;
import edu.asu.ser421.booktown.api.model.AuthorRequest;
import edu.asu.ser421.booktown.api.model.exceptions.AuthorInternalException;
import edu.asu.ser421.booktown.api.model.exceptions.AuthorNotFoundException;
import edu.asu.ser421.booktown.services.AuthorService;

@RequestMapping("/authors")
@RestController
public class AuthorController {
	private AuthorService __authorService = AuthorService.getInstance();
	
	//first endpoint, return a collection of authors
	@GetMapping
	public ResponseEntity<List<Author>> returnAuthors() throws Throwable {
		return new ResponseEntity<List<Author>>(__authorService.getAuthors(), HttpStatus.OK);
	}
	
	//second endpoint, return a specific Author by id
	@GetMapping("/{id}")
	public ResponseEntity<Author> returnAuthor(@PathVariable Integer id) {
		return new ResponseEntity<Author>(__authorService.getAuthor(id), HttpStatus.OK);
	}
	
	// third endpoint, create an Author via POST
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Author> createAuthor(@RequestBody AuthorRequest authorRequest) {
		return new ResponseEntity<Author>(__authorService.createAuthor(authorRequest.getLastName(), authorRequest.getFirstName(), authorRequest.getBooks()), HttpStatus.CREATED);
	}
	
	// Put is interesting in that we either create a new Author like POST, or update an existing one
	// fourth endpoint, Insert or Update Author
	@PutMapping
	public ResponseEntity<Author> insertOrUpdateAuthor(@RequestBody AuthorRequest authorRequest) {
		return createAuthor(authorRequest);

		//__authorService.createAuthor(authorRequest.getLastName(), authorRequest.getFirstName(), authorRequest.getBooks());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Author> insertOrUpdateAuthor(@PathVariable Integer id, @RequestBody AuthorRequest authorRequest) {
		// see if there is an Author with this id
		return new ResponseEntity<Author>(__authorService.updateAuthor(new Author(id, authorRequest)), HttpStatus.OK);
	}
	
	// fifth endpoint, PATCH
	@PatchMapping("/{id}")
	public ResponseEntity<Author> modifyAuthor(@PathVariable Integer id, @RequestBody AuthorRequest partialAuthor) {
		// in Patch, we only expect one or more attributes to be updated, not the whole object.
		return new ResponseEntity<Author>(__authorService.modifyAuthor(new Author(id, partialAuthor)), HttpStatus.OK);
	}
	
	// Sixth endpoint: DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<Author> deleteAuthor(@PathVariable Integer id) {
		return new ResponseEntity<Author>(__authorService.deleteAuthor(id), HttpStatus.NO_CONTENT);
	}
	
	// Class-level Exception Handling methods
	@ExceptionHandler(value = org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handleTypeMismatchException(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException exc) {
		return new ResponseEntity<String>("Invalid type for Author Id, must be an Integer", HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = AuthorNotFoundException.class)
	public ResponseEntity<?> handleAuthorNotFoundException(AuthorNotFoundException exc) {
		return new ResponseEntity<String>(exc.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = AuthorInternalException.class) 
	public ResponseEntity<?> handleThrowable(AuthorInternalException exc) {
		return new ResponseEntity<String>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = java.lang.Throwable.class) 
	public ResponseEntity<?> handleThrowable(java.lang.Throwable t) {
		return new ResponseEntity<String>(t.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}