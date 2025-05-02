import {useUser} from "../context/UserContext.tsx";


const AdminUserPage = () =>{
    const { user, loading } = useUser();



    if (!user || loading) {
        return (
            <div className="main-container">
                <p className="error-message">Użytkownik niezalogowany</p>
            </div>
        );
    }



    return (
    <div>
        <p>{user?.username}</p>
    </div>

);


}; export default AdminUserPage;