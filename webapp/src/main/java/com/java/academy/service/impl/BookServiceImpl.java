package com.java.academy.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.java.academy.dao.CollectedDatesDao;
import com.java.academy.model.Bookstore;
import com.java.academy.model.CollectionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.academy.dao.BookDao;
import com.java.academy.dao.BookstoreDao;
import com.java.academy.model.Book;
import com.java.academy.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	private BookDao bookDao;
	private BookstoreDao bookstoreDao;
	private CollectedDatesDao collectedDatesDao;

	@Autowired
	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}
	@Autowired
	public void setBookstoreDao(BookstoreDao bookstoreDao) {
		this.bookstoreDao = bookstoreDao;
	}
	@Autowired
	public void setCollectedDatesDao(CollectedDatesDao collectedDatesDao) {
		this.collectedDatesDao = collectedDatesDao;
	}

	public List<Book> getAllBooks() {
		return bookDao.findAll();
	}

	public Book getBookById(String bookId) {
		return bookDao.getBookById(bookId);
	}

	public List<Book> getBooksByFilter(String filter, String value) {
		if(value.equals("undefined")) {
			return bookDao.findAll();
		}
		return getBooksFromDaoByFilter(filter, value);
	}

	List<Book> getBooksFromDaoByFilter(String filter, String value) {
		switch (filter) {
			case "title":
				return bookDao.getBooksByTitleContaining(value);
			case "author":
				return bookDao.getBooksByAuthorContaining(value);
			case "category":
				return bookDao.getBooksByCategoryContaining(value);
			case "bookstore":
				return bookDao.getBooksByBookstoreNameContaining(value);
			default:
				return bookDao.getBooksByCategoryContaining(value);
		}
	}

	@Override
	public Set<Book> getBooksByParams(String filter, String value, Map<String, List<String>> filterParams) {
		List<Book> listOfBooks = getBooksByFilter(filter, value);
		Set<String> criterias = filterParams.keySet();
		Set<Book> booksByLowPriceRange = new HashSet<>();
		Set<Book> booksByHighPriceRange = new HashSet<>();
		Set<Book> booksByPriceRange = new HashSet<>();
		/*if(criterias.contains("low")) {
			for(String price: filterParams.get("low")) {
				for(Book book: listOfBooks) {
					if((new BigDecimal(price)).compareTo(book.getPrice()) <= 0){
						booksByLowPriceRange.add(book);
					}
				}
			}
		}
		if(criterias.contains("high")) {
			for(String price: filterParams.get("high")) {
				for(Book book: listOfBooks) {
					if((new BigDecimal(price)).compareTo(book.getPrice()) >= 0){
						booksByHighPriceRange.add(book);
					}
				}
			}
		}*/

		if(!booksByLowPriceRange.isEmpty() && !booksByHighPriceRange.isEmpty()) {
			booksByHighPriceRange.retainAll(booksByLowPriceRange);
			booksByPriceRange.addAll(booksByHighPriceRange);
		}else{
			booksByPriceRange.addAll(booksByHighPriceRange);
			booksByPriceRange.addAll(booksByLowPriceRange);
		}
		return booksByPriceRange;
	}

	public void addBook(Book book, CollectionTime collectionTime) {
		Bookstore bookstore = bookstoreDao.getBookstoreByName(book.getBookstore().getName());
		if(bookstore == null) {
			bookstore = bookstoreDao.save(book.getBookstore());
		}
		book.setBookstore(bookstore);

		Book bookFromBase = bookDao.getBookByTitle(book.getTitle());
		if(bookFromBase == null) {
			bookFromBase = book;
		}
		collectionTime.setBook(bookFromBase);
		bookFromBase.addCollectedDates(collectionTime);

		bookDao.save(bookFromBase);
		collectedDatesDao.save(bookFromBase.getCollectedDates());
	}
}
