import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../utils/fetchWithRefresh.tsx";
import "../css/BookPage.css";

class Authors {
    name: string | undefined;
    info: string | undefined;
}

class Category {
    category_name: String | undefined;
}

interface BookDto {
    book_id: number;
    title: string;
    yearRelease: number;
    language: string;
    jpg: string;
    pageCount: number;
    description: string;
    authors: Authors[];
    categories: Category[];
    totalCopies: number;
    availableCopies: number;
}

interface BookPageResponse {
    content: BookDto[];
    currentPage: number;
    totalPages: number;
}

const BookPage = () => {
    const [bookList, setBookList] = useState<BookDto[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 5;

    const fetchBooks = async (page: number) => {
        try {
            const res = await fetchWithRefresh(`http://localhost:8080/book/all?page=${page}&size=${pageSize}`, {
                method: "GET",
                credentials: "include"
            });

            if (!res.ok) {
                throw new Error("Błąd serwera: " + res.status);
            }

            const data: BookPageResponse = await res.json();
            setBookList(data.content);
            setCurrentPage(data.currentPage);
            setTotalPages(data.totalPages);
        } catch (err) {
            console.error("Błąd pobierania danych:", err);
        }
    };

    useEffect(() => {
        fetchBooks(currentPage);
    }, [currentPage]);

    return (
        <div className="book-wrapper">
            <h2>Lista książek</h2>
            {bookList.length > 0 ? (
                <ul>
                    {bookList.map((book) => (
                        <div className="book-div" key={book.book_id}>
                            <div className="book-image">
                                <h3><b>{book.title}</b></h3>
                                <img src={book.jpg} alt={book.title} />
                            </div>
                            <div className="book-details">
                                <p><strong>Opis:</strong> {book.description}</p>
                                <p><strong>Autorzy:</strong> {book.authors.map(a => a.name).join(", ")}</p>
                                <p><strong>Kategorie:</strong> {book.categories.map(c => c.category_name).join(", ")}</p>
                                <p><strong>Ilość kopii:</strong> {book.totalCopies}</p>
                                <p><strong>Dostępne:</strong> {book.availableCopies}</p>
                                <button onClick={() => window.location.href = `/book/${book.book_id}`}>Zobacz więcej</button>
                            </div>
                        </div>
                    ))}
                </ul>
            ) : (
                <p>Ładowanie danych...</p>
            )}

            <div className="pagination">
                {Array.from({ length: totalPages }, (_, i) => (
                    <button
                        key={i}
                        onClick={() => setCurrentPage(i)}
                        className={i === currentPage ? "active-page" : ""}
                    >
                        {i + 1}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default BookPage;
