import { useEffect, useState } from "react";
import "../css/RandomBookSlider.css";
import {useNavigate} from "react-router-dom";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";

interface Book {
    book_id: number;
    title: string;
    jpg?: string;
}

const RandomBooksSlider = () => {
    const [books, setBooks] = useState<Book[]>([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [animationKey, setAnimationKey] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {
        fetchWithRefresh("http://localhost:8080/book/randomBooks", {
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
                {book.jpg && <img src={book.jpg} alt={book.title}/>}
                <h3 className="book-title-random">{book.title}</h3>
            </div>
            <div className="progress-bar-wrapper">
                <div key={animationKey} className="progress-bar"/>
            </div>
            <div className="slider-buttons">
                <button onClick={prevSlide}>←</button>
                <button onClick={nextSlide}>→</button>
            </div>
        </div>
    );
};

export default RandomBooksSlider;
