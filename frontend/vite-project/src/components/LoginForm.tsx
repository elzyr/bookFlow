import React, {useState} from "react";
import { useNavigate } from "react-router-dom";


const LoginForm  = () => {
const [username , setUsername] = useState("");
const [password , setPassword] = useState("");
const navigate = useNavigate();

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
        navigate("/mainPage");
    }
    else{
        console.log("Bledne dane");
        // TODO: Add Notification for bad credentials
    }
    };

    return (
        <div style={{ maxWidth: "400px", margin: "100px auto" }}>
            <h2>Logowanie</h2>
            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: "10px" }}>
                    <input
                        type="text"
                        placeholder="Nazwa użytkownika"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        style={{ width: "100%", padding: "8px" }}
                    />
                </div>
                <div style={{ marginBottom: "10px" }}>
                    <input
                        type="password"
                        placeholder="Hasło"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        style={{ width: "100%", padding: "8px" }}
                    />
                </div>
                <button type="submit" style={{ padding: "10px 20px" }}>
                    Zaloguj się
                </button>
            </form>
        </div>
    );
};
export default LoginForm;