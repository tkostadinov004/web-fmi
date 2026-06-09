import React from 'react';
import { NavLink } from 'react-router-dom';
import '../Auth/AuthShared.css';

const CreateProjectPage = () => {
  return (
    <div className="auth-container">
      <div className="auth-form" style={{ textAlign: 'center' }}>
        <h2>Create Project Page</h2>
        <p style={{ marginBottom: '2rem', color: 'var(--text-dark)' }}>
          ⚙️ Страницата е в процес на разработка.
        </p>
        
        <NavLink 
          to="/home" 
          style={{ 
            color: 'var(--sage-green)', 
            textDecoration: 'none', 
            fontWeight: 'bold' 
          }}
        >
          &larr; Back to Home page
        </NavLink>
      </div>
    </div>
  );
};

export default CreateProjectPage;