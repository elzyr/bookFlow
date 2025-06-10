import React, { useState } from 'react';
import { fetchWithRefresh } from '../utils/fetchWithRefresh.tsx';
import '../css/AddAuthor.css';
import Notification from './Notification';

const AddAuthor: React.FC = () => {
  const [formData, setFormData] = useState({ name: '', information: '' });
  const [errors, setErrors] = useState<{ name?: string; information?: string }>({});
  const [notification, setNotification] = useState<{
    message: string;
    type: 'success' | 'error';
  } | null>(null);

  const validate = () => {
    const errs: typeof errors = {};
    if (!formData.name.trim()) errs.name = 'Imię autora nie może być puste';
    if (!formData.information.trim()) errs.information = 'Informacje nie mogą być puste';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      const res = await fetchWithRefresh('http://localhost:8080/authors/add', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: formData.name,
          information: formData.information,
        }),
      });
      const text = await res.text();
      if (!res.ok) {
        setNotification({ message: `${text}`, type: 'error' });
        return;
      }
      setFormData({ name: '', information: '' });
      setNotification({ message: 'Autor został dodany pomyślnie', type: 'success' });
    } catch (err) {
      console.error('Submit author error', err);
      setNotification({ message: 'Błąd sieci przy zapisie autora.', type: 'error' });
    }
  };

  return (
    <div className="wrapper-user">
      <div className="return-container">
        {notification && (
          <Notification
            message={notification.message}
            type={notification.type}
            onClose={() => setNotification(null)}
          />
        )}
        <h2>Dodaj nowego autora</h2>
        <form onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label htmlFor="name">Imię i nazwisko</label>
            <input
              id="name"
              name="name"
              type="text"
              value={formData.name}
              onChange={handleChange}
            />
            {errors.name && <span className="error-msg">{errors.name}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="information">Informacje</label>
            <textarea
              id="information"
              name="information"
              rows={4}
              value={formData.information}
              onChange={handleChange}
            />
            {errors.information && <span className="error-msg">{errors.information}</span>}
          </div>

          <button type="submit">Dodaj autora</button>
        </form>
      </div>
    </div>
  );
};

export default AddAuthor;
