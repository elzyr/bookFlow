import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../../utils/fetchWithRefresh.tsx";
import "../../css/BookPage.css";
import { useNavigate, useSearchParams } from "react-router-dom";

class Authors {
    name: string | undefined;
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

interface BookPageResponse {
    content: BookDto[];
    pageNumber: number;
    totalPages: number;
}

const BookPage = () => {
    const [bookList, setBookList] = useState<BookDto[]>([]);
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();

    const initialPage = parseInt(searchParams.get("page") || "0", 10);
    const [currentPage, setCurrentPage] = useState<number>(initialPage);
    const [totalPages, setTotalPages] = useState<number>(0);
    const pageSize = 5;
    const [sortKey, setSortKey] = useState("title");
    const [sortDirection, setSortDirection] = useState("asc");
    const [search, setSearch] = useState("")

    const fetchBooks = async (page: number) => {
        try {
            const params = new URLSearchParams({
                page: page.toString(),
                size: pageSize.toString(),
                sort: `${sortKey},${sortDirection}`
            });

            if (search.trim() !== "") {
                params.append("search", search);
                params.append("filterField", sortKey);
            }

            const res = await fetchWithRefresh(`http://localhost:8080/books/all?${params.toString()}`, {
                method: "GET",
            });

            if (!res.ok) {
                throw new Error("Błąd serwera: " + res.status);
            }

            const data: BookPageResponse = await res.json();
            setBookList(data.content);
            setCurrentPage(data.pageNumber);
            setTotalPages(data.totalPages);
        } catch (err) {
            console.error("Błąd pobierania danych:", err);
        }
    };


    useEffect(() => {
        fetchBooks(currentPage);
        setSearchParams({ page: currentPage.toString() });
    }, [currentPage, sortKey, sortDirection, search]);


    return (
        <div className="book-wrapper">
            <div className="filters">
                <label>Sortuj według&nbsp;
                    <select value={sortKey} onChange={(e) => {
                        setSortKey(e.target.value);
                        setCurrentPage(0);
                    }}>
                        <option value="title">Tytuł</option>
                        <option value="authors.name">Autor</option>
                    </select>
                </label>

                <label>Kierunek&nbsp;
                    <select value={sortDirection} onChange={(e) => {
                        setSortDirection(e.target.value);
                        setCurrentPage(0);
                    }}>
                        <option value="asc">A-Z</option>
                        <option value="desc">Z-A</option>
                    </select>
                </label>

                <label>Wyszukaj&nbsp;
                    <input
                        type="text"
                        value={search}
                        onChange={(e) => {
                            setSearch(e.target.value);
                            setCurrentPage(0);
                        }}
                        placeholder="np. Sapkowski"
                    />
                </label>
            </div>
            <h2>Lista książek</h2>
            {bookList.length > 0 ? (
                <ul>
                    {bookList.map((book) => (
                        <li className="book-div" key={book.book_id}>
                            <div className="book-image">
                                <h3><b>{book.title}</b></h3>
                                <img src={book.jpg} alt={book.title}/>
                            </div>
                            <div className="book-details">
                                <p><strong>Opis:</strong> {book.description.substring(0, 300)}...</p>
                                <p><strong>Autorzy:</strong> {book.authors.map(a => a.name).join(", ")}</p>
                                <p><strong>Kategorie:</strong> {book.categories.map(c => c.categoryName).join(", ")}</p>
                                <p><strong>Ilość kopii:</strong> {book.totalCopies}</p>
                                <p><strong>Dostępne:</strong> {book.availableCopies}</p>
                                <button onClick={() => navigate(`/books/info/${book.book_id}`)}>
                                    Zobacz więcej
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Ładowanie danych...</p>
            )}

            <div className="pagination">
                {Array.from({length: totalPages}, (_, i) => (
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
