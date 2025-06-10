import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../../utils/fetchWithRefresh.tsx";
import "../../css/LoanUserInfo.css";
import LoanTabs from "./LoanTabs.tsx";
import { useUser } from "../../context/UserContext.tsx";
import { LoanDto, LoanStatus } from "../../types/LoanDto.tsx";

const statusLabels: Record<LoanStatus, string> = {
  [LoanStatus.PENDING_LOAN]: "Oczekuje na wypożyczenie",
  [LoanStatus.LOAN_ACCEPTED]: "Wypożyczone",
  [LoanStatus.PENDING_RETURN]: "Oczekuje na akceptację zwrotu",
  [LoanStatus.RETURN_ACCEPTED]: "Zwrócone",
};

const LoanHistory = () => {
  const [loanedBook, setLoanedBook] = useState<LoanDto[]>([]);
  const [activeTab, setActiveTab] = useState<"current" | "returned">("current");
  const { user, loading } = useUser();

  useEffect(() => {
    if (!user?.id) return;

    (async () => {
      try {
        let loans: LoanDto[] = [];

        if (activeTab === "current") {
          const statuses = [LoanStatus.LOAN_ACCEPTED, LoanStatus.PENDING_RETURN];
          const results = await Promise.all(
            statuses.map((status) =>
              fetchWithRefresh(
                `http://localhost:8080/loans/myloans?status=${status}`,
                { method: "GET", credentials: "include" }
              ).then(async (res) => {
                if (!res.ok) {
                  if (res.status !== 404) console.warn(`Fetch error ${res.status} for status ${status}`);
                  return [];
                }
                const contentType = res.headers.get("content-type") || "";
                if (!contentType.includes("application/json")) {
                  console.error("Odpowiedź nie jest JSON-em:", await res.text());
                  return [];
                }
                return (await res.json()) as LoanDto[];
              })
            )
          );
          loans = results.flat();
        } else {
          const res = await fetchWithRefresh(
            `http://localhost:8080/loans/myloans?status=${LoanStatus.RETURN_ACCEPTED}`,
            { method: "GET", credentials: "include" }
          );
          if (!res.ok) {
            if (res.status !== 404) console.warn(`Fetch error ${res.status}`);
            loans = [];
          } else {
            const contentType = res.headers.get("content-type") || "";
            if (!contentType.includes("application/json")) {
              console.error("Odpowiedź nie jest JSON-em:", await res.text());
              loans = [];
            } else {
              loans = await res.json();
            }
          }
        }

        setLoanedBook(loans);
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

      {loanedBook.length === 0 ? (
        <div className="no-loans-message">
          {activeTab === "current" ? "Brak wypożyczonych książek" : "Brak zwróconych książek"}
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
                  <th>Status</th>
                  <th>Data faktycznego zwrotu</th>
                </tr>
              </thead>
              <tbody>
                {loanedBook.map((book, index) => {
                  const borrowDate = book.borrowDate ? new Date(book.borrowDate).toLocaleDateString() : "—";
                  const returnDate = book.returnDate ? new Date(book.returnDate).toLocaleDateString() : "—";
                  const actualReturn = book.bookReturned ? new Date(book.bookReturned).toLocaleDateString() : "Nie zwrócono";
                  const statusText = statusLabels[book.status] || book.status;

                  return (
                    <tr key={book.id ?? index}>
                      <td><strong>{index + 1}</strong></td>
                      <td><strong>{book.title}</strong></td>
                      <td>{borrowDate}</td>
                      <td><strong>{returnDate}</strong></td>
                      <td>{book.extendedTime ? "Tak" : "Nie"}</td>
                      <td><strong>{statusText}</strong></td>
                      <td>{actualReturn}</td>
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