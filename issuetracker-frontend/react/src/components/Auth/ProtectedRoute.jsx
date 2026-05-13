import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('authToken');

  // Ако НЯМА токен, го изхвърляме към логин страницата
  // Свойството "replace" изтрива опита за влизане от историята на браузъра, 
  // за да не може потребителят да се върне назад с бутона "Back"
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;