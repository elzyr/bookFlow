import  {useEffect, useState} from "react";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";
import "../css/LoanUserInfo.css"
import LoanTabs from "./LoanTabs.tsx";
import {useUser} from "../context/UserContext.tsx";

class LoanDto {
    title:  string | undefined;
    borrowDate: Date | undefined;
    returnDate : Date | undefined;
    extendedTime: boolean | undefined;
    returned : boolean | undefined;
    bookReturned: Date | undefined;
}

const LoanUserInfo = () => {
    const [loanedBook,setLoanedBook] = useState<LoanDto[]>();
    const [activeTab, setActiveTab] = useState<"current" | "returned">("current");
    const {user, loading} = useUser();


    useEffect(() => {
        if (!user || !user.id) return;
        const endpoint =
            activeTab === "current"
                ? `http://localhost:8080/loan/historyLoanActive?username=${user.username}`
                : `http://localhost:8080/loan/historyLoanReturned?username=${user.username}`;

        fetchWithRefresh(endpoint, {
            method: "GET",
            credentials: "include"
        })
            .then( res   => {
                if (!res.ok) {
                  console.log(res.status);
                }
                return res.json();
            })
            .then(data => {
                    const converted = data.map((book: any) => ({
                        ...book,
                        borrowDate: new Date(book.borrowDate),
                        returnDate: new Date(book.returnDate),
                        bookReturned: book.bookReturned ? new Date(book.bookReturned) : undefined
                    }));
                    setLoanedBook(converted);
                    console.log("Wypożyczone książki:", converted);
            });
    }, [activeTab,user]);

    if (!user || loading) {
        return <p>Ładowanie danych użytkownika...</p>;
    }

    if (!loanedBook || loanedBook.length === 0) {
        return (
            <>
                <LoanTabs activeTab={activeTab} onTabChange={setActiveTab} />
                <div className="no-loans-message">
                    {activeTab === "current"
                        ? "Brak wypożyczonych książek"
                        : "Brak zwróconych książek"}
                </div>
            </>
        );
    }


    return (
        <div className="loan-wrapper">
            <LoanTabs activeTab={activeTab} onTabChange={setActiveTab}/>
            <div className="loan-table-container">
                <table className="loan-table">
                    <thead>
                    <tr>
                        <th></th>
                        <th>Tytuł</th>
                        <th>Data wypożyczenia</th>
                        <th>Data zwrotu</th>
                        <th>Przedłużone</th>
                        <th>Oddane</th>
                        <th>Data zwrotu do biblioteki</th>
                    </tr>
                    </thead>
                    <tbody>
                    {loanedBook.map((book, index) => (
                        <tr key={index}>
                            <td><strong>{index + 1}</strong></td>
                            <td><strong>{book.title}</strong></td>
                            <td>{book.borrowDate?.toLocaleDateString()}</td>
                            <td><strong>{book.returnDate?.toLocaleDateString()}</strong></td>
                            <td>{book.extendedTime ? "Tak" : "Nie"}</td>
                            <td><strong>{book.returned ? "Tak" : "Nie"}</strong></td>
                            <td>{book.bookReturned ? book.bookReturned.toLocaleDateString() : "Nie zwrócono"}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );

};
export default LoanUserInfo;