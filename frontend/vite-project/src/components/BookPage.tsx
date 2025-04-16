import {useEffect, useState} from "react";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";
import "../css/BookPage.css"

class Authors {
    name: string | undefined;
    info : string | undefined;
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
    categories : Category[]
}

const BookPage = () => {

    const [bookList, setBookList] = useState<BookDto[] | null>(null);

    useEffect(() => {
        fetchWithRefresh("http://localhost:8080/book/all", {
            method: "GET",
            credentials: "include"
        })
            .then(async res => {
                if (!res.ok) {
                    throw new Error("Błąd serwera: " + res.status);
                }
                return res.json();
            })
            .then((bookData: BookDto[]) => {
                setBookList(bookData);
            })
            .catch(err => console.error("Błąd pobierania danych:", err));
    }, []);

    return (
        <div className="book-wrapper">
            <h2>Lista książek</h2>
            {bookList ? (
                <ul>
                    {bookList.map((book) => (
                        <div className="book-div" key={book.book_id}>
                            <div className="book-image">
                                <h3><b>{book.title}</b></h3>
                                <img src={book.jpg} alt={book.title}/>
                            </div>
                            <div className="book-details">
                                <p><strong>Opis:</strong> {book.description}</p>
                                <p><strong>Język:</strong> {book.language}</p>
                                <p><strong>Strony:</strong> {book.pageCount}</p>
                                <p><strong>Autorzy</strong> {book.authors.map(a => a.name).join(", ")}</p>
                                <p><strong>Kategorie:</strong> {book.categories.map(c => c.category_name).join(", ")}</p>
                                <button onClick={() => window.location.href = `/book/${book.book_id}`}>Zobacz więcej
                                </button>
                            </div>
                        </div>
                    ))}
                </ul>
            ) : (
                <p>Ładowanie danych...</p>
            )}
        </div>
    );

};
export default BookPage;