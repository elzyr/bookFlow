import React, {useState} from "react";
import { useNavigate } from "react-router-dom";
import '../css/LoginForm.css';
import {useUser} from "../context/UserContext.tsx";
import Notification from "./Notification.tsx";


const LoginForm  = () => {
const [username , setUsername] = useState("");
const [password , setPassword] = useState("");
const navigate = useNavigate();
const {refreshUser} = useUser();
const [notification, setNotification] = useState<{ message: string; type?: "success" | "error" } | null>(null);

const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    console.log("Logowanie: "+ username+ " haslo "+password);

    const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
            username,
            password,
        }),
    });
    if(response.ok){
        refreshUser();
        navigate("/mainPage");
    }
    else{
        console.log("Bledne dane");
        setNotification({ message: "Sprawdź poprawność loginu lub hasła", type: "error" });
    }
    };

    return (
        <div className="login-wrapper">
            <div className="login-container">
                <h2>Logowanie</h2>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Nazwa użytkownika"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Hasło"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <div className="login-options">
                        <a href="#">Zapomniałeś hasła?</a>
                    </div>
                    <button type="submit">Zaloguj się</button>
                </form>
            </div>
            <div className="register-panel">
                <h2>Witaj!</h2>
                <p>Nie masz jeszcze konta?</p>
                <button className="register-button" onClick={() => navigate("/register")}>
                    Zarejestruj się
                </button>
            </div>
            {notification && (
                <Notification
                    message={notification.message}
                    type={notification.type}
                    onClose={() => setNotification(null)}
                />
            )}
        </div>
    );

};
export default LoginForm;