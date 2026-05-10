import { NavLink } from 'react-router-dom'
import './App.css'

function App() {
  return (
    <div className="app-container">
      <h1>Issue Tracker Project</h1>
    
      <button>
        <NavLink to="/register"> To Register page</NavLink>
      </button>
    </div>
  )
}

export default App