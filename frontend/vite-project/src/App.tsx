import { Routes, Route, useLocation } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import MainPage from "./components/MainPage";
import Navbar from "./components/Navbar";
import BookPage from "./components/BookPage.tsx";
import BookInfo from "./components/BookInfo.tsx";
import LoanUserInfo from "./components/LoanUserInfo.tsx";
import BookStatus from "./components/BookStatus.tsx";
import UserInfoPage from "./components/UserInfoPage.tsx";

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
                <Route path="/BookInfo/:id" element={<BookInfo/>}></Route>
                <Route path="/LoanUserInfo" element={<LoanUserInfo/>}></Route>
                <Route path="/bookStatus" element={<BookStatus/>}></Route>
                <Route path="/userInfo" element={<UserInfoPage/>}></Route>
            </Routes>
        </>
    );
};

function App() {
    return (
            <AppLayout />
    );
}

export default App;
