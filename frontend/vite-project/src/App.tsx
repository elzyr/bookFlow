import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import MainPage from "./components/MainPage.tsx";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<LoginForm />} />
                <Route path="/mainPage" element={<MainPage />} />
            </Routes>
        </Router>
    );
}

export default App;
