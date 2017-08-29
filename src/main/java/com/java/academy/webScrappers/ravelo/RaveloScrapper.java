package com.java.academy.webScrappers.ravelo;

import com.java.academy.model.Book;
import com.java.academy.model.Bookstore;
import com.java.academy.webScrappers.BookScrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RaveloScrapper implements BookScrapper{

    private static final int FIRST_ELEMENT = 0;
    private final String shopLink;
    private final Bookstore bookStore;
    private Document shopConnection;
    private Element tempBook;
    private String bookCategory;

    RaveloScrapper(String link, Bookstore bookstore) {
        this.shopLink = link;
        this.bookStore = bookstore;
    }

    List<Book> prepareBookPackage() {
        List<Book> booksByGenre = new ArrayList<>();
        String nextPageLink;

        try {
            provideShopConnection(shopLink);

            for (int page = 1; page < getTotalNumberOfPages(shopConnection); page++) {
                booksByGenre.addAll(collectBooksFromSinglePage(shopConnection));
                nextPageLink = shopLink + "&p=" + (page+1);
                provideShopConnection(nextPageLink);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return booksByGenre;
    }
    @Override
    public List<Book> collectBooksFromSinglePage(Document connection) {
        this.shopConnection = connection;
        Elements bookContainer = connection.getElementsByClass("row productBox ");
        List<Book> singlePage = new ArrayList<>();

        bookContainer.forEach(book -> {
            tempBook = book;
            Book singleBook = new Book(getBookTitle(),
                    getBookAuthor(),
                    getBookCategory(),
                    getDiscount(),
                    new BigDecimal(getBookPrice()),
                    bookStore);
            singleBook.setImgUrl(getImageUrl());
            singleBook.setUrl(getBookLink());

            singlePage.add(singleBook);
        });

        return singlePage;
    }

    void provideShopConnection(String linkToConnect) throws IOException {
        shopConnection = Jsoup.connect(linkToConnect).get();
    }

    int getTotalNumberOfPages(Document connection) {
        String[] pages = connection.getElementsByClass("pagination")
                .text()
                .replaceAll("[a-ż]+", "")
                .split(" ");
        return Integer.parseInt(pages[pages.length-1]);
    }

    @Override
    public String getBookAuthor() {
        Elements author = tempBook.getElementsByClass("autor");
        return author.get(FIRST_ELEMENT).getElementsByTag("a").get(FIRST_ELEMENT).text();
    }

    @Override
    public String getImageUrl() {
        Elements image = tempBook.getElementsByClass("span2 imgContainer");
        return image.get(FIRST_ELEMENT)
                .getElementsByTag("a")
                .get(FIRST_ELEMENT).getElementsByTag("img")
                .attr("data-src");
    }

    @Override
    public String getBookCategory() {
        if (bookCategory == null || bookCategory.isEmpty()) {
            Elements genre = shopConnection.getElementsByClass("searchNavHeader");
            bookCategory = genre.get(FIRST_ELEMENT).text()
                    .substring(genre.get(FIRST_ELEMENT).text().indexOf(':') + 1).trim();
            return genre.get(FIRST_ELEMENT).text()
                    .substring(genre.get(FIRST_ELEMENT).text().indexOf(':') + 1).trim();
        }
        return bookCategory;
    }

    @Override
    public String getBookTitle() {
        Elements title = tempBook.getElementsByTag("h2");
        return title.text().replaceAll("Outlet", "");
    }

    @Override
    public String getBookLink() {
        Elements bookLink = tempBook.getElementsByClass("span2 imgContainer");
        return bookLink.get(FIRST_ELEMENT)
                .getElementsByTag("a")
                .attr("href");
    }

    @Override
    public Double getBookPrice() {
        Elements price = tempBook.getElementsByClass("newPrice");
        return Double.parseDouble(price.text().replaceAll("[a-ż]+", ""));
    }

    @Override
    public String getDiscount() {
        Elements discount = tempBook.getElementsByClass("save");
        return discount.text().replaceAll("[a-ż]+", "-");
    }
}

