import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

import Login from './components/Auth/Login/Login';
import Register from './components/Auth/Register/Register';
import ProtectedRoute from './components/Auth/ProtectedRoute';
import Dashboard from './components/Dashboard/Dashboard';

import './App.css';

function App() {
  return (
    <div className="App">
      {/* Тук е само списъкът с пътищата */}
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } 
        />

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </div>
  );
}

export default App;