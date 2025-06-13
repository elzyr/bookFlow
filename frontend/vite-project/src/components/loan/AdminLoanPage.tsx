import { useUser } from "../../context/UserContext.tsx";
import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../../utils/fetchWithRefresh.tsx";
import { LoanDto, LoanStatus } from "../../types/LoanDto.tsx";
import "../../css/AdminLoanPage.css";
import Notification from "../Notification.tsx";

const AdminLoanPage = () => {
  const { user, loading } = useUser();
  const [pendingLoans, setPendingLoans] = useState<LoanDto[]>([]);
  const [pendingReturns, setPendingReturns] = useState<LoanDto[]>([]);
  const [notification, setNotification] = useState<{ message: string; type?: "success" | "error" } | null>(null);

  const [loanPage, setLoanPage] = useState(0);
  const [loanTotalPages, setLoanTotalPages] = useState(0);
  const [returnPage, setReturnPage] = useState(0);
  const [returnTotalPages, setReturnTotalPages] = useState(0);

  const fetchPendingLoans = () => {
    fetchWithRefresh(`http://localhost:8080/loans?status=${LoanStatus.PENDING_LOAN}&page=${loanPage}&size=5`, {
      method: "GET"
    })
        .then(res => res.ok ? res.json() : null)
        .then(data => {
          if (data) {
            setPendingLoans(data.content);
            setLoanTotalPages(data.totalPages);
          }
        });
  };

  const fetchPendingReturns = () => {
    fetchWithRefresh(`http://localhost:8080/loans?status=${LoanStatus.PENDING_RETURN}&page=${returnPage}&size=5`, {
      method: "GET"
    })
        .then(res => res.ok ? res.json() : null)
        .then(data => {
          if (data) {
            setPendingReturns(data.content);
            setReturnTotalPages(data.totalPages);
          }
        });
  };

  useEffect(() => {
    fetchPendingLoans();
  }, [loanPage]);

  useEffect(() => {
    fetchPendingReturns();
  }, [returnPage]);

  if (!user || loading) {
    return (
        <div className="main-container">
          <p className="error-message">Użytkownik niezalogowany</p>
        </div>
    );
  }

  const handleAcceptLoan = async (loanId: number) => {
    try {
      const res = await fetchWithRefresh(`http://localhost:8080/loans/${loanId}/confirmLoan`, { method: "POST" });
      if (!res.ok) {
        const errText = await res.text();
        alert(`Błąd: ${errText}`);
      } else {
        setNotification({ message: `Wypożyczenie nr. ${loanId} zostało zaakceptowane`, type: "success" });
        fetchPendingLoans();
      }
    } catch (e) {
      console.error(e);
      setNotification({ message: "Błąd przy akceptacji wypożyczenia", type: "error" });
    }
  };

  const handleAcceptReturn = async (loanId: number) => {
    try {
      const res = await fetchWithRefresh(`http://localhost:8080/loans/${loanId}/confirmReturn`, { method: "POST" });
      if (!res.ok) {
        const errText = await res.text();
        alert(`Błąd: ${errText}`);
      } else {
        setNotification({ message: `Zwrot nr. ${loanId} został zaakceptowany`, type: "success" });
        fetchPendingReturns();
      }
    } catch (e) {
      console.error(e);
      setNotification({ message: "Błąd przy akceptacji zwrotu", type: "error" });
    }
  };

  const handleCancelLoan = async (loanId: number) => {
    try {
      const res = await fetchWithRefresh(`http://localhost:8080/loans/${loanId}/cancel`, { method: "POST" });
      if (!res.ok) {
        const errText = await res.text();
        alert(`Błąd: ${errText}`);
      } else {
        setNotification({ message: `Rezerwacja nr. ${loanId} została anulowana`, type: "success" });
        fetchPendingLoans();
      }
    } catch (e) {
      console.error(e);
      setNotification({ message: "Błąd przy anulowaniu rezerwacji", type: "error" });
    }
  };

  return (
      <div className="wrapper-loan">
        <div className="return-container-loan">
          <h2>Niezaakceptowane Wypożyczenia</h2>
          {pendingLoans.length === 0 ? (
              <p>Brak wypożyczeń do zatwierdzenia</p>
          ) : (
              <>
                <table className="loan-table">
                  <thead>
                  <tr>
                    <th>Lp.</th>
                    <th>Tytuł książki</th>
                    <th>Data wypożyczenia</th>
                    <th>Planowana data zwrotu</th>
                    <th>E-mail użytkownika</th>
                    <th>Akcja</th>
                  </tr>
                  </thead>
                  <tbody>
                  {pendingLoans.map((loan, idx) => (
                      <tr key={loan.id ?? idx}>
                        <td>{idx + 1 + loanPage * 5}</td>
                        <td>{loan.title}</td>
                        <td>{loan.borrowDate}</td>
                        <td>{loan.returnDate}</td>
                        <td>{loan.userEmail}</td>
                        <td>
                          <button className="acceptLoan-button" onClick={() => handleAcceptLoan(loan.id!)}>
                            Akceptuj wypożyczenie
                          </button>
                          <button className="cancelLoan-button" onClick={() => handleCancelLoan(loan.id!)}>
                            Anuluj wypożyczenie
                          </button>
                        </td>
                      </tr>
                  ))}
                  </tbody>
                </table>

                <div className="pagination-controls">
                  <button onClick={() => setLoanPage(p => Math.max(p - 1, 0))} disabled={loanPage === 0}>
                    Poprzednia
                  </button>
                  <span>Strona {loanPage + 1} z {loanTotalPages}</span>
                  <button onClick={() => setLoanPage(p => Math.min(p + 1, loanTotalPages - 1))} disabled={loanPage + 1 >= loanTotalPages}>
                    Następna
                  </button>
                </div>
              </>
          )}

          <h2 style={{ marginTop: "2rem" }}>Niezaakceptowane Zwroty</h2>
          {pendingReturns.length === 0 ? (
              <p>Brak zwrotów do zatwierdzenia</p>
          ) : (
              <>
                <table className="return-table">
                  <thead>
                  <tr>
                    <th>Lp.</th>
                    <th>Tytuł książki</th>
                    <th>Data faktycznego zwrotu</th>
                    <th>E-mail użytkownika</th>
                    <th>Akcja</th>
                  </tr>
                  </thead>
                  <tbody>
                  {pendingReturns.map((loan, idx) => (
                      <tr key={loan.id ?? idx}>
                        <td>{idx + 1 + returnPage * 5}</td>
                        <td>{loan.title}</td>
                        <td>{loan.returnDate}</td>
                        <td>{loan.userEmail}</td>
                        <td>
                          <button className="acceptReturn-button" onClick={() => handleAcceptReturn(loan.id!)}>
                            Akceptuj zwrot
                          </button>
                        </td>
                      </tr>
                  ))}
                  </tbody>
                </table>

                <div className="pagination-controls">
                  <button onClick={() => setReturnPage(p => Math.max(p - 1, 0))} disabled={returnPage === 0}>
                    Poprzednia
                  </button>
                  <span>Strona {returnPage + 1} z {returnTotalPages}</span>
                  <button onClick={() => setReturnPage(p => Math.min(p + 1, returnTotalPages - 1))} disabled={returnPage + 1 >= returnTotalPages}>
                    Następna
                  </button>
                </div>
              </>
          )}

          {notification && (
              <Notification
                  message={notification.message}
                  type={notification.type}
                  onClose={() => setNotification(null)}
              />
          )}
        </div>
      </div>
  );
};

export default AdminLoanPage;
