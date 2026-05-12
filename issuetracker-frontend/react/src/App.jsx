import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

import Login from './components/Auth/Login/Login';
import ForgotPassword from './components/Auth/Login/ForgotPassword';
import Register from './components/Auth/Register/Register';
import ProtectedRoute from './components/Auth/ProtectedRoute';
import Dashboard from './components/Dashboard/Dashboard';

import './App.css';
import TicketModal from './components/Tickets/TicketModal/TicketModal';

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/ticket/:id" 
          element={
            <ProtectedRoute>
              <TicketModal />
            </ProtectedRoute>
          } 
        />

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </div>
  );
}

export default App;