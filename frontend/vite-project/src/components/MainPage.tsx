import "../css/mainPage.css";
import { useUser } from "../context/UserContext.tsx";
import RandomBookSlider from "./book/RandomBookSlider.tsx";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const MainPage = () => {
    const { user, loading } = useUser();
    const navigate = useNavigate();

    useEffect(() => {
        if (!user && !loading) {
            const timeout = setTimeout(() => {
                navigate("/");
            }, 5000);

            return () => clearTimeout(timeout);
        }
    }, [user, loading, navigate]);

    if (!user || loading) {
        return (
            <div className="main-container">
                <p className="error-message">Użytkownik niezalogowany — nastąpi przekierowanie...</p>
            </div>
        );
    }

    return (
        <div className="main-container">
            <div className="welcome-section">
                <h1>Witaj, {user.name}!</h1>
            </div>
            <RandomBookSlider />
        </div>
    );
};

export default MainPage;
