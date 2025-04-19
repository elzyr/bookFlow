import  {useEffect, useState} from "react";
import {fetchWithRefresh} from "../utils/fetchWithRefresh.tsx";
import "../css/LoanUserInfo.css"
import LoanTabs from "./LoanTabs.tsx";

class LoanDto {
    title:  string | undefined;
    borrowDate: Date | undefined;
    returnDate : Date | undefined;
    extendedTime: boolean | undefined;
    returned : boolean | undefined;
}

const LoanUserInfo = () => {
    const [loanedBook,setLoanedBook] = useState<LoanDto[]>();
    const [activeTab, setActiveTab] = useState<"current" | "returned">("current");
    const [user, setUser] = useState<any>(null);

    useEffect(() => {
        fetchWithRefresh("http://localhost:8080/info/me", {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(data => setUser(data));
    }, []);

    useEffect(() => {
        if (!user || !user.id) return;
        const endpoint =
            activeTab === "current"
                ? `http://localhost:8080/loan/historyLoanActive?userId=${user.id}`
                : `http://localhost:8080/loan/historyLoanReturned?userId=${user.id}`;

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
                        returnDate: new Date(book.returnDate)
                    }));
                    setLoanedBook(converted);
                    console.log("Wypożyczone książki:", converted);
            });
    }, [activeTab,user]);

    if (!user) {
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
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );

};
export default LoanUserInfo;