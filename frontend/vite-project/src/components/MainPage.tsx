import "../css/mainPage.css";
import { useUser } from "../context/UserContext.tsx";
import RandomBookSlider from "./RandomBookSlider.tsx";

const MainPage = () => {
    const { user, loading,  } = useUser();


    if (!user || loading) {
        return (
            <div className="main-container">
                <p className="error-message">Użytkownik niezalogowany</p>
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
