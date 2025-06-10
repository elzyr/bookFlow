import { Routes, Route, useLocation } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import MainPage from "./components/MainPage";
import Navbar from "./components/Navbar";
import BookPage from "./components/BookPage.tsx";
import BookInfo from "./components/BookInfo.tsx";
import LoanUserInfo from "./components/LoanUserInfo.tsx";
import BookStatus from "./components/BookStatus.tsx";
import AdminUserPage from "./components/AdminUserPage.tsx";
import UserInfoPage from "./components/UserInfoPage.tsx";
import RegisterForm from "./components/RegisterForm.tsx";
import AdminLoanRanks from "./components/AdminLoanRanks.tsx";
import AdminBookPage from "./components/AdminBookPage.tsx";
import AddBook from "./components/AddBook.tsx";
import EditBook from "./components/EditBook.tsx";
import AdminLoanPage from "./components/AdminLoanPage.tsx";
import LoanRemindersPanel from "./components/LoanRemindersPanel.tsx";
import AddAuthor from "./components/AddAuthor.tsx";

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
                <Route path="/addbook" element={<AddBook/>}></Route>
                <Route path="/adminbookpage" element={<AdminBookPage/>}></Route>
                <Route path="/books/edit/:id" element={<EditBook />} />
                <Route path="author/add" element={<AddAuthor />} />

                <Route path="/LoanUserInfo" element={<LoanUserInfo/>}></Route>

                <Route path="/adminloanpage" element={<AdminLoanPage/>}></Route>
                
                <Route path="/LoanUserInfo" element={<LoanUserInfo/>}></Route>
                <Route path="/register" element={<RegisterForm/>}></Route>
                <Route path="/bookStatus" element={<BookStatus/>}></Route>
                <Route path="/adminUserPage" element={<AdminUserPage/>}></Route>
                <Route path="/userInfo" element={<UserInfoPage/>}></Route>
                <Route path="/AdminRanks" element={<AdminLoanRanks/>}></Route>
                <Route path="/LoanRemindersPanel" element={<LoanRemindersPanel/>}></Route>
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
