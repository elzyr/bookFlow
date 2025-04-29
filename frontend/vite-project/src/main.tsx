import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import 'bootstrap/dist/css/bootstrap.min.css';
import {UserProvider} from "./context/UserContext.tsx";
import {BrowserRouter} from "react-router-dom";
import React from 'react';


createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
        <BrowserRouter>
            <UserProvider>
                <App />
            </UserProvider>
        </BrowserRouter>
    </React.StrictMode>
);

