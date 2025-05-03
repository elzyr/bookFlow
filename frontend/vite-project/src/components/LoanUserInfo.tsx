import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../utils/fetchWithRefresh.tsx";
import "../css/LoanUserInfo.css";
import LoanTabs from "./LoanTabs.tsx";
import { useUser } from "../context/UserContext.tsx";

interface LoanDto {
  title?: string;
  borrowDate?: Date;
  returnDate?: Date;
  extendedTime?: boolean;
  returned?: boolean;
  bookReturned?: Date;
}

const LoanUserInfo = () => {
  const [loanedBook, setLoanedBook] = useState<LoanDto[]>();
  const [activeTab, setActiveTab] = useState<"current" | "returned">("current");
  const { user, loading } = useUser();

  useEffect(() => {
    if (!user?.id) return;

    const endpoint =
      activeTab === "current"
        ? `http://localhost:8080/loans/historyLoanActive`
        : `http://localhost:8080/loans/historyLoanReturned`;

    (async () => {
      try {
        const res = await fetchWithRefresh(endpoint, {
          method: "GET",
          credentials: "include"
        });

        if (!res.ok) {
          console.warn(`Fetch error ${res.status}`);
          // tu możesz rzucić albo ustawić pustą tablicę, żeby pokazać komunikat "Brak..."
          setLoanedBook([]);
          return;
        }

        // upewniamy się, że to JSON
        const contentType = res.headers.get("content-type") || "";
        if (!contentType.includes("application/json")) {
          console.error("Odpowiedź nie jest JSON-em:", await res.text());
          setLoanedBook([]);
          return;
        }

        const data = await res.json();
        const converted: LoanDto[] = data.map((book: any) => ({
          ...book,
          borrowDate: book.borrowDate ? new Date(book.borrowDate) : undefined,
          returnDate: book.returnDate ? new Date(book.returnDate) : undefined,
          bookReturned: book.bookReturned ? new Date(book.bookReturned) : undefined
        }));

        setLoanedBook(converted);
        console.log("Wypożyczone książki:", converted);
      } catch (err) {
        console.error("Błąd parsowania lub sieci:", err);
        setLoanedBook([]);
      }
    })();
  }, [activeTab, user]);

  if (!user || loading) {
    return <p>Ładowanie danych użytkownika...</p>;
  }

  return (
    <>
      <LoanTabs activeTab={activeTab} onTabChange={setActiveTab} />
      {!loanedBook || loanedBook.length === 0 ? (
        <div className="no-loans-message">
          {activeTab === "current"
            ? "Brak wypożyczonych książek"
            : "Brak zwróconych książek"}
        </div>
      ) : (
        <div className="loan-wrapper">
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
                    <td>{book.bookReturned
                      ? book.bookReturned.toLocaleDateString()
                      : "Nie zwrócono"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </>
  );
};

export default LoanUserInfo;
