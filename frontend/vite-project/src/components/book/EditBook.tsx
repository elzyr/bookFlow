import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BookForm, { BookFormData } from './BookForm';
import { fetchWithRefresh } from '../../utils/fetchWithRefresh.tsx';

interface ApiBookDto {
  id: number;
  title: string;
  authors: { name: string }[];
  yearRelease: number;
  language: string;
  jpg: string | null;
  pageCount: number;
  description: string;
  categories: { categoryName: string }[];
  totalCopies: number;
}

const EditBook: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [initialData, setInitialData] = useState<BookFormData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    (async () => {
      try {
        const res = await fetchWithRefresh(`http://localhost:8080/books/${id}`, {
          method: 'GET',
          credentials: 'include',
        });
        if (!res.ok) throw new Error(`Status ${res.status}`);
        const data = (await res.json()) as ApiBookDto;
        setInitialData({
          title: data.title,
          authors: data.authors.map(a => ({ value: a.name, label: a.name })),
          yearRelease: data.yearRelease,
          language: data.language,
          jpg: data.jpg ?? '',
          pageCount: data.pageCount,
          description: data.description,
          categories: data.categories.map(c => ({ value: c.categoryName, label: c.categoryName })),
          totalCopies: data.totalCopies,
        });
      } catch (err) {
        console.error('Error loading book:', err);
        alert('Błąd podczas ładowania danych książki');
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  const handleSuccess = () => {
    navigate('/books/edit');
  };

  if (loading) return <div>Ładowanie...</div>;
  if (!initialData) return <div>Błąd podczas ładowania danych książki.</div>;

  return (
    <BookForm
      initialData={initialData}
      bookId={Number(id)}
      onSuccess={handleSuccess}
    />
  );
};

export default EditBook;