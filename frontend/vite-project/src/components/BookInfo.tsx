import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchWithRefresh } from "../utils/fetchWithRefresh.tsx";
import "../css/BookInfo.css";
import { useUser } from "../context/UserContext.tsx";

class Authors {
    name: string | undefined;
    information: string | undefined;
}

class Category {
    category_name: string | undefined;
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

const BookInfo = () => {
    const { id } = useParams<{ id: string }>();
    const [book, setBook] = useState<BookDto>();
    const { user, loading } = useUser();

    const fetchBookData = () => {
        fetchWithRefresh(`http://localhost:8080/books/${id}`, {
            method: "GET"
        })
            .then(response => response.json())
            .then((data: BookDto) => setBook(data))
            .catch(err => console.error("Failed to fetch book:", err));
    };

    useEffect(() => {
        fetchBookData();
    }, [id]);

    const handleLoan = async (e: React.FormEvent) => {
        if (!user) return;
        e.preventDefault();
        const res = await fetchWithRefresh(
            `http://localhost:8080/loans/loanBook?bookId=${book?.book_id}`,
            { method: "PUT" }
        );
        if (res.ok) {
            alert("Book loaned successfully");
        } else {
            const text: string = await res.text();
            alert(text);
        }
        fetchBookData();
    };

    if (!book || !user || loading) {
        return <p>Trwa ładowanie ..</p>;
    }

    return (
        <div className="book-details-wrapper">
            <h1 className="book-title">{book.title}</h1>

            <div className="book-main">
                <img className="book-image" src={book.jpg} alt={book.title} />

                <div className="book-center-info">
                    <p><strong>Liczba stron</strong> {book.pageCount}</p>
                    <p><strong>Kategorie</strong> {book.categories.map(c => c.category_name).join(", ")}</p>
                    <p><strong>Język</strong> {book.language}</p>
                    <p><strong>Rok wydania</strong> {book.yearRelease}</p>
                </div>

                <div className="book-side-meta">
                    <p><strong>Dostępna ilość kopii:</strong> {book.availableCopies}</p>
                    <p><strong>Łączna liczba kopii:</strong> {book.totalCopies}</p>
                    <button className="borrow-button" onClick={handleLoan}>Wypożycz</button>
                </div>
            </div>

            <div className="book-info">
                <h2>Opis książki</h2>
                <p>{book.description}</p>
            </div>

            <div className="book-authors">
                <h2>Autorzy</h2>
                {book.authors.map((author, index) => (
                    <div key={index} className="author-block">
                        <div className="author-header">
                            <div className="author-name-wrapper">
                                <p className="author-name">{author.name}</p>
                            </div>
                        </div>
                        <h4 className="intro-author"><strong>O autorze</strong></h4>
                        <p className="author-info-details">{author.information}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default BookInfo;
