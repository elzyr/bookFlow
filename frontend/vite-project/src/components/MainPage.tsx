import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

type UserDto = {
    username: string;
    email: string;
    name: string;
    creationDate: string;
    roles: string[];
};

const MainPage = () => {

    const [user, setUser] = useState<UserDto | null>(null);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const handleLogout = () =>{
        fetch("http://localhost:8080/info/logout",{
            method: "POST",
            credentials: "include"
        })
            .then(() =>{
                setUser(null);
                setError(null);
                navigate("/");
            })
            .catch(err =>{
                console.log(err);
            })
    };

    useEffect(() => {
        fetch("http://localhost:8080/info/me", {
            method: "GET",
            credentials: "include"
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error("Unauthorized or error: " + res.status);
                }
                return res.json();
            })
            .then(userData => {
                console.log(userData);
                setUser(userData);
            })
            .catch(err => setError(err));
    }, []);

    if (!user || error) {
        console.log("User not logged");
        return <div>User not logged
            <button
            onClick={handleLogout}
            style={{marginTop: "20px", padding: "10px 20px"}}
        >
            Wyloguj się
        </button></div>
    }

    return (
        <div style={{padding: "20px"}}>
            <h2>Witaj, {user.name}!</h2>
            <p><strong>Nazwa użytkownika:</strong> {user.username}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Data utworzenia:</strong> {user.creationDate}</p>
            <p><strong>Role:</strong> {user.roles.join(", ")}</p>
            <button
                onClick={handleLogout}
                style={{marginTop: "20px", padding: "10px 20px"}}
            >
                Wyloguj się
            </button>
        </div>
    );

};
export default MainPage;