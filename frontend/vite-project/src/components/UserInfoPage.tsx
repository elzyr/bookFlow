import { useUser } from "../context/UserContext.tsx";
import "../css/UserInfo.css";
import { useState } from "react";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";

const UserInfoPage = () => {
    const { user, loading } = useUser();
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    if (!user || loading) {
        return (
            <div className="main-container">
                <p className="error-message">Użytkownik niezalogowany</p>
            </div>
        );
    }

    const creationDate = new Date(user.creationDate);
    const today = new Date();
    const timeDiff = today.getTime() - creationDate.getTime();
    const daysDiff = Math.floor(timeDiff / (1000 * 60 * 60 * 24));
    const formattedDate = creationDate.toLocaleDateString();

    const handleChangePassword = async (e: React.FormEvent) => {
        e.preventDefault();


        try {
            const response = await fetchWithRefresh("http://localhost:8080/info/passwordChange", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    userId: user.id,
                    oldPassword,
                    newPassword,
                })
            });

            const data = await response.text();

            if (!response.ok) {
                alert("Błąd: " + data);
            } else {
                alert("Sukces: " + data);
                window.location.reload();
            }

        } catch (err) {
            console.error("Błąd połączenia:", err);
        }
    };


    return (
        <div className="user-info-container">
            <div className="user-card">
                <div className="avatar">
                    {user.name?.charAt(0).toUpperCase() ?? "Un"}
                </div>
                <div className="user-details">
                    <h2>{user.name}</h2>
                    <p className="username">@{user.username}</p>
                    <p>Email: {user.email}</p>
                    <p>Data rejestracji: {formattedDate}</p>
                    <p className="user-since">
                        Jesteś użytkownikiem od <strong>{daysDiff}</strong> dni!
                    </p>
                </div>
            </div>

            <div className="change-password-card">
                <h3>Zmień hasło</h3>
                <form onSubmit={handleChangePassword}>
                    <input
                        type="password"
                        placeholder="Stare hasło"
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Nowe hasło"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Potwierdź nowe hasło"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                    <button type="submit" onClick={handleChangePassword}>Zmień hasło</button>
                </form>
            </div>
        </div>
    );
};

export default UserInfoPage;
