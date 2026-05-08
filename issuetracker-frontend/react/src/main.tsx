import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './index.css'

import App from './App.tsx'
import Register from './Register.tsx'

//From documentation for react-routing https://reactrouter.com/start/declarative/routing
//I will be using the Declarative type of routing, because it's the base one

const root = document.getElementById('root');

createRoot(root!).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />}></Route>
        <Route path="/try" element={<Register />}></Route>
        
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)


