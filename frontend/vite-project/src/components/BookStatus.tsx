import {useEffect, useState} from "react";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";
import {useUser} from "../context/UserContext.tsx";
import '../css/BookStatus.css';


interface ReturnBook{
    id : number | undefined;
    title : string | undefined;
    returnDate: Date | undefined;
    extendedTime : boolean | undefined;
}

const BookStatus = () =>{
    const [bookNotReturned, setBookNotReturnedList] = useState<ReturnBook[]>();
    const {user,loading} = useUser();

    useEffect(() => {
        if(loading || !user)return;
        fetchWithRefresh(`http://localhost:8080/loan/historyLoanActive?userId=${user.id}`,{
            method: "GET",
            credentials: "include"
        })
            .then(res => {
                if(!res.ok){
                    throw new Error("Blad wczytywania! error yti");
                }
                return res.json();
            })
            .then(data => setBookNotReturnedList(data));
    }, [user]);




    return (
        <div className="return-container">
            <h2>Książki do zwrotu</h2>
            {!bookNotReturned || loading || bookNotReturned.length === 0 ? (
                <p>Brak książek do zwrotu.</p>
            ) : (
                <table className="return-table">
                    <thead>
                    <tr>
                        <th></th>
                        <th>Tytuł</th>
                        <th>Data zwrotu</th>
                        <th>Przedłużone</th>
                        <th>Akcje</th>
                    </tr>
                    </thead>
                    <tbody>
                    {bookNotReturned.map((book, index) => (
                        <tr key={book.id ?? index}>
                            <td>{index + 1}</td>
                            <td>{book.title}</td>
                            <td>{book.returnDate ? new Date(book.returnDate).toLocaleDateString() : "brak daty"}</td>
                            <td>{book.extendedTime ? "Tak" : "Nie"}</td>
                            <td>
                                <button className="return-button" onClick={() => handleReturn(book.id)}>
                                    Zwróć
                                </button>
                                {!book.extendedTime && (
                                    <button className="extend-button" onClick={() => handleExtend(book.id)}>
                                        Przedłuż
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );



}; export default BookStatus;