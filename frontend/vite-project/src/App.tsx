import { Routes, Route, useLocation } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import MainPage from "./components/MainPage";
import Navbar from "./components/Navbar";
import BookPage from "./components/book/BookPage.tsx";
import BookDetails from "./components/book/BookDetails.tsx";
import LoanHistory from "./components/loan/LoanHistory.tsx";
import UserLoans from "./components/loan/UserLoans.tsx";
import AdminUserPage from "./components/AdminUserPage.tsx";
import UserInfoPage from "./components/UserInfoPage.tsx";
import RegisterForm from "./components/RegisterForm.tsx";
import AdminLoanRanks from "./components/loan/AdminLoanRanks.tsx";
import AdminBookList from "./components/book/AdminBookList.tsx";
import AddBook from "./components/book/AddBook.tsx";
import EditBook from "./components/book/EditBook.tsx";
import AdminLoanPage from "./components/loan/AdminLoanPage.tsx";
import LoanRemindersPanel from "./components/loan/LoanRemindersPanel.tsx";
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
                <Route path="/register" element={<RegisterForm/>} />

                <Route path="/main" element={<MainPage />} />

                <Route path="/books/info" element={<BookPage />} />
                <Route path="/books/info/:id" element={<BookDetails/>} />
                <Route path="/books/add" element={<AddBook/>} />
                <Route path="/books/edit" element={<AdminBookList/>} />
                <Route path="/books/edit/:id" element={<EditBook />} />

                <Route path="/author/add" element={<AddAuthor />} />

                
                <Route path="/loans" element={<UserLoans/>} />
                <Route path="/loans/history" element={<LoanHistory/>} />
                <Route path="/loans/verify" element={<AdminLoanPage/>} />
                <Route path="/loans/reminder" element={<LoanRemindersPanel/>} />

                <Route path="/users" element={<AdminUserPage/>} />
                <Route path="/profile" element={<UserInfoPage/>} />
                <Route path="/stats" element={<AdminLoanRanks/>} />
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
