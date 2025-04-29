import { useNavigate } from "react-router-dom";
import "../css/mainPage.css";
import { useUser } from "../context/UserContext.tsx";
import RandomBookSlider from "./RandomBookSlider.tsx";

const MainPage = () => {
    const { user, loading, setUser } = useUser();
    const navigate = useNavigate();

    const handleLogout = () => {
        fetch("http://localhost:8080/info/logout", {
            method: "POST",
            credentials: "include"
        })
            .then(() => {
                setUser(null);
                window.location.reload();
                navigate("/");
            })
            .catch(err => {
                console.log(err);
            });
    };

    if (!user || loading) {
        return (
            <div className="main-container">
                <p className="error-message">Użytkownik niezalogowany</p>
                <button className="logout-button" onClick={handleLogout}>
                    Wyloguj się
                </button>
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
