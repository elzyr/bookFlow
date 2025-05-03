import  {useEffect, useState} from "react";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";
import {useUser} from "../context/UserContext.tsx";
import '../css/BookStatus.css';


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

    useEffect(() => {
        if (!loading && user) {
            fetchLoan();
        }
    }, [user, loading]);


    const fetchLoan = () => {
        if(!user)return;
        fetchWithRefresh(`http://localhost:8080/loans/historyLoanActive`,{
            method: "GET"
        })
            .then(res => {
                return res.json();
            })
            .then(data => {
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
            });
    };


    const handleExtend = async (bookId : number) =>{
        if(!user || !bookId)return;
        const res = await fetchWithRefresh(`http://localhost:8080/loans/${bookId}/extendTime`,{
            method: "PUT"
        })
        if(res.ok){
            alert("Extended time successfully.");
        } else {
            const text: string = await res.text();
            alert(text);
        }
        fetchLoan();
    };


    const handleReturn = async (bookId : number) =>{
        if(!user || !bookId)return;
        const res = await fetchWithRefresh(`http://localhost:8080/loans/${bookId}/return`,{
            method: "PUT"
        })
        if(res.ok){
            alert("Book returned successfully");
        } else {
            const text: string = await res.text();
            alert(text);
        }
        fetchLoan();
    };



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
                                <button className="return-button" onClick={() => handleReturn(book.bookId)}>
                                    Zwróć
                                </button>
                                <button className="extend-button" onClick={() => handleExtend(book.bookId)}>
                                    Przedłuż
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );


};
export default BookStatus;