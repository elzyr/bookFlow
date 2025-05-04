import { useUser } from "../context/UserContext.tsx";
import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../utils/fetchWithRefresh.tsx";
import { BookDto } from "../types/BookDto.tsx";
import "../css/AdminBookPage.css";
import { useNavigate } from "react-router-dom";

const AdminBookPage = () => {
  const navigate = useNavigate();
  const { user, loading } = useUser();
  const [books, setBooks] = useState<BookDto[]>([]);

  const fetchBooks = () => {
    fetchWithRefresh(`http://localhost:8080/books`, {
      method: "GET",
    })
      .then((res) => {
        if (!res.ok) return null;
        return res.json();
      })
      .then((data: any[] | null) => {
        if (!data) return;
        const mapped: BookDto[] = data.map((b) => ({
          bookId: b.book_id,
          title: b.title,
          yearRelease: b.yearRelease,
          language: b.language,
          jpg: b.jpg,
          pageCount: b.pageCount,
          description: b.description,
          authors: b.authors,
          categories: b.categories,
          totalCopies: b.totalCopies,
          availableCopies: b.availableCopies,
        }));
        setBooks(mapped);
      })
      .catch((err) => {
        console.error("Fetch books error:", err);
      });
  };

  useEffect(() => {
    fetchBooks();
  }, []);

  if (!user || loading) {
    return (
      <div className="main-container">
        <p className="error-message">Użytkownik niezalogowany</p>
      </div>
    );
  }

  const handleDeleteBook = async (id: number) => {
    try {
      const res = await fetchWithRefresh(
        `http://localhost:8080/books/${id}`,
        { method: "DELETE" }
      );
      if (!res.ok) {
        const err = await res.text();
        alert(`Błąd: ${err}`);
      } else {
        alert(`Książka o ID ${id} została usunięta.`);
        fetchBooks();
      }
    } catch (e) {
      console.error(e);
      alert("Błąd połączenia z serwerem.");
    }
  };

  const handleEditBook = (id: number) => {
    navigate(`/admin/books/${id}`);
  };

  return (
    <div className="wrapper-book">
      <div className="return-container-book">
        <h2>Lista Książek</h2>
        {!books || books.length === 0 ? (
          <p>Brak książek do wyświetlenia</p>
        ) : (
          <table className="book-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Tytuł</th>
                <th>Rok</th>
                <th>Język</th>
                <th>Liczba stron</th>
                <th>Akcje</th>
              </tr>
            </thead>
            <tbody>
              {books.map((b, idx) => (
                <tr key={b.bookId}>
                  <td>{idx + 1}</td>
                  <td>{b.title}</td>
                  <td>{b.yearRelease}</td>
                  <td>{b.language}</td>
                  <td>{b.pageCount}</td>
                  <td className="actions-cell">
                    <button
                      className="editBook-button"
                      onClick={() => handleEditBook(b.bookId)}
                    >
                      Modyfikuj
                    </button>
                    <button
                      className="deleteBook-button"
                      onClick={() => handleDeleteBook(b.bookId)}
                    >
                      Usuń
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default AdminBookPage;
