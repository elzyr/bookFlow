import React, { useState, useEffect } from 'react';
import Select from 'react-select';
import CreatableSelect from 'react-select/creatable';
import { fetchWithRefresh } from '../../utils/fetchWithRefresh.tsx';
import '../../css/BookForm.css';

export type SelectOption = { value: string; label: string };

export interface BookFormData {
  title: string;
  authors: SelectOption[];
  yearRelease: number;
  language: string;
  jpg: string;
  pageCount: number;
  description: string;
  categories: SelectOption[];
  totalCopies: number;
}

type FormErrors = Partial<Record<keyof BookFormData, string>>;

interface BookFormProps {
  initialData?: BookFormData;
  bookId?: number;
  onSuccess: () => void;
}

const BookForm: React.FC<BookFormProps> = ({ initialData, bookId, onSuccess }) => {
  const [form, setForm] = useState<BookFormData>(
    initialData || {
      title: '',
      authors: [],
      yearRelease: new Date().getFullYear(),
      language: '',
      jpg: '',
      pageCount: 1,
      description: '',
      categories: [],
      totalCopies: 1,
    }
  );
  const [errors, setErrors] = useState<FormErrors>({});
  const isEdit = Boolean(initialData);

  const [authorOptions, setAuthorOptions] = useState<SelectOption[]>([]);
  const [categoryOptions, setCategoryOptions] = useState<SelectOption[]>([]);

  useEffect(() => {
    if (initialData) setForm(initialData);
  }, [initialData]);

  useEffect(() => {
    fetchWithRefresh('http://localhost:8080/authors', { credentials: 'include' })
      .then(res => res.json())
      .then((data: { author_id: number; name: string }[]) =>
        setAuthorOptions(data.map(a => ({ value: a.name, label: a.name })))
      );
    fetchWithRefresh('http://localhost:8080/categories', { credentials: 'include' })
      .then(res => res.json())
      .then((data: { categoryId: number; categoryName: string }[]) =>
        setCategoryOptions(data.map(c => ({ value: c.categoryName, label: c.categoryName })))
      );
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    setForm(prev => ({ ...prev, [name]: type === 'number' ? +value : value }));
  };

  const handleSelectChange = (
    value: SelectOption[] | null,
    _meta: any,
    field: 'authors' | 'categories'
  ) => setForm(prev => ({ ...prev, [field]: value || [] }));

  const validate = (): boolean => {
    const errs: FormErrors = {};
    if (!form.title.trim()) errs.title = 'Tytuł nie może być pusty';
    if (form.authors.length === 0) errs.authors = 'Wybierz co najmniej jednego autora';
    if (form.yearRelease < 0) errs.yearRelease = 'Rok wydania musi być liczbą naturalną lub zerem';
    if (!form.language.trim()) errs.language = 'Język nie może być pusty';
    if (!form.jpg.trim()) errs.jpg = 'Adres okładki nie może być pusty';
    if (form.pageCount <= 0) errs.pageCount = 'Liczba stron musi być większa od zera';
    if (form.description.length > 7000) errs.description = 'Opis nie może przekraczać 7000 znaków';
    if (form.categories.length === 0) errs.categories = 'Wybierz co najmniej jedną kategorię';
    if (form.totalCopies <= 0) errs.totalCopies = 'Liczba kopii musi być większa od zera';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      title: form.title,
      authorNames: form.authors.map(a => a.value),
      yearRelease: form.yearRelease,
      language: form.language,
      jpg: form.jpg,
      pageCount: form.pageCount,
      description: form.description,
      categoryNames: form.categories.map(c => c.value),
      totalCopies: form.totalCopies,
    };

    const url = bookId ? `http://localhost:8080/books/${bookId}` : 'http://localhost:8080/books';
    const method = bookId ? 'PUT' : 'POST';

    console.log('→ Wysyłam:', method, url, payload);
    try {
      const res = await fetchWithRefresh(url, {
        method,
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      const text = await res.text();
      console.log('← Odpowiedź:', res.status, text);
      if (!res.ok) { alert(`Błąd ${res.status}: ${text}`); return; }
      onSuccess();
    } catch (err) {
      console.error('Fetch error:', err);
      alert(`Błąd sieci przy zapisie: ${err}`);
    }
  };

  return (
    <div className="wrapper-user">
      <div className="return-container">
        <h2>{isEdit ? 'Edytuj książkę' : 'Dodaj nową książkę'}</h2>
        <form onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label htmlFor="title">Tytuł</label>
            <input id="title" name="title" type="text" value={form.title} onChange={handleInputChange} />
            {errors.title && <span className="error-msg">{errors.title}</span>}
          </div>
          <div className="form-group">
            <label>Autorzy</label>
            <Select isMulti options={authorOptions} value={form.authors}
              onChange={(val, meta) => handleSelectChange(val as SelectOption[], meta, 'authors')} />
            {errors.authors && <span className="error-msg">{errors.authors}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="yearRelease">Rok wydania</label>
            <input id="yearRelease" name="yearRelease" type="number" value={form.yearRelease} onChange={handleInputChange} />
            {errors.yearRelease && <span className="error-msg">{errors.yearRelease}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="language">Język</label>
            <input id="language" name="language" type="text" value={form.language} onChange={handleInputChange} />
            {errors.language && <span className="error-msg">{errors.language}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="jpg">URL okładki</label>
            <input id="jpg" name="jpg" type="text" value={form.jpg} onChange={handleInputChange} />
            {errors.jpg && <span className="error-msg">{errors.jpg}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="pageCount">Liczba stron</label>
            <input id="pageCount" name="pageCount" type="number" min={1} value={form.pageCount} onChange={handleInputChange} />
            {errors.pageCount && <span className="error-msg">{errors.pageCount}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="description">Opis</label>
            <textarea id="description" name="description" rows={4} value={form.description} onChange={handleInputChange} />
            {errors.description && <span className="error-msg">{errors.description}</span>}
          </div>
          <div className="form-group">
            <label>Kategorie</label>
            <CreatableSelect isMulti options={categoryOptions} value={form.categories}
              onChange={(val, meta) => handleSelectChange(val as SelectOption[], meta, 'categories')} />
            {errors.categories && <span className="error-msg">{errors.categories}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="totalCopies">Całkowita liczba kopii</label>
            <input id="totalCopies" name="totalCopies" type="number" min={1} value={form.totalCopies} onChange={handleInputChange} />
            {errors.totalCopies && <span className="error-msg">{errors.totalCopies}</span>}
          </div>
          <button type="submit">{isEdit ? 'Aktualizuj' : 'Dodaj'}</button>
        </form>
      </div>
    </div>
  );
};

export default BookForm;
