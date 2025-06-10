import React from 'react';
import { useNavigate } from 'react-router-dom';
import BookForm from './BookForm';

const AddBook: React.FC = () => {
  const navigate = useNavigate();

  const handleSuccess = () => {
    navigate('/books/edit');
  };

  return <BookForm onSuccess={handleSuccess} />;
};

export default AddBook;
