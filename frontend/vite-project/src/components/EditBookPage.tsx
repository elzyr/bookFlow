import { useEffect, useState, useRef, FormEvent } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Select from "react-select";
import CreatableSelect from "react-select/creatable";
import { fetchWithRefresh } from "../utils/fetchWithRefresh.tsx";

import { BookDto } from "../types/BookDto.ts";
import { UpdateBookDto } from "../types/UpdateBookDto.ts";
import { CategoryInputDto } from "../types/CategoryInputDto.ts";
import { Author } from "../types/Author.ts";

import "../css/EditBookPage.css";

type CategoryOption = { value: number; label: string; __isNew__?: boolean };
type AuthorOption   = { value: number; label: string };

export default function EditBookPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [book, setBook] = useState<BookDto | null>(null);
  const [allAuthors, setAllAuthors]     = useState<Author[]>([]);
  const [allCategories, setAllCategories] = useState<CategoryInputDto[]>([]);
  const [authorOptions, setAuthorOptions]   = useState<AuthorOption[]>([]);
  const [categoryOptions, setCategoryOptions] = useState<CategoryOption[]>([]);

  const [selectedAuthorOptions, setSelectedAuthorOptions]   = useState<AuthorOption[]>([]);
  const [selectedCategoryOptions, setSelectedCategoryOptions] = useState<CategoryOption[]>([]);

  const [title, setTitle]       = useState("");
  const [year, setYear]         = useState<number>(new Date().getFullYear());
  const [language, setLanguage] = useState("");
  const [pageCount, setPageCount]         = useState(0);
  const [totalCopies, setTotalCopies]     = useState(0);
  const [availableCopies, setAvailableCopies] = useState(0);
  const [description, setDescription]     = useState("");

  const [errors, setErrors] = useState<{[key: string]: string}>({});

  const originalTotalRef     = useRef<number>(0);
  const originalAvailableRef = useRef<number>(0);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving]   = useState(false);

  useEffect(() => {
    if (!id) { setLoading(false); return; }
    (async () => {
      try {
        const resBook = await fetchWithRefresh(`http://localhost:8080/books/${id}`, { method: "GET" });
        if (!resBook.ok) throw new Error(`Book fetch failed ${resBook.status}`);
        const dto = (await resBook.json()) as BookDto;
        setBook(dto);
        setTitle(dto.title);
        setYear(dto.yearRelease);
        setLanguage(dto.language);
        setPageCount(dto.pageCount);
        setTotalCopies(dto.totalCopies);
        setAvailableCopies(dto.availableCopies);
        setDescription(dto.description);

        originalTotalRef.current     = dto.totalCopies;
        originalAvailableRef.current = dto.availableCopies;

        setSelectedAuthorOptions(dto.authors.map(a => ({ value: a.author_id, label: a.name })));
        setSelectedCategoryOptions(dto.categories.map(c => ({ value: c.categoryId, label: c.categoryName })));

        const [resAuth, resCat] = await Promise.all([
          fetchWithRefresh(`http://localhost:8080/authors/all`, { method: "GET" }),
          fetchWithRefresh(`http://localhost:8080/categories`, { method: "GET" }),
        ]);
        if (!resAuth.ok) throw new Error(`Authors fetch failed ${resAuth.status}`);
        if (!resCat.ok) throw new Error(`Categories fetch failed ${resCat.status}`);

        const auths = (await resAuth.json()) as Author[];
        const cats  = (await resCat.json()) as CategoryInputDto[];

        setAllAuthors(auths);
        setAuthorOptions(auths.map(a => ({ value: a.author_id, label: a.name })));

        setAllCategories(cats);
        setCategoryOptions(cats.map(c => ({ value: c.categoryId, label: c.categoryName })));
      } catch (err) {
        console.error(err);
        alert("Błąd ładowania danych");
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  useEffect(() => {
    const delta = totalCopies - originalTotalRef.current;
    const newAvailable = originalAvailableRef.current + delta;
    setAvailableCopies(newAvailable >= 0 ? newAvailable : 0);
  }, [totalCopies]);

  if (loading) return <p>Ładowanie…</p>;
  if (!book)   return <p>Nie znaleziono książki {id}</p>;

  const validate = () => {
    const errs: {[key: string]: string} = {};
    if (!title.trim()) errs.title = "Tytuł jest wymagany";
    if (selectedAuthorOptions.length === 0) errs.authors = "Wybierz przynajmniej jednego autora";
    if (selectedCategoryOptions.length === 0) errs.categories = "Wybierz przynajmniej jedną kategorię";
    if (year <= 0) errs.year = "Rok wydania musi być większy od zera";
    if (!language.trim()) errs.language = "Język jest wymagany";
    if (pageCount <= 0) errs.pageCount = "Liczba stron musi być większa niż zero";
    if (totalCopies <= 0) errs.totalCopies = "Całkowita liczba kopii musi być większa niż zero";
    if (!description.trim()) errs.description = "Opis jest wymagany";
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    setSaving(true);

    const payload: UpdateBookDto = {
      title,
      yearRelease: year,
      language,
      pageCount,
      totalCopies,
      availableCopies,
      description,
      authors: selectedAuthorOptions.map(o => ({ author_id: o.value })),
      categories: selectedCategoryOptions.map(o => ({
        categoryId: o.__isNew__ ? null! : o.value,
        categoryName: o.label,
      })),
    };

    try {
      const res = await fetchWithRefresh(`http://localhost:8080/books/${id}`, {
        method:  "PUT",
        headers: { "Content-Type": "application/json" },
        body:    JSON.stringify(payload),
      });
      if (!res.ok) {
        const txt = await res.text();
        alert(`Błąd: ${txt}`);
      } else {
        alert("Zapisano zmiany");
        navigate("/adminbookpage");
      }
    } catch {
      alert("Błąd sieci");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="wrapper-edit-book">
      <div className="container-edit-book">
        <h2>Edycja książki <b>{book.title}</b></h2>
        <form className="edit-book-form" onSubmit={handleSubmit} noValidate>

          <label>
            Tytuł:
            <input value={title} onChange={e => setTitle(e.target.value)} required />
            {errors.title && <span className="error">{errors.title}</span>}
          </label>

          <label>
            Autorzy:
            <Select
              isMulti
              options={authorOptions}
              value={selectedAuthorOptions}
              onChange={(v) => setSelectedAuthorOptions(v as AuthorOption[])}
            />
            {errors.authors && <span className="error">{errors.authors}</span>}
          </label>

          <label>
            Kategorie:
            <CreatableSelect
              isMulti
              options={categoryOptions}
              value={selectedCategoryOptions}
              onChange={(v) => setSelectedCategoryOptions(v as CategoryOption[])}
            />
            {errors.categories && <span className="error">{errors.categories}</span>}
          </label>

          <label>
            Rok wydania:
            <input
              type="number"
              value={year}
              onChange={e => setYear(+e.target.value)}
              required
            />
            {errors.year && <span className="error">{errors.year}</span>}
          </label>

          <label>
            Język:
            <input value={language} onChange={e => setLanguage(e.target.value)} required />
            {errors.language && <span className="error">{errors.language}</span>}
          </label>

          <label>
            Liczba stron:
            <input
              type="number"
              value={pageCount}
              onChange={e => setPageCount(+e.target.value)}
              required
            />
            {errors.pageCount && <span className="error">{errors.pageCount}</span>}
          </label>

          <label>
            Całkowita liczba kopii:
            <input
              type="number"
              value={totalCopies}
              onChange={e => setTotalCopies(+e.target.value)}
              required
            />
            {errors.totalCopies && <span className="error">{errors.totalCopies}</span>}
          </label>

          <p className="available-display">
            Dostępne kopie: <strong>{availableCopies}</strong>
          </p>

          <label>
            Opis:
            <textarea
              rows={5}
              value={description}
              onChange={e => setDescription(e.target.value)}
            />
            {errors.description && <span className="error">{errors.description}</span>}
          </label>

          <div className="buttons-row">
            <button type="submit" disabled={saving}>
              {saving ? "Zapisywanie…" : "Zapisz zmiany"}
            </button>
            <button
              type="button"
              className="cancel-button"
              onClick={() => navigate("/adminbookpage")}
            >
              Anuluj
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
