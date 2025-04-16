import { BrowserRouter as Router, Routes, Route, useLocation } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import MainPage from "./components/MainPage";
import Navbar from "./components/Navbar";
import BookPage from "./components/BookPage.tsx";

const AppLayout = () => {
    const location = useLocation();
    const hideNavbarPaths = ["/", "/register"];

    const shouldHideNavbar = hideNavbarPaths.includes(location.pathname);

    return (
        <>
            {!shouldHideNavbar && <Navbar />}
            <Routes>
                <Route path="/" element={<LoginForm />} />
                <Route path="/mainPage" element={<MainPage />} />
                <Route path="/BookPage" element={<BookPage />}></Route>
            </Routes>
        </>
    );
};

function App() {
    return (
        <Router>
            <AppLayout />
        </Router>
    );
}

export default App;
