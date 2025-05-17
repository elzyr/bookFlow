import  {useEffect, useState} from "react";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";
import {useUser} from "../context/UserContext.tsx";
import '../css/BookStatus.css';
import Notification from "../components/Notification";

interface ReturnBook{
    id : number;
    bookId: number;
    title : string ;
    returnDate: Date ;
    extendedTime : boolean;
}

const BookStatus = () =>{
    const [bookNotReturned, setBookNotReturnedList] = useState<ReturnBook[]>();
    const {user, loading} = useUser();
    const [notification, setNotification] = useState<{ message: string; type?: "success" | "error" } | null>(null);

    useEffect(() => {
        if (!loading && user) {
            fetchLoan();
        }
    }, [user, loading]);


    const fetchLoan = () => {
        if(!user)return;
        fetchWithRefresh(`http://localhost:8080/loans/historyLoanActive`, {
            method: "GET"
        })
            .then(async res => {
                if (!res.ok) {
                    if (res.status !== 404) {
                        const errMsg = await res.text();
                        console.warn("Błąd pobierania książek:", errMsg);
                    }
                    setBookNotReturnedList([]);
                    return null;
                }
                return res.json();
            })
            .then(data => {
                if (!data) return;

                const converted = data.map((entry: any) => ({
                    id: entry.id,
                    bookId: entry.bookId,
                    title: entry.title,
                    borrowDate: entry.borrowDate ? new Date(entry.borrowDate) : undefined,
                    returnDate: entry.returnDate ? new Date(entry.returnDate) : undefined,
                    extendedTime: entry.extendedTime
                }));

                console.log("Wypożyczone książki:", converted);
                setBookNotReturnedList(converted);
            })
            .catch(err => {
                console.error("Nieoczekiwany błąd:", err);
                setBookNotReturnedList([]);
            });
    };


    const handleExtend = async (loanId : number) =>{
        if(!user || !loanId)return;
        const res = await fetchWithRefresh(`http://localhost:8080/loans/${loanId}/extendTime`,{
            method: "PUT"
        })
        if(res.ok){
            setNotification({ message: "Pomyślnie wydłużono czas oddania", type: "success" });
            setTimeout(() => {
                window.location.reload();
            }, 3000);
        } else {
            const text: string = await res.text();
            setNotification({ message: text, type: "error" });
        }
        fetchLoan();
    };


    const handleReturn = async (loanId : number) =>{
        if(!user || !loanId)return;
        const res = await fetchWithRefresh(`http://localhost:8080/loans/${loanId}/return`,{
            method: "PUT"
        })
        if(res.ok){
            setNotification({ message: "Pomyślnie zwrócono książkę", type: "success" });
            setTimeout(() => {
                window.location.reload();
            }, 3000);
        } else {
            const text: string = await res.text();
            setNotification({ message: text, type: "error" });
        }
        fetchLoan();
    };



    return (
        <div className="return-container-loan">
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
                                <button className="extend-button" onClick={() => handleExtend(book.id)}>
                                    Przedłuż
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
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
export default BookStatus;