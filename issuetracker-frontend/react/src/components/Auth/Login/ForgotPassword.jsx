import React from 'react';
import { Link } from 'react-router-dom';
import '../AuthShared.css';

const ForgotPassword = () => {
  return (
    <div className="auth-container">
      <div className="auth-form" style={{ textAlign: 'center' }}>
        <h2>Forgot Password</h2>
        <p style={{ marginBottom: '2rem', color: 'var(--text-dark)' }}>
          ⚙️ Страницата е в процес на разработка.
        </p>
        
        <Link 
          to="/login" 
          style={{ 
            color: 'var(--sage-green)', 
            textDecoration: 'none', 
            fontWeight: 'bold' 
          }}
        >
          &larr; Back to Login
        </Link>
      </div>
    </div>
  );
};

export default ForgotPassword;