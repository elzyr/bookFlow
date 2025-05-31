import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchWithRefresh } from "../utils/fetchWithRefresh.tsx";
import "../css/BookInfo.css";
import Notification from "../components/Notification";
import { useUser } from "../context/UserContext.tsx";

class Authors {
    name: string | undefined;
    information: string | undefined;
}

class Category {
    categoryName: string | undefined;
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
    const [isAlreadyLoaned, setIsAlreadyLoaned] = useState(false);
    const [notification, setNotification] = useState<{ message: string; type?: "success" | "error" } | null>(null);

    const fetchBookData = () => {
        fetchWithRefresh(`http://localhost:8080/books/${id}`, {
            method: "GET"
        })
            .then(response => response.json())
            .then((data: BookDto) => setBook(data))
            .catch(err => console.error("Failed to fetch book:", err));
    };

    const fetchLoaned = () => {
        fetchWithRefresh(`http://localhost:8080/loans/isLoaned?bookId=${id}`, {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then((loaned: boolean) => setIsAlreadyLoaned(loaned))
            .catch(console.error);
    };

    useEffect(() => {
        fetchBookData();
        fetchLoaned();
    }, [id]);

    const handleLoan = async (e: React.FormEvent) => {
        if (!user) return;
        e.preventDefault();
        const res = await fetchWithRefresh(
            `http://localhost:8080/loans?bookId=${book?.book_id}`,
            { method: "POST" }
        );
        if (res.ok) {
            setNotification({ message: "Książka została zarezerwowana!", type: "success" });
            setTimeout(() => {
                window.location.reload();
            }, 3000);
            fetchBookData();
        } else {
            const text = await res.text();
            setNotification({ message: text, type: "error" });
        }
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
                    <p><strong>Kategorie</strong> {book.categories.map(c => c.categoryName).join(", ")}</p>
                    <p><strong>Język</strong> {book.language}</p>
                    <p><strong>Rok wydania</strong> {book.yearRelease}</p>
                </div>

                <div className="book-side-meta">
                    <p><strong>Dostępna ilość kopii:</strong> {book.availableCopies}</p>
                    <p><strong>Łączna liczba kopii:</strong> {book.totalCopies}</p>
                    <button
                        className={`borrow-button ${(isAlreadyLoaned || book.availableCopies === 0) ? "borrowed" : ""}`}
                        disabled={isAlreadyLoaned || book.availableCopies === 0}
                        onClick={handleLoan}
                    >
                        {isAlreadyLoaned ? "Zarezerwowana" : "Zarezerwuj"}
                    </button>
                    {book.availableCopies === 0 && (
                        <p className="already-loaned-info">Brak dostępnych egzemplarzy!</p>
                    )}
                    {isAlreadyLoaned && (
                        <p className="already-loaned-info">Ta książka jest obecnie przypisana do twojego konta</p>
                    )}
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
            {notification && (
                <Notification
                    message={notification.message}
                    type={notification.type}
                    onClose={() => setNotification(null)}
                />
            )}
        </div>
    );
};

export default BookInfo;
