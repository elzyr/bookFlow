import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../../utils/fetchWithRefresh.tsx";
import "../../css/LoanUserInfo.css";
import LoanTabs from "./LoanTabs.tsx";
import { useUser } from "../../context/UserContext.tsx";
import { LoanDto, LoanStatus } from "../../types/LoanDto.tsx";

const LoanHistory = () => {
  const [loanedBook, setLoanedBook] = useState<LoanDto[]>([]);
  const [activeTab, setActiveTab] = useState<"current" | "returned">("current");
  const { user, loading } = useUser();

  useEffect(() => {
    if (!user?.id) return;

    const endpoint =
      activeTab === "current"
        ? `http://localhost:8080/loans/myloans?status=${LoanStatus.LOAN_ACCEPTED}`
        : `http://localhost:8080/loans/myloans?status=${LoanStatus.RETURN_ACCEPTED}`;

    (async () => {
      try {
        const res = await fetchWithRefresh(endpoint, {
          method: "GET",
          credentials: "include",
        });

        if (!res.ok) {
          if (res.status !== 404) {
            console.warn(`Fetch error ${res.status}`);
          }
          setLoanedBook([]);
          return;
        }

        const contentType = res.headers.get("content-type") || "";
        if (!contentType.includes("application/json")) {
          console.error("Odpowiedź nie jest JSON-em:", await res.text());
          setLoanedBook([]);
          return;
        }

        // Odbieramy tablicę LoanDto (pola typu string dla dat)
        const data: LoanDto[] = await res.json();
        setLoanedBook(data);
        console.log("Wypożyczone książki:", data);
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
                  <th>Data zwrotu (planowana)</th>
                  <th>Przedłużone</th>
                  <th>Oddane</th>
                  <th>Data faktycznego zwrotu</th>
                </tr>
              </thead>
              <tbody>
                {loanedBook.map((book, index) => {
                  const borrowDateStr = book.borrowDate
                    ? new Date(book.borrowDate).toLocaleDateString()
                    : "—";
                  const returnDateStr = book.returnDate
                    ? new Date(book.returnDate).toLocaleDateString()
                    : "—";
                  const actualReturnStr = book.bookReturned
                    ? new Date(book.bookReturned).toLocaleDateString()
                    : "Nie zwrócono";

                  const isReturned = Boolean(book.bookReturned);

                  return (
                    <tr key={book.id ?? index}>
                      <td>
                        <strong>{index + 1}</strong>
                      </td>
                      <td>
                        <strong>{book.title}</strong>
                      </td>
                      <td>{borrowDateStr}</td>
                      <td>
                        <strong>{returnDateStr}</strong>
                      </td>
                      <td>{book.extendedTime ? "Tak" : "Nie"}</td>
                      <td>
                        <strong>{isReturned ? "Tak" : "Nie"}</strong>
                      </td>
                      <td>{actualReturnStr}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </>
  );
};

export default LoanHistory;
