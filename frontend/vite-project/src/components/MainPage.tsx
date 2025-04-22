import {useNavigate} from "react-router-dom";
import "../css/mainPage.css"
import { useUser } from "../context/UserContext.tsx";


const MainPage = () => {

    const { user, loading,setUser } = useUser();
    const navigate = useNavigate();

    const handleLogout = () =>{
        fetch("http://localhost:8080/info/logout",{
            method: "POST",
            credentials: "include"
        })
            .then(() =>{
                setUser(null);
                window.location.reload();
                navigate("/");
            })
            .catch(err =>{
                console.log(err);
            })
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
            <h2>Witaj, {user.name}!</h2>
            <p><strong>Nazwa użytkownika:</strong> {user.username}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Data utworzenia:</strong> {user.creationDate}</p>
            <p><strong>Role:</strong> {user.roles.join(", ")}</p>
        </div>
    );

};
export default MainPage;