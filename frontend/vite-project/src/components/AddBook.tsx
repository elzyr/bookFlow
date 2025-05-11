import React from 'react';
import { useNavigate } from 'react-router-dom';
import BookForm from './BookForm';

const AddBook: React.FC = () => {
  const navigate = useNavigate();

  const handleSuccess = () => {
    // Po udanym dodaniu przekieruj na listę książek
    navigate('/books');
  };

  return <BookForm onSuccess={handleSuccess} />;
};

export default AddBook;
