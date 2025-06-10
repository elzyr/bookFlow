import { useUser } from "../context/UserContext.tsx";
import "../css/UserInfo.css";
import {useEffect, useState} from "react";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";

const UserInfoPage = () => {
    const { user, loading } = useUser();
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [debt, setDebt] = useState<number | null>(null);


    useEffect(() => {
        if (!user) return;

        const fetchDebt = async () => {
            try {
                const res = await fetchWithRefresh("http://localhost:8080/loans/userDebt", {
                    method: "GET"
                });
                if (res.ok) {
                    const value = await res.json();
                    setDebt(typeof value === "number" ? value : value.totalDebt);
                } else {
                    console.warn("Nie udało się pobrać informacji o zadłużeniu");
                }
            } catch (err) {
                console.error("Błąd podczas pobierania zadłużenia:", err);
            }
        };
        fetchDebt();
    }, [user]);

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
            const response = await fetchWithRefresh("http://localhost:8080/users/passwordChange", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    userName:  user.username,
                    oldPassword,
                    newPassword,
                })
            });

            if (response.ok) {
                alert("Password changed");
                window.location.reload();
            } else {
            const data = await response.text();
                alert(data);
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
                    {debt !== null && (
                        <p className="user-debt">
                            Zadłużenie: <strong style={{ color: debt > 0 ? "red" : "green" }}>
                            {debt > 0 ? `${debt.toFixed(2)} zł` : "Brak"}
                        </strong>
                        </p>
                    )}
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
