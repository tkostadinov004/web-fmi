import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  // 1. Проверяваме дали съществува токен в браузъра
  const token = localStorage.getItem('authToken');

  // 2. Ако НЯМА токен, го изхвърляме към логин страницата
  // Свойството "replace" изтрива опита за влизане от историята на браузъра, 
  // за да не може потребителят да се върне назад с бутона "Back"
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // 3. Ако ИМА токен, показваме това, което сме обвили (children)
  return children;
};

export default ProtectedRoute;