import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import 'bootstrap/dist/css/bootstrap.min.css';
import {UserProvider} from "./context/UserContext.tsx";


createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <UserProvider>
            <App />
      </UserProvider>
  </StrictMode>,
)
