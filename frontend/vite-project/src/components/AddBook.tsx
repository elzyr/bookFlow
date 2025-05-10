import { useState, useEffect } from "react";
import Select, { MultiValue } from "react-select";
import CreatableSelect from "react-select/creatable";
import { fetchWithRefresh } from "../utils/fetchWithRefresh.tsx";
import "../css/AddBook.css";

// Interfejs wejściowy dla autora (backend zwraca pole author_id)
interface AuthorInput {
  id: number;
}

// Interfejs wejściowy dla kategorii – teraz obsługuje też categoryName
interface CategoryInput {
  categoryId?: number;
  categoryName?: string;
}

// DTO wysyłane do backendu
interface BookDto {
  title: string;
  yearRelease: number;
  language: string;
  jpg: string;
  pageCount: number;
  description: string;
  authors: AuthorInput[];
  categories: CategoryInput[];
  totalCopies: number;
  availableCopies: number;
}

// react-select potrzebuje tej struktury; value może być number (istniejąca kategoria) lub string (nowa)
interface Option {
  label: string;
  value: number | string;
}

const AddBook = () => {
  const [title, setTitle] = useState<string>("");
  const [yearRelease, setYearRelease] = useState<number>(new Date().getFullYear());
  const [language, setLanguage] = useState<string>("");
  const [jpg, setJpg] = useState<string>("");
  const [pageCount, setPageCount] = useState<number>(0);
  const [description, setDescription] = useState<string>("");

  const [categoryOptions, setCategoryOptions] = useState<Option[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<MultiValue<Option>>([]);

  const [authorOptions, setAuthorOptions] = useState<Option[]>([]);
  const [selectedAuthors, setSelectedAuthors] = useState<MultiValue<Option>>([]);

  const [totalCopies, setTotalCopies] = useState<number>(1);
  const [loading, setLoading] = useState<boolean>(false);

  // 1) Pobieramy kategorie
  useEffect(() => {
    (async () => {
      try {
        const res = await fetchWithRefresh("http://localhost:8080/categories");
        if (!res.ok) {
          console.error("Failed to load categories");
          return;
        }
        const data: { categoryId: number; categoryName: string }[] = await res.json();
        setCategoryOptions(
          data.map(c => ({
            label: c.categoryName,
            value: c.categoryId,
          }))
        );
      } catch (e) {
        console.error(e);
      }
    })();
  }, []);

  // 2) Pobieramy autorów
  useEffect(() => {
    (async () => {
      try {
        const res = await fetchWithRefresh("http://localhost:8080/authors/all");
        if (!res.ok) {
          console.error("Failed to load authors");
          return;
        }
        const data: { author_id: number; name: string; information: string }[] = await res.json();
        setAuthorOptions(
          data.map(a => ({
            label: a.name,
            value: a.author_id,
          }))
        );
      } catch (e) {
        console.error(e);
      }
    })();
  }, []);

  // 3) Obsługa wysyłki formularza
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    // Budujemy authors: [{ id: author_id }, ...]
    const authors: AuthorInput[] = selectedAuthors.map(opt => ({
      id: Number(opt.value),
    }));
    // Budujemy categories: albo id, albo name
    const categories: CategoryInput[] = selectedCategories.map(opt => {
      if (typeof opt.value === "number") {
        return { categoryId: opt.value };
      } else {
        return { categoryName: opt.label };
      }
    });

    const bookDto: BookDto = {
      title,
      yearRelease,
      language,
      jpg,
      pageCount,
      description,
      authors,
      categories,
      totalCopies,
      availableCopies: totalCopies,
    };

    console.log("📤 Wysyłany DTO:", bookDto);

    try {
      const res = await fetchWithRefresh("http://localhost:8080/booksł", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(bookDto),
      });
      console.log("🔄 Response status:", res.status);

      if (res.ok) {
        alert("Książka została dodana pomyślnie");
      } else {
        const text = await res.text();
        alert(`Błąd: ${text}`);
      }
    } catch (err) {
      console.error(err);
      alert("Wystąpił błąd podczas dodawania książki");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="wrapper-user">
      <div className="return-container">
        <h2>Dodaj nową książkę</h2>
        <form onSubmit={handleSubmit}>
          {/* Tytuł */}
          <div className="form-group">
            <label>Tytuł:</label>
            <input
              type="text"
              value={title}
              onChange={e => setTitle(e.target.value)}
              required
            />
          </div>

          {/* Rok wydania */}
          <div className="form-group">
            <label>Rok wydania:</label>
            <input
              type="number"
              value={yearRelease}
              onChange={e => setYearRelease(Number(e.target.value))}
              required
            />
          </div>

          {/* Język */}
          <div className="form-group">
            <label>Język:</label>
            <input
              type="text"
              value={language}
              onChange={e => setLanguage(e.target.value)}
              required
            />
          </div>

          {/* URL okładki */}
          <div className="form-group">
            <label>URL okładki (jpg):</label>
            <input
              type="text"
              value={jpg}
              onChange={e => setJpg(e.target.value)}
            />
          </div>

          {/* Liczba stron */}
          <div className="form-group">
            <label>Liczba stron:</label>
            <input
              type="number"
              value={pageCount}
              onChange={e => setPageCount(Number(e.target.value))}
              required
            />
          </div>

          {/* Opis */}
          <div className="form-group">
            <label>Opis:</label>
            <textarea
              value={description}
              onChange={e => setDescription(e.target.value)}
              rows={4}
              required
            />
          </div>

          {/* Autorzy */}
          <div className="form-group">
            <label>Autorzy:</label>
            <Select<Option, true>
              isMulti
              options={authorOptions}
              value={selectedAuthors}
              onChange={(opts) => setSelectedAuthors(opts)}
              placeholder="Wybierz autorów..."
            />
          </div>

          {/* Kategorie */}
          <div className="form-group">
            <label>Kategorie:</label>
            <CreatableSelect<Option, true>
            isMulti
            options={categoryOptions}
            value={selectedCategories}
            onChange={(opts) => setSelectedCategories(opts)}
            placeholder="Wybierz lub dodaj kategorie..."
            />
            </div>
          {/* Łączna liczba kopii */}
          <div className="form-group">
            <label>Łączna liczba kopii:</label>
            <input
              type="number"
              value={totalCopies}
              onChange={e => setTotalCopies(Number(e.target.value))}
              required
            />
          </div>

          <button type="submit" disabled={loading} className="lockAccount-button">
            {loading ? "Dodawanie..." : "Dodaj książkę"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddBook;