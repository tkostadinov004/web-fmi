import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import './index.css'


import "bootstrap/dist/css/bootstrap.min.css";

import App from './App.jsx'

//From documentation for react-routing https://reactrouter.com/start/declarative/routing
//I will be using the Declarative type of routing, because it's the base one

const root = document.getElementById('root');

createRoot(root).render(
  <StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>
)


