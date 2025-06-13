import { useUser } from "../context/UserContext.tsx";
import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../utils/fetchWithRefresh.tsx";
import { UserDto } from "../types/UserDto.tsx";
import "../css/AdminUserPage.css";
import Notification from "./Notification.tsx";

const AdminUserPage = () => {
    const { user, loading } = useUser();
    const [allUsersList, setUserList] = useState<UserDto[]>([]);
    const [notification, setNotification] = useState<{ message: string; type?: "success" | "error" } | null>(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const fetchUser = () => {
        fetchWithRefresh(`http://localhost:8080/users?page=${page}&size=5`, {
            method: "GET"
        })
            .then(res => {
                if (!res.ok) return;
                return res.json();
            })
            .then(data => {
                if (data) {
                    setUserList(data.content);
                    setTotalPages(data.totalPages);
                }
            });
    };

    useEffect(() => {
        fetchUser();
    }, [page]);

    if (!user || loading) {
        return (
            <div className="main-container">
                <p className="error-message">Użytkownik niezalogowany</p>
            </div>
        );
    }

    const handleDeleteAccount = async (username: string) => {
        try {
            const res = await fetchWithRefresh(`http://localhost:8080/users/${encodeURIComponent(username)}`, {
                method: "DELETE"
            });
            if (!res.ok) {
                const errorText = await res.text();
                alert(`Błąd: ${errorText}`);
            } else {
                setNotification({ message: "Użytkownik został usunięty", type: "success" });
                fetchUser();
            }
        } catch (e) {
            console.error(e);
            setNotification({ message: "Błąd przy usuwaniu", type: "error" });
        }
    };

    const handleStatusAccount = async (username: string, status: boolean) => {
        try {
            const res = await fetchWithRefresh(
                `http://localhost:8080/users/${encodeURIComponent(username)}/status?status=${status}`,
                { method: "PUT" }
            );

            if (!res.ok) {
                const errorText = await res.text();
                alert(`Błąd: ${errorText}`);
            } else {
                setNotification({
                    message: `Status konta użytkownika ${username} został zmieniony na ${status ? "aktywny" : "zablokowany"}`,
                    type: "success"
                });
                fetchUser();
            }
        } catch (e) {
            console.error(e);
            setNotification({ message: "Błąd zmiany statusu", type: "error" });
        }
    };

    return (
        <div className="wrapper-user">
            <div className="return-container-user">
                <h2>Lista Użytkowników</h2>
                {!allUsersList || allUsersList.length === 0 ? (
                    <p>Brak użytkowników do wyświetlenia</p>
                ) : (
                    <>
                        <table className="user-table">
                            <thead>
                                <tr>
                                    <th>Id</th>
                                    <th>Dane osobowe</th>
                                    <th>Email</th>
                                    <th>Data utworzenia</th>
                                    <th>Role</th>
                                    <th>Aktywność</th>
                                    <th>Akcje</th>
                                </tr>
                            </thead>
                            <tbody>
                                {allUsersList.map((u, index) => (
                                    <tr key={u.id ?? index}>
                                        <td>{index + 1 + page * 5}</td>
                                        <td>{u.name}</td>
                                        <td>{u.email}</td>
                                        <td>{u.creationDate}</td>
                                        <td>{u.roles.join(", ")}</td>
                                        <td className={u.active ? "status-active" : "status-inactive"}>
                                            {u.active ? "Aktywne" : "Nieaktywne"}
                                        </td>
                                        <td>
                                            {u.username !== user.username ? (
                                                <>
                                                    <button
                                                        className="deleteAccount-button"
                                                        onClick={() => handleDeleteAccount(u.username)}
                                                    >
                                                        Usuń
                                                    </button>
                                                    <button
                                                        className="lockAccount-button"
                                                        onClick={() => handleStatusAccount(u.username, false)}
                                                    >
                                                        Zablokuj konto
                                                    </button>
                                                    <button
                                                        className="unlockAccount-button"
                                                        onClick={() => handleStatusAccount(u.username, true)}
                                                    >
                                                        Odblokuj konto
                                                    </button>
                                                </>
                                            ) : (
                                                <em>Brak akcji dla Twojego konta</em>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>

                        <div className="pagination-controls">
                            <button onClick={() => setPage(p => Math.max(p - 1, 0))} disabled={page === 0}>
                                Poprzednia
                            </button>
                            <span>Strona {page + 1} z {totalPages}</span>
                            <button onClick={() => setPage(p => Math.min(p + 1, totalPages - 1))} disabled={page + 1 >= totalPages}>
                                Następna
                            </button>
                        </div>
                    </>
                )}
                {notification && (
                    <Notification
                        message={notification.message}
                        type={notification.type}
                        onClose={() => setNotification(null)}
                    />
                )}
            </div>
        </div>
    );
};

export default AdminUserPage;
