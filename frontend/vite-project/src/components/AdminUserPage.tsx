import {useUser} from "../context/UserContext.tsx";
import {useEffect, useState} from "react";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";
import {UserDto} from "../types/UserDto.tsx";
import "../css/AdminUserPage.css"

const AdminUserPage = () =>{
    const { user, loading } = useUser();
    const [ allUsersList , setUserList ] = useState<UserDto[]>([]);

    const fetchUser = () => {
         fetchWithRefresh(`http://localhost:8080/info/getAllUsers`, {
            method: "GET"
        })
            .then(res => {
                if (!res.ok) {
                    return;
                }
                return res.json();
            }).then(
                data => {
                    setUserList(data)
                    console.log(data)
                }
         )
    };

    useEffect(() => {
        fetchUser();
    }, []);

    if (!user || loading) {
        return (
            <div className="main-container">
                <p className="error-message">Użytkownik niezalogowany</p>
            </div>
        );
    }

    const handleStatusAccount = async (id: number, lock: string) => {
        const res = await fetchWithRefresh(`http://localhost:8080/info/changeAccountStatus?userId=${id}&status=${lock}`, {
            method: "PUT"
        })
        const text: string = await res.text();
        alert(text);
        fetchUser();
    }

    return (
        <div className="wrapper-user">
            <div className="return-container">
                <h2>Lista Użytkowników</h2>
                {!allUsersList || loading || allUsersList.length === 0 ? (
                    <p>Brak użytkowników do wyświetlenia</p>
                ) : (
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
                        {allUsersList.map((userList, index) => (
                            <tr key={userList.id ?? index}>
                                <td>{index + 1}</td>
                                <td>{userList.name}</td>
                                <td>{userList.email}</td>
                                <td>{userList.creationDate}</td>
                                <td>{userList.roles.join(",")}</td>
                                <td className={userList.active ? "status-active" : "status-inactive"}>
                                    {userList.active ? "Aktywne" : "Nieaktywne"}
                                </td>
                                <td>
                                    <button className="deleteAccount-button">
                                        Usuń
                                    </button>
                                    <button className="lockAccount-button"
                                            onClick={() => handleStatusAccount(userList.id, "lock")}>
                                        Zablokuj konto
                                    </button>
                                    <button className="unlockAccount-button" onClick={() => handleStatusAccount(userList.id,"unlock")}>
                                        Odblokuj konto
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );


};
export default AdminUserPage;