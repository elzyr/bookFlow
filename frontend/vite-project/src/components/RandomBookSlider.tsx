import { useEffect, useState } from "react";
import "../css/RandomBookSlider.css";
import {useNavigate} from "react-router-dom";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";

interface Book {
    book_id: number;
    title: string;
    jpg?: string;
    description : string;
    language : string;
}

const RandomBooksSlider = () => {
    const [books, setBooks] = useState<Book[]>([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [, setAnimationKey] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {
        fetchWithRefresh("http://localhost:8080/books/randomBooks", {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(data => setBooks(data))
            .catch(err => console.error("Błąd pobierania losowych książek:", err));
    }, []);

    useEffect(() => {
        if (books.length === 0) return;

        const interval = setInterval(() => {
            setCurrentIndex(prev => (prev + 1) % books.length);
            setAnimationKey(prev => prev + 1);
        }, 5000);

        return () => clearInterval(interval);
    }, [books]);

    const prevSlide = () => {
        setCurrentIndex(prev => (prev - 1 + books.length) % books.length);
        setAnimationKey(prev => prev + 1);
    };

    const nextSlide = () => {
        setCurrentIndex(prev => (prev + 1) % books.length);
        setAnimationKey(prev => prev + 1);
    };

    if (books.length === 0) return null;
    const book = books[currentIndex];

    return (
        <div className="slider-container">
            <h2>Książki na dziś!</h2>
            <div className="slide" onClick={() => navigate(`/BookInfo/${book.book_id}`)}>
                {book.jpg && (
                    <div className="slide-image">
                        <img src={book.jpg} alt={book.title} />
                    </div>
                )}
                <div className="slide-content">
                    <h3 className="book-title-random">{book.title}</h3>
                    <p className="book-description">
                        {book.description.length > 400
                            ? `${book.description.substring(0, 200)}...`
                            : book.description}
                    </p>
                    <p className="book-language">Język : {book.language}</p>
                </div>
            </div>
            <div className="slider-buttons">
                <button onClick={prevSlide}>←</button>
                <button onClick={nextSlide}>→</button>
            </div>
        </div>
    );
};

export default RandomBooksSlider;
