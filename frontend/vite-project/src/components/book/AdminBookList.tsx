import { useEffect, useState } from 'react';
import { useUser } from '../../context/UserContext.tsx';
import { fetchWithRefresh } from '../../utils/fetchWithRefresh.tsx';
import { BookDto } from '../../types/BookDto.tsx';
import { useNavigate } from 'react-router-dom';
import '../../css/AdminBookPage.css';

export default function AdminBookList() {
  const { user, loading } = useUser();
  const [books, setBooks] = useState<BookDto[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();

  const fetchBooks = async () => {
    try {
      const res = await fetchWithRefresh(`http://localhost:8080/books?page=${page}&size=10`, {
        method: 'GET'
      });
      if (!res.ok) throw new Error(res.statusText);

      const data = await res.json();

      const mapped: BookDto[] = data.content.map((b: any) => ({
        bookId:        b.book_id,
        title:         b.title,
        yearRelease:   b.yearRelease,
        language:      b.language,
        jpg:           b.jpg,
        pageCount:     b.pageCount,
        description:   b.description,
        authors:       b.authors,
        categories:    b.categories,
        totalCopies:   b.totalCopies,
        availableCopies: b.availableCopies,
      }));

      setBooks(mapped);
      setTotalPages(data.totalPages);
    } catch (err) {
      console.error('Fetch books error:', err);
      alert('Błąd pobierania książek');
    }
  };

  useEffect(() => {
    fetchBooks();
  }, [page]);

  if (!user || loading) {
    return (
        <div className="main-container">
          <p className="error-message">Użytkownik niezalogowany</p>
        </div>
    );
  }

  const handleDeleteBook = async (id: number) => {
    if (!window.confirm('Czy na pewno chcesz usunąć tę książkę?')) return;
    try {
      const res = await fetchWithRefresh(`http://localhost:8080/books/${id}`, {
        method: 'DELETE',
        credentials: 'include'
      });
      if (!res.ok) {
        const errMsg = await res.text();
        alert(`Błąd: ${errMsg}`);
        return;
      }
      alert(`Książka o ID ${id} została usunięta.`);
      fetchBooks();
    } catch (e) {
      console.error(e);
      alert('Błąd połączenia z serwerem.');
    }
  };

  const handleEditBook = (id: number) => {
    navigate(`/books/edit/${id}`);
  };

  return (
      <div className="wrapper-book">
        <div className="return-container-book">
          <h2>Lista Książek</h2>
          {!books.length ? (
              <p>Brak książek do wyświetlenia</p>
          ) : (
              <>
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
                        <td>{idx + 1 + page * 10}</td>
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

                <div className="pagination-controls">
                  <button onClick={() => setPage(p => Math.max(p - 1, 0))} disabled={page === 0}>
                    Poprzednia
                  </button>
                  <span>Strona {page + 1} z {totalPages}</span>
                  <button onClick={() => setPage(p => Math.min(p + 1, totalPages - 1))} disabled={page + 1 >= totalPages}>
                    Następna
                  </button>
                </div>
              </>
          )}
        </div>
      </div>
  );
}
